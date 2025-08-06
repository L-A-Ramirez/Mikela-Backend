package comercio.negocio.security.controller;

import comercio.negocio.management.entities.Negocio;
import comercio.negocio.security.dto.JwtDto;
import comercio.negocio.security.dto.LoginUsuario;
import comercio.negocio.security.dto.NuevoUsuario;
import comercio.negocio.security.entity.Rol;
import comercio.negocio.security.entity.Usuario;
import comercio.negocio.security.enums.RolNombre;
import comercio.negocio.security.jwt.JwtProvider;
import comercio.negocio.security.service.EmailService;
import comercio.negocio.security.service.RolService;
import comercio.negocio.security.service.UsuarioService;
import comercio.negocio.management.service.negocio.NegocioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import mi.porfolio.security.controller.Mensaje;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    Logger logger;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NegocioService negocioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailService emailService;


    @PostMapping("/create")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Campos mal ingresados o email inválido"), HttpStatus.BAD_REQUEST);
        }

        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario())) {
            return new ResponseEntity<>(new Mensaje("Ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);
        }

        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return new ResponseEntity<>(new Mensaje("Ese email ya está registrado"), HttpStatus.BAD_REQUEST);
        }

        Negocio negocio = nuevoUsuario.getNegocio();
        if (negocio == null || negocio.getId() == null) {
            throw new RuntimeException("Error: El negocio no fue proporcionado o es inválido.");
        }

        // Verifica que el negocio exista en la base de datos
        negocioService.getById(negocio.getId()).orElseThrow(
                () -> new RuntimeException("Error: El negocio no fue encontrado.")
        );

        // Crear el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nuevoUsuario.getNombreUsuario());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        usuario.setActivo(true); // El usuario está activo al crearse
        usuario.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        usuario.setNegocio(nuevoUsuario.getNegocio()); // Asignar el negocio al usuario

        // Asignación de roles
        Set<Rol> roles = new HashSet<>();
        // Rol por defecto
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).orElseThrow(
                () -> new RuntimeException("Error: El rol USER no fue encontrado.")
        ));

        // Si el usuario solicita rol "ROLE_ADMIN", se asigna también ese rol
        if (nuevoUsuario.getRoles() != null && nuevoUsuario.getRoles().contains("ROLE_ADMIN")) {
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).orElseThrow(
                    () -> new RuntimeException("Error: El rol ADMIN no fue encontrado.")
            ));
        }

        // Asignar los roles al usuario
        usuario.setRoles(roles);

        // Guardar el usuario
        usuarioService.saveUser(usuario);

        return new ResponseEntity<>(new Mensaje("Usuario guardado con éxito"), HttpStatus.CREATED);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errores = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(new Mensaje("Errores: " + errores), HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUsuario.getNombreUsuario(),
                            loginUsuario.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Obtener el usuario desde la base de datos
            Usuario usuario = usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener negocioId desde el usuario
            Long negocioId = usuario.getNegocio().getId();

            // Generar el token
            Long userId = Long.valueOf(usuario.getId());
            String jwt = jwtProvider.generateToken(authentication.getName(), negocioId, userId);

            // Crear y devolver el DTO con el token y datos del usuario
            JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
            return new ResponseEntity<>(jwtDto, HttpStatus.OK);

        } catch (AuthenticationException e) {
            logger.error("Error de autenticación: ", e);
            return new ResponseEntity<>(new Mensaje("Credenciales inválidas"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la traza completa
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al procesar la venta: " + e.getMessage());
        }

    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetearContrasena(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String nuevaPassword = request.get("password");

        Usuario usuario = usuarioService.getByTokenRecuperacion(token)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Token inválido"));
        }


        if (usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("El token ha expirado"));
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenExpiracion(null);
        usuarioService.saveUser(usuario);

        return new ResponseEntity<>(new Mensaje("Contraseña restablecida correctamente"), HttpStatus.OK);
    }

    @GetMapping("/password-reset/validate")
    public ResponseEntity<?> validarTokenRecuperacion(@RequestParam("token") String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("Token inválido"));
        }

        Optional<Usuario> usuarioOpt = usuarioService.getByTokenRecuperacion(token);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new Mensaje("Token inválido"));
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getTokenExpiracion() == null || usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new Mensaje("El token ha expirado"));
        }

        return ResponseEntity.ok(new Mensaje("Token válido"));
    }


    @PostMapping("/password-reset-request")
    public ResponseEntity<?> solicitarResetPassword(@RequestBody Map<String, String> request) {
        String emailOrUsername = request.get("emailOrUsername");

        Optional<Usuario> usuarioOpt = usuarioService.getByEmailOrNombreUsuario(emailOrUsername);
        if (!usuarioOpt.isPresent()) {
            // No informar si existe o no
            return ResponseEntity.ok(new Mensaje("Si el usuario existe, se ha enviado un email"));
        }

        Usuario usuario = usuarioOpt.get();

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusMinutes(30));
        usuarioService.saveUser(usuario);

        String resetLink = "http://localhost:4200/resetear-contrasena?token=" + token;
        emailService.enviarEmail(usuario.getEmail(), "Recuperación de contraseña",
                "Hacé clic en el siguiente enlace para recuperar tu contraseña: " + resetLink);

        return ResponseEntity.ok(new Mensaje("Si el usuario existe, se ha enviado un email"));
    }

}
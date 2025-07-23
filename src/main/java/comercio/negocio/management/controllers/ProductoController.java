package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.Negocio;
import comercio.negocio.management.entities.Producto;
import comercio.negocio.management.service.producto.I_ProductoService;
import comercio.negocio.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    private I_ProductoService service;

    @Autowired
    private JwtProvider jwtProvider;

    private Long extractNegocioId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }
        return jwtProvider.getNegocioIdFromToken(token.replace("Bearer ", ""));
    }

    @GetMapping("/listas")
    public List<Producto> getAllPublicos() {
        return service.getAllProductos(); // productos públicos
    }

    @GetMapping("/lista")
    public List<Producto> getAll(@RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        return service.getByNegocioId(negocioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<Producto> producto = service.getByIdAndNegocioId(id, negocioId);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> save(@RequestBody Producto producto, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Negocio negocio = new Negocio();
        negocio.setId(negocioId);
        producto.setNegocio(negocio);

        service.save(producto);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Producto registrado correctamente.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> update(@RequestBody Producto producto, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        if (!producto.getNegocio().getId().equals(negocioId)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No puedes modificar productos de otro negocio.");
            return ResponseEntity.status(403).body(error);
        }

        service.save(producto);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Producto actualizado correctamente.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<Producto> producto = service.getByIdAndNegocioId(id, negocioId);

        if (producto.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        service.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                           @RequestBody Map<String, Boolean> body,
                                           Authentication authentication) {
        String username = authentication.getName();

        Optional<Producto> productoOpt = service.getById(id, username);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Boolean activo = body.get("activo");
        if (activo == null) {
            return ResponseEntity.badRequest().body("Falta el campo 'activo'");
        }

        Producto producto = productoOpt.get();
        producto.setActivo(activo);
        service.save(producto);

        return ResponseEntity.ok().build();
    }




}

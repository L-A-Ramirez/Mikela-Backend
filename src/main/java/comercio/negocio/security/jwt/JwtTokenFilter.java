package comercio.negocio.security.jwt;

import comercio.negocio.security.service.UserDetailsImplements;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsImplements userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);

            if (token == null || token.isEmpty()) {
                logger.warn("Token no proporcionado en la solicitud");
                filterChain.doFilter(request, response);
                return; // Salida temprana si no hay token
            }

            if (jwtProvider.validateToken(token)) {
                String nombreUsuario = jwtProvider.getNombreUsuarioFromToken(token);
                if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                    logger.warn("El token no contiene un nombre de usuario válido");
                    filterChain.doFilter(request, response);
                    return; // Salida temprana si no hay nombre de usuario
                }

                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(nombreUsuario);
                } catch (UsernameNotFoundException e) {
                    logger.warn("El usuario no se encontró: " + nombreUsuario);
                    filterChain.doFilter(request, response);
                    return; // Salida temprana si el usuario no existe
                }

                if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                    logger.warn("El usuario no está habilitado o está bloqueado: " + nombreUsuario);
                    filterChain.doFilter(request, response);
                    return; // Salida temprana si el usuario está bloqueado o deshabilitado
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (UsernameNotFoundException e) {
            logger.warn("El usuario no fue encontrado", e);
        } catch (JwtException e) {
            logger.warn("El token JWT no es válido", e);
        } catch (Exception e) {
            logger.error("Error inesperado en el método doFilterInternal", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) {
            logger.warn("Encabezado Authorization no presente en la solicitud. URL: {}", request.getRequestURI());
        } else if (!header.startsWith("Bearer ")) {
            logger.warn("El formato del encabezado Authorization es incorrecto: {}", header);
        }
        return header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
    }
}

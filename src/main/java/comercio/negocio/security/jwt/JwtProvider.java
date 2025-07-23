package comercio.negocio.security.jwt;


import io.jsonwebtoken.*;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(String nombreUsuario, Long negocioId, Long userId) {
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            logger.error("El nombre de usuario es nulo o vacío. No se puede generar el token.");
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío.");
        }

        if (negocioId == null) {
            logger.error("El negocioId es nulo. No se puede generar el token.");
            throw new IllegalArgumentException("El negocioId no puede ser nulo.");
        }

        if (userId == null) {
            logger.error("El userId es nulo. No se puede generar el token.");
            throw new IllegalArgumentException("El userId no puede ser nulo.");
        }

        logger.info("Generando token para usuario: {}, negocioId: {}, userId: {}", nombreUsuario, negocioId, userId);

        return Jwts.builder()
                .setSubject(nombreUsuario)
                .claim("negocioId", negocioId)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // Método para obtener el nombre de usuario desde el token
    public String getNombreUsuarioFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Método para obtener negocioId desde el token
    public Long getNegocioIdFromToken(String token) {
        try {
            Long negocioId = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("negocioId", Long.class);

            if (negocioId == null) {
                logger.warn("El token no contiene un negocioId");
                throw new JwtException("El token no contiene un negocioId válido");
            }

            return negocioId;
        } catch (JwtException e) {
            logger.error("Error al extraer negocioId del token: {}", e.getMessage());
            throw e;
        }
    }

    // Validar el token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token malformado: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Firma inválida: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("El token está vacío o es nulo: {}", e.getMessage());
        }
        return false;
    }

    // Método para obtener userId desde el token
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            Object userIdObj = claims.get("userId");

            if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue(); // En caso de ser Integer
            } else if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else {
                throw new JwtException("El token no contiene un userId válido");
            }
        } catch (JwtException e) {
            logger.error("Error al extraer userId del token: {}", e.getMessage());
            throw e;
        }
    }

}

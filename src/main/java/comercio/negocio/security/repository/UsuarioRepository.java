package comercio.negocio.security.repository;


import comercio.negocio.security.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);

    Optional<Usuario> findByEmailOrNombreUsuario(String email, String nombreUsuario);

}
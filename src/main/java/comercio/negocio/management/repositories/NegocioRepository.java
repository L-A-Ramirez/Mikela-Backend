package comercio.negocio.management.repositories;

import comercio.negocio.management.entities.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Long> {
    boolean existsByNombre(String nombre);
}

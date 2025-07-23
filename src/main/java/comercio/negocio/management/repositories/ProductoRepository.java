package comercio.negocio.management.repositories;

import comercio.negocio.management.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue(); // Para obtener solo productos activos

    List<Producto> findByNegocioId(Long negocioId);

    Optional<Producto> findByIdAndNegocioId(Long id, Long negocioId);
}


package comercio.negocio.management.repositories;

import comercio.negocio.management.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByMail(String mail); // Para buscar clientes por correo

    List<Cliente> findByNegocioId(Long negocioId);
    Optional<Cliente> findByIdAndNegocioId(Long id, Long negocioId);
}

package comercio.negocio.management.service.cliente;

import comercio.negocio.management.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface I_ClienteService {
    List<Cliente> getAll(Long negocioId);
    Optional<Cliente> getByIdAndNegocioId(Long id, Long negocioId);
    void save(Cliente cliente);
    void remove(Long id);
    List<Cliente> getClientesByUsername(String username);
}
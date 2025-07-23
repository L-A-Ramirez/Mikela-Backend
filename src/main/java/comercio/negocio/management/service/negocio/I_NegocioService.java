package comercio.negocio.management.service.negocio;

import comercio.negocio.management.entities.Negocio;

import java.util.List;
import java.util.Optional;

public interface I_NegocioService {
    List<Negocio> getAll();
    Negocio save(Negocio negocio);
    void remove(Long id);
    Optional<Negocio> getById(Long id);
}
package comercio.negocio.management.service.auditoria;


import comercio.negocio.management.entities.Auditoria;

import java.util.List;
import java.util.Optional;

public interface I_AuditoriaService {
    List<Auditoria> getAll();
    void save(Auditoria auditoria);
    void remove(Long id);
    Optional<Auditoria> getById(Long id);
}

package comercio.negocio.management.service.auditoria;

import comercio.negocio.management.entities.Auditoria;
import comercio.negocio.management.repositories.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaService implements I_AuditoriaService{

    @Autowired
    private AuditoriaRepository repository;

    @Override
    public List<Auditoria> getAll() {
        return (List<Auditoria>) repository.findAll();
    }

    @Override
    public void save(Auditoria auditoria) {
        repository.save(auditoria);
    }

    @Override
    public void remove(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Auditor con id " + id + " no existe.");
        }
    }

    @Override
    public Optional<Auditoria> getById(Long id) {
        return repository.findById(id);
    }
}

package comercio.negocio.management.service.negocio;

import comercio.negocio.management.entities.Negocio;
import comercio.negocio.management.repositories.NegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NegocioService implements I_NegocioService {

    @Autowired
    private NegocioRepository repository;

    @Override
    public List<Negocio> getAll() {
        return (List<Negocio>) repository.findAll();
    }

    @Override
    public Negocio save(Negocio negocio) {
        repository.save(negocio);
        return negocio;
    }

    @Override
    public void remove(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("El negocio con id " + id + " no existe.");
        }
    }

    @Override
    public Optional<Negocio> getById(Long id) {
        return repository.findById(id);
    }
}

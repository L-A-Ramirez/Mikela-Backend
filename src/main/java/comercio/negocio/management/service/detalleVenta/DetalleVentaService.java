package comercio.negocio.management.service.detalleVenta;

import comercio.negocio.management.entities.DetalleVenta;
import comercio.negocio.management.repositories.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleVentaService implements I_DetalleVentaService{

    @Autowired
    private DetalleVentaRepository repository;

    @Override
    public List<DetalleVenta> getAll() {
        return (List<DetalleVenta>) repository.findAll();
    }

    @Override
    public void save(DetalleVenta detalleVenta) {
        repository.save(detalleVenta);
    }

    @Override
    public void remove(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("El producto con id " + id + " no existe.");
        }
    }

    @Override
    public Optional<DetalleVenta> getById(Long id) {
        return repository.findById(id);
    }
}

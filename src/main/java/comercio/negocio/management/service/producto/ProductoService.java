package comercio.negocio.management.service.producto;

import comercio.negocio.management.entities.Producto;
import comercio.negocio.management.repositories.ProductoRepository;
import comercio.negocio.security.entity.Usuario;
import comercio.negocio.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService implements I_ProductoService {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Producto> getProductosByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuario(username);
        if (usuario.isEmpty() || usuario.get().getNegocio() == null) {
            throw new IllegalArgumentException("Usuario o negocio no encontrado");
        }

        return repository.findByNegocioId(usuario.get().getNegocio().getId());
    }

    @Override
    public List<Producto> getAllProductos() {
        return repository.findAll();
    }

    @Override
    public List<Producto> getByNegocioId(Long negocioId) {
        return repository.findByNegocioId(negocioId);
    }

    @Override
    public Optional<Producto> getByIdAndNegocioId(Long id, Long negocioId) {
        return repository.findByIdAndNegocioId(id, negocioId);
    }

    @Override
    public void save(Producto producto) {
        repository.save(producto);
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
    public Optional<Producto> getById(Long id, String username) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuario(username);
        if (usuario.isEmpty() || usuario.get().getNegocio() == null) {
            throw new IllegalArgumentException("Usuario o negocio no encontrado");
        }

        Optional<Producto> producto = repository.findById(id);
        if (producto.isPresent() && producto.get().getNegocio().getId().equals(usuario.get().getNegocio().getId())) {
            return producto;
        }

        throw new IllegalArgumentException("Producto no encontrado o no pertenece al negocio del usuario");
    }
}

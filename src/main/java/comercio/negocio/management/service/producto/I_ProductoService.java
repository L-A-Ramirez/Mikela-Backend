package comercio.negocio.management.service.producto;

import comercio.negocio.management.entities.Producto;

import java.util.List;
import java.util.Optional;

public interface I_ProductoService {
    List<Producto> getProductosByUsername(String username);
    List<Producto> getAllProductos();

    List<Producto> getByNegocioId(Long negocioId);
    Optional<Producto> getByIdAndNegocioId(Long id, Long negocioId);

    void save(Producto producto);
    void remove(Long id);
    Optional<Producto> getById(Long id, String username);
}
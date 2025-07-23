package comercio.negocio.management.service.detalleVenta;


import comercio.negocio.management.entities.DetalleVenta;

import java.util.List;
import java.util.Optional;

public interface I_DetalleVentaService {
    List<DetalleVenta> getAll();
    void save(DetalleVenta detalleVenta);
    void remove(Long id);
    Optional<DetalleVenta> getById(Long id);
}

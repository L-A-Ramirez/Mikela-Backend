package comercio.negocio.management.entities.dto;

import comercio.negocio.management.entities.Producto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDTO {
    private String nombre;

    public ProductoDTO(Producto producto) {
        this.nombre = producto.getNombre();
    }
}

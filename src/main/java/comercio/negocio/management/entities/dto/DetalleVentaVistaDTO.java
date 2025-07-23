package comercio.negocio.management.entities.dto;

import comercio.negocio.management.entities.DetalleVenta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaVistaDTO {
    private ProductoDTO producto;
    private Double cantidad;
    private Double precioUnitario;

    public DetalleVentaVistaDTO(DetalleVenta detalle) {
        this.producto = new ProductoDTO(detalle.getProducto());
        this.cantidad = detalle.getCantidad();
        this.precioUnitario = detalle.getPrecioUnitario();
    }
}

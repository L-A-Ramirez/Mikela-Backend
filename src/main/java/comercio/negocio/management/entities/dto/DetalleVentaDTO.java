package comercio.negocio.management.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaDTO {
    private Long id;
    private Double cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private SimpleProductoDTO producto;
    private SimpleVentaDTO venta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public SimpleProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(SimpleProductoDTO producto) {
        this.producto = producto;
    }

    public SimpleVentaDTO getVenta() {
        return venta;
    }

    public void setVenta(SimpleVentaDTO venta) {
        this.venta = venta;
    }
}

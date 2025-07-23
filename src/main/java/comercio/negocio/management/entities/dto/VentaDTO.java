package comercio.negocio.management.entities.dto;

import comercio.negocio.management.entities.Venta;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class VentaDTO {
    private String fecha;
    private String pago;
    private Double total;
    private Object cliente; // Podés tipar mejor si tenés un ClienteDTO
    private List<DetalleVentaVistaDTO> detalles;

    public VentaDTO(Venta venta) {
        this.fecha = venta.getFecha().toString();
        this.pago = venta.getPago().toString();
        this.total = venta.getTotal();
        this.cliente = venta.getCliente();
        this.detalles = venta.getDetalles().stream()
                .map(DetalleVentaVistaDTO::new)
                .collect(Collectors.toList());
    }
}

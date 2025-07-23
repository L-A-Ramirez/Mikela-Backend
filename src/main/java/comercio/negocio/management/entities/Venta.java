package comercio.negocio.management.entities;

import comercio.negocio.management.enums.Pago;
import comercio.negocio.security.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.processing.SQL;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter
@Setter
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Timestamp fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(nullable = true, name = "cliente_id")
    private Cliente cliente;

    @Column(name = "total", insertable = false, updatable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Double getTotal() {
        return total;
    }

    public Pago getPago() {
        return pago;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}

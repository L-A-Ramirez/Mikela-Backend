package comercio.negocio.management.entities;

import comercio.negocio.security.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "auditorias")
@Getter
@Setter
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @Column(nullable = false)
    private Timestamp fecha;

    @Column(columnDefinition = "TEXT")
    private String detalle;
}
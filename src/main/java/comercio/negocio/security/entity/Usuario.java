package comercio.negocio.security.entity;

import comercio.negocio.management.entities.Negocio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombreUsuario; // Campo correcto

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private Boolean activo;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "negocio_id", nullable = true)
    private Negocio negocio;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )

    private Set<Rol> roles;

    public Set<Rol> getRoles() {
        return roles;
    }

    public Integer getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }
}
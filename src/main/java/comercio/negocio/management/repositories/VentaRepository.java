package comercio.negocio.management.repositories;

import comercio.negocio.management.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findAllByNegocioId(Long negocioId);

    @Query("SELECT v FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.negocio.id = :negocioId ORDER BY v.fecha DESC")
    List<Venta> findByFechaAndNegocioId(
            @Param("inicio") Timestamp inicio,
            @Param("fin") Timestamp fin,
            @Param("negocioId") Long negocioId
    );


    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta AND v.negocio.id = :negocioId")
    List<Venta> findByFechaBetweenAndNegocioId(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("negocioId") Long negocioId
    );

}

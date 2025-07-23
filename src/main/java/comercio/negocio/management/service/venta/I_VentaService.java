package comercio.negocio.management.service.venta;


import comercio.negocio.management.entities.Venta;
import comercio.negocio.management.entities.dto.DetalleVentaDTO;
import comercio.negocio.management.entities.dto.VentaDTO;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface I_VentaService {
    List<Venta> getAllByNegocioId(Long negocioId);
    Optional<Venta> getById(Long id);
    Venta save(Venta venta);
    void remove(Long id);
    ByteArrayOutputStream generarCierreDelDiaPDF(LocalDate fecha, Long negocioId) throws Exception;
    ByteArrayOutputStream generarCierreDelMesPDF(YearMonth mes, Long negocioId) throws Exception;
    ByteArrayOutputStream generarCierreDelAnioPDF(int anio, Long negocioId) throws Exception;
    List<VentaDTO> getVentasDetalladasPorFecha(LocalDate fecha, Long negocioId);

    Optional<VentaDTO> getVentaDetalladaPorId(Long id, Long negocioId);
}

package comercio.negocio.management.service.venta;

import comercio.negocio.management.entities.DetalleVenta;
import comercio.negocio.management.entities.Venta;
import comercio.negocio.management.entities.dto.DetalleVentaDTO;
import comercio.negocio.management.entities.dto.VentaDTO;
import comercio.negocio.management.repositories.VentaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService implements I_VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public List<Venta> getAllByNegocioId(Long negocioId) {
        return ventaRepository.findAllByNegocioId(negocioId);
    }

    @Override
    public Optional<Venta> getById(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta save(Venta venta) {
        ventaRepository.save(venta);
        return venta;
    }

    @Override
    public void remove(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public ByteArrayOutputStream generarCierreDelDiaPDF(@NotNull LocalDate fecha, Long negocioId) throws Exception {
        Timestamp inicio = Timestamp.valueOf(fecha.atStartOfDay());
        Timestamp fin = Timestamp.valueOf(fecha.plusDays(1).atStartOfDay());

        List<Venta> ventas = ventaRepository.findByFechaAndNegocioId(inicio, fin, negocioId);
        double total = ventas.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0).sum();

        Map<String, List<Venta>> ventasPorCliente = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getCliente() != null ? v.getCliente().getNombre() : "N/A"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        Paragraph titulo = new Paragraph("🧾 Cierre de Caja - " + fecha);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        document.add(titulo);
        document.add(new Paragraph(" "));

        // Total general
        Paragraph totalParrafo = new Paragraph(String.format("💵 Total de ventas: $%.2f", total));
        totalParrafo.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalParrafo);
        document.add(new Paragraph(" "));

        for (Map.Entry<String, List<Venta>> entry : ventasPorCliente.entrySet()) {
            String clienteNombre = entry.getKey();
            List<Venta> ventasCliente = entry.getValue();
            double subtotalCliente = 0.0;

            document.add(new Paragraph("👤 Cliente: " + clienteNombre));
            document.add(new Paragraph(" "));

            for (Venta v : ventasCliente) {
                String fechaVenta = v.getFecha() != null ? v.getFecha().toString() : "Sin fecha";
                String formaPago = v.getPago() != null ? v.getPago().toString() : "No especificada";

                if (v.getDetalles() != null && !v.getDetalles().isEmpty()) {
                    for (DetalleVenta detalle : v.getDetalles()) {
                        String producto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto N/A";
                        double precio = detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0.0;
                        double cantidad = detalle.getCantidad();
                        double subtotal = precio * cantidad;
                        subtotalCliente += subtotal;

                        document.add(new Paragraph(
                                "- " + producto +
                                        " | Precio: $" + String.format("%.2f", precio) +
                                        " | Cantidad: " + cantidad +
                                        " | Subtotal: $" + String.format("%.2f", subtotal)
                        ));
                    }
                } else {
                    document.add(new Paragraph("- Sin productos"));
                }

                document.add(new Paragraph("🕒 Fecha: " + fechaVenta + " | 💳 Pago: " + formaPago));
                document.add(new Paragraph("--------------------------------------------------"));
            }

            document.add(new Paragraph("💰 Subtotal: $" + String.format("%.2f", subtotalCliente)));
            document.add(new Paragraph("=================================================="));
            document.add(new Paragraph(" "));
        }

        document.close();
        return out;
    }

    @Override
    public ByteArrayOutputStream generarCierreDelMesPDF(YearMonth mes, Long negocioId) throws Exception {
        LocalDate desde = mes.atDay(1);
        LocalDate hasta = mes.atEndOfMonth();

        List<Venta> ventas = ventaRepository.findByFechaBetweenAndNegocioId(
                desde.atStartOfDay(), hasta.atTime(23, 59, 59), negocioId
        );

        double totalGeneral = ventas.stream()
                .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0)
                .sum();

        // Agrupar ventas por cliente
        Map<String, List<Venta>> ventasPorCliente = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getCliente() != null ? v.getCliente().getNombre() : "Cliente N/A"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Cierre de Caja - Mes: " + mes));
        document.add(new Paragraph("Total ventas del mes: $" + totalGeneral));
        document.add(new Paragraph(" "));

        for (Map.Entry<String, List<Venta>> entry : ventasPorCliente.entrySet()) {
            String clienteNombre = entry.getKey();
            List<Venta> ventasCliente = entry.getValue();

            document.add(new Paragraph("Cliente: " + clienteNombre));

            double totalCliente = ventasCliente.stream()
                    .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0)
                    .sum();

            document.add(new Paragraph("Total del cliente: $" + totalCliente));
            document.add(new Paragraph("------------------------------------------"));

            for (Venta v : ventasCliente) {
                String fechaVenta = v.getFecha() != null ? v.getFecha().toString() : "Sin fecha";
                String formaPago = v.getPago() != null ? v.getPago().toString() : "No especificada";

                document.add(new Paragraph("Fecha: " + fechaVenta + " - Pago: " + formaPago));

                if (v.getDetalles() != null && !v.getDetalles().isEmpty()) {
                    for (DetalleVenta detalle : v.getDetalles()) {
                        String producto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto N/A";
                        double precio = detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0.0;
                        double cantidad = detalle.getCantidad();
                        double subtotal = precio * cantidad;

                        document.add(new Paragraph(
                                "   " + producto +
                                        " - Precio: $" + precio +
                                        " - Cantidad: " + cantidad +
                                        " - Subtotal: $" + subtotal
                        ));
                    }
                } else {
                    document.add(new Paragraph("   Sin detalles de productos."));
                }

                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("=========================================="));
            document.add(new Paragraph(" "));
        }

        document.close();
        return out;
    }

    @Override
    public ByteArrayOutputStream generarCierreDelAnioPDF(int anio, Long negocioId) throws Exception {
        LocalDateTime desde = LocalDateTime.of(anio, Month.JANUARY, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(anio, Month.DECEMBER, 31, 23, 59, 59);

        List<Venta> ventas = ventaRepository.findByFechaBetweenAndNegocioId(desde, hasta, negocioId);

        // Agrupar ventas por mes
        Map<Month, List<Venta>> ventasPorMes = ventas.stream()
                .filter(v -> v.getFecha() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getFecha().toLocalDateTime().getMonth(),
                        TreeMap::new,
                        Collectors.toList()
                ));
        double totalAnual = 0;

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Cierre de Caja - Año: " + anio));
        document.add(new Paragraph(" "));

        for (Map.Entry<Month, List<Venta>> entry : ventasPorMes.entrySet()) {
            Month mes = entry.getKey();
            List<Venta> ventasMes = entry.getValue();

            double totalMes = ventasMes.stream()
                    .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0)
                    .sum();

            totalAnual += totalMes;

            document.add(new Paragraph("Mes: " + mes.getDisplayName(TextStyle.FULL, new Locale("es"))));
            document.add(new Paragraph("Total: $" + String.format("%.2f", totalMes)));
            document.add(new Paragraph(" "));
        }

        document.add(new Paragraph("======================================"));
        document.add(new Paragraph("Total anual: $" + String.format("%.2f", totalAnual)));

        document.close();
        return out;
    }

    @Override
    public List<VentaDTO> getVentasDetalladasPorFecha(LocalDate fecha, Long negocioId) {
        Timestamp inicio = Timestamp.valueOf(fecha.atStartOfDay());
        Timestamp fin = Timestamp.valueOf(fecha.plusDays(1).atStartOfDay());

        List<Venta> ventas = ventaRepository.findByFechaAndNegocioId(inicio, fin, negocioId);

        return ventas.stream()
                .map(VentaDTO::new)
                .toList();
    }

    @Override
    public Optional<VentaDTO> getVentaDetalladaPorId(Long id, Long negocioId) {
        return ventaRepository.findById(id)
                .filter(v -> v.getNegocio().getId().equals(negocioId))
                .map(VentaDTO::new);
    }

}
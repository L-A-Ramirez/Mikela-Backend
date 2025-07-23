package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.Negocio;
import comercio.negocio.management.entities.Venta;
import comercio.negocio.management.entities.dto.DetalleVentaDTO;
import comercio.negocio.management.entities.dto.VentaDTO;
import comercio.negocio.management.service.venta.I_VentaService;
import comercio.negocio.security.entity.Usuario;
import comercio.negocio.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    @Autowired
    private I_VentaService service;

    @Autowired
    private JwtProvider jwtProvider;

    private Long extractNegocioId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }
        return jwtProvider.getNegocioIdFromToken(token.replace("Bearer ", ""));
    }

    private Long extractUsuarioId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }
        return jwtProvider.getUserIdFromToken(token.replace("Bearer ", ""));
    }

    @GetMapping("/lista")
    public List<VentaDTO> getAll(@RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        return service.getAllByNegocioId(negocioId).stream()
                .map(VentaDTO::new)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public Optional<Venta> getById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<Venta> venta = service.getById(id);
        if (venta.isPresent() && !venta.get().getNegocio().getId().equals(negocioId)) {
            throw new RuntimeException("No tienes permiso para acceder a esta venta.");
        }
        return venta;
    }

    @DeleteMapping("delete/{id}")
    public void remove(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<Venta> venta = service.getById(id);
        if (venta.isPresent() && !venta.get().getNegocio().getId().equals(negocioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta venta.");
        }
        service.remove(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> save(@RequestBody Venta venta, @RequestHeader("Authorization") String token) {
        try {
            Long negocioId = extractNegocioId(token);
            Long usuarioId = extractUsuarioId(token);

            Negocio negocio = new Negocio();
            negocio.setId(negocioId);
            venta.setNegocio(negocio);

            Usuario usuario = new Usuario();
            usuario.setId(Math.toIntExact(usuarioId));
            venta.setUsuario(usuario);

            Venta ventaGuardada = service.save(venta);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Venta registrada correctamente");
            response.put("ventaId", String.valueOf(ventaGuardada.getId()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al procesar la venta: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/update")
    public void update(@RequestBody Venta venta, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        if (!venta.getNegocio().getId().equals(negocioId)) {
            throw new RuntimeException("No puedes actualizar ventas para otro negocio.");
        }
        service.save(venta);
    }

    @GetMapping("/cierre-dia/pdf")
    public ResponseEntity<byte[]> getCierreDelDiaPDF(@RequestParam String fecha, @RequestHeader("Authorization") String token) {
        try {
            Long negocioId = extractNegocioId(token);
            LocalDate targetDate = LocalDate.parse(fecha);
            ByteArrayOutputStream pdfStream = service.generarCierreDelDiaPDF(targetDate, negocioId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("cierre_dia_" + fecha + ".pdf").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error generando el PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/cierre-mes/pdf")
    public ResponseEntity<byte[]> getCierreDelMesPDF(@RequestParam int anio, @RequestParam int mes, @RequestHeader("Authorization") String token) {
        try {
            Long negocioId = extractNegocioId(token);
            YearMonth yearMonth = YearMonth.of(anio, mes);
            ByteArrayOutputStream pdfStream = service.generarCierreDelMesPDF(yearMonth, negocioId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("cierre_mes_" + anio + "_" + mes + ".pdf").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error generando el PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/cierre-anio/pdf")
    public ResponseEntity<byte[]> getCierreDelAnioPDF(@RequestParam int anio, @RequestHeader("Authorization") String token) {
        try {
            Long negocioId = extractNegocioId(token);
            ByteArrayOutputStream pdfStream = service.generarCierreDelAnioPDF(anio, negocioId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename("cierre_anio_" + anio + ".pdf").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error generando el PDF: " + e.getMessage()).getBytes());
        }
    }



    @GetMapping("/detalladas")
    @PreAuthorize("isAuthenticated()")
    public List<VentaDTO> getVentasDetalladas(
            @RequestParam String fecha,
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("❌ Token inválido o no enviado. Valor recibido: " + token);
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }

        System.out.println("🔐 Token recibido correctamente: " + token);

        Long negocioId = extractNegocioId(token);
        LocalDate targetDate = LocalDate.parse(fecha);

        System.out.println("📅 Fecha solicitada: " + fecha + " | ID negocio: " + negocioId);

        return service.getVentasDetalladasPorFecha(targetDate, negocioId);
    }

    @GetMapping("/detallada/{id}")
    public ResponseEntity<VentaDTO> getVentaDetalladaPorId(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<VentaDTO> ventaDTO = service.getVentaDetalladaPorId(id, negocioId);
        return ventaDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
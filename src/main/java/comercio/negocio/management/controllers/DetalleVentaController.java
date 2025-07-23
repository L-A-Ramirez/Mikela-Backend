package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.DetalleVenta;
import comercio.negocio.management.entities.Producto;
import comercio.negocio.management.entities.Venta;
import comercio.negocio.management.entities.dto.DetalleVentaDTO;
import comercio.negocio.management.repositories.ProductoRepository;
import comercio.negocio.management.repositories.VentaRepository;
import comercio.negocio.management.service.detalleVenta.I_DetalleVentaService;
import comercio.negocio.management.utils.ApiResponse;
import comercio.negocio.security.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/detalleventas")
@CrossOrigin(origins = "*")
public class DetalleVentaController {

    @Autowired
    private I_DetalleVentaService service;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private JwtProvider jwtProvider;

    private Long extractNegocioId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }
        return jwtProvider.getNegocioIdFromToken(token.replace("Bearer ", ""));
    }

    @GetMapping("/lista")
    @PreAuthorize("isAuthenticated()")
    public List<DetalleVenta> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Optional<DetalleVenta> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> remove(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);

        Optional<DetalleVenta> detalle = service.getById(id);
        if (detalle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!detalle.get().getVenta().getNegocio().getId().equals(negocioId)) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar este detalle.");
        }

        service.remove(id);
        return ResponseEntity.ok("Detalle eliminado correctamente.");
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<ApiResponse> save(@RequestBody DetalleVentaDTO dto, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);

        if (dto.getProducto() == null || dto.getProducto().getId() == null
                || dto.getVenta() == null || dto.getVenta().getId() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Los IDs de producto y venta son requeridos"));
        }

        Producto producto = productoRepository.findById(dto.getProducto().getId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Producto no encontrado con ID: " + dto.getProducto().getId()));
        }

        Venta venta = ventaRepository.findById(dto.getVenta().getId()).orElse(null);
        if (venta == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Venta no encontrada con ID: " + dto.getVenta().getId()));
        }

        if (!venta.getNegocio().getId().equals(negocioId)) {
            return ResponseEntity.status(403).body(new ApiResponse("No puedes crear detalles para una venta de otro negocio."));
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(dto.getCantidad());
        detalle.setProducto(producto);
        detalle.setVenta(venta);

        service.save(detalle);
        return ResponseEntity.ok(new ApiResponse("Detalle de venta guardado correctamente"));
    }


    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> update(@RequestBody DetalleVenta detalleVenta, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);

        if (!detalleVenta.getVenta().getNegocio().getId().equals(negocioId)) {
            return ResponseEntity.status(403).body("No puedes modificar detalles de otro negocio.");
        }

        service.save(detalleVenta);
        return ResponseEntity.ok("Detalle actualizado correctamente.");
    }
}

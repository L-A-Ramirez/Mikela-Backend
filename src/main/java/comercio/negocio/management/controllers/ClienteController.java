package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.Cliente;
import comercio.negocio.management.entities.Negocio;
import comercio.negocio.management.service.cliente.I_ClienteService;
import comercio.negocio.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private I_ClienteService service;

    @Autowired
    private JwtProvider jwtProvider;

    private Long extractNegocioId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido o ausente. Intenta iniciar sesión nuevamente.");
        }
        return jwtProvider.getNegocioIdFromToken(token.replace("Bearer ", ""));
    }

    @GetMapping("/lista")
    public List<Cliente> getAll(@RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        return service.getAll(negocioId);
    }

    @GetMapping("/{id}/negocio/{negocioId}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id, @PathVariable Long negocioId,
                                           @RequestHeader("Authorization") String token) {
        Long tokenNegocioId = extractNegocioId(token);
        if (!tokenNegocioId.equals(negocioId)) {
            return ResponseEntity.status(403).build();
        }

        Optional<Cliente> cliente = service.getByIdAndNegocioId(id, negocioId);
        return cliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> save(@RequestBody Cliente cliente, @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Negocio negocio = new Negocio();
        negocio.setId(negocioId);
        cliente.setNegocio(negocio);

        service.save(cliente);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cliente registrado correctamente.");
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> update(@RequestBody Cliente cliente,
                                                      @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        if (!cliente.getNegocio().getId().equals(negocioId)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No puedes modificar clientes de otro negocio.");
            return ResponseEntity.status(403).body(error);
        }

        service.save(cliente);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cliente actualizado correctamente.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        Long negocioId = extractNegocioId(token);
        Optional<Cliente> cliente = service.getByIdAndNegocioId(id, negocioId);

        if (cliente.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        service.remove(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

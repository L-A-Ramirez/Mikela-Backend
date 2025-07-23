package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.Auditoria;
import comercio.negocio.management.entities.Venta;
import comercio.negocio.management.service.auditoria.I_AuditoriaService;
import comercio.negocio.management.service.venta.I_VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/auditorias")
@CrossOrigin(origins = "*")
public class AuditoriaController {
    @Autowired
    private I_AuditoriaService service;

    @GetMapping("/lista")
    public List<Auditoria> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Auditoria> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("delete/{id}")
    public void remove(@PathVariable String id) {
        service.remove((long) Integer.parseInt(id));
    }

    @PostMapping("/create")
    public void save(@RequestBody Auditoria auditoria) {
        service.save(auditoria);
    }

    @PutMapping("/update")
    public void update(@RequestBody Auditoria auditoria) {
        service.save(auditoria);
    }
}

package comercio.negocio.management.controllers;

import comercio.negocio.management.entities.Negocio;
import comercio.negocio.management.service.negocio.I_NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/negocios")
@CrossOrigin(origins = "*") // Permitir acceso desde cualquier origen
public class NegocioController {

    @Autowired
    private I_NegocioService service;

    // Obtener la lista de negocios
    @GetMapping("/lista")
    public ResponseEntity<List<Negocio>> getAll() {
        List<Negocio> negocios = service.getAll();
        return new ResponseEntity<>(negocios, HttpStatus.OK);
    }

    // Obtener un negocio por su ID
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Negocio> getById(@PathVariable Long id) {
        Optional<Negocio> negocio = service.getById(id);
        if (negocio.isPresent()) {
            return new ResponseEntity<>(negocio.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar un negocio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            service.remove(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Crear un nuevo negocio
    @PostMapping("/create")
    public ResponseEntity<Negocio> save(@RequestBody Negocio negocio) {
        try {
            Negocio nuevoNegocio = service.save(negocio);
            return new ResponseEntity<>(nuevoNegocio, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar un negocio existente
    @PutMapping("/update")
    public ResponseEntity<Negocio> update(@RequestBody Negocio negocio) {
        try {
            Negocio negocioActualizado = service.save(negocio);
            return new ResponseEntity<>(negocioActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

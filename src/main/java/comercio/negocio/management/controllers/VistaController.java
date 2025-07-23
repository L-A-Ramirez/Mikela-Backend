package comercio.negocio.management.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {
    @GetMapping("/catalogos")
    public String mostrarProductos() {
        return "productos";
    }
}

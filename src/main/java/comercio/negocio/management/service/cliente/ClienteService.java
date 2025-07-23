package comercio.negocio.management.service.cliente;

import comercio.negocio.management.entities.Cliente;
import comercio.negocio.management.repositories.ClienteRepository;
import comercio.negocio.security.entity.Usuario;
import comercio.negocio.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements I_ClienteService{

    @Autowired
    private ClienteRepository repository;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Cliente> getAll(Long negocioId) {
        return repository.findByNegocioId(negocioId);
    }

    @Override
    public void save(Cliente cliente) {
        repository.save(cliente);
    }

    @Override
    public void remove(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("El cliente con id " + id + " no existe.");
        }
    }

    @Override
    public Optional<Cliente> getByIdAndNegocioId(Long id, Long negocioId) {
        return repository.findByIdAndNegocioId(id, negocioId);
    }


    @Override
    public List<Cliente> getClientesByUsername(String username) {
        // Obtén el negocio asociado al usuario
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuario(username);
        if (usuario == null || usuario.get().getNegocio() == null) {
            throw new IllegalArgumentException("Usuario o negocio no encontrado");
        }

        // Filtra los clientes por el ID del negocio
        return repository.findByNegocioId(usuario.get().getNegocio().getId());
    }

}

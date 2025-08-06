package comercio.negocio.security.service;


import comercio.negocio.security.entity.Usuario;
import comercio.negocio.security.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Buscar usuario por nombreUsuario
    public Optional<Usuario> getByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    // Buscar usuario por email
    public Optional<Usuario> getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Crear o actualizar usuario
    public Usuario saveUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario por ID
    public void deleteUser(Integer id) {
        usuarioRepository.deleteById(id);
    }

    // Verificar existencia por email
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    // Verificar existencia por nombreUsuario
    public boolean existsByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    public Optional<Usuario> getByTokenRecuperacion(String token) {
        return usuarioRepository.findByTokenRecuperacion(token);
    }

    // Buscar usuario por email o nombre de usuario
    public Optional<Usuario> getByEmailOrNombreUsuario(String emailOrUsername) {
        return usuarioRepository.findByEmailOrNombreUsuario(emailOrUsername, emailOrUsername);
    }
}
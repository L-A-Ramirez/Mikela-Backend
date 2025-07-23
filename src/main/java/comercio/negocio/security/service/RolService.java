package comercio.negocio.security.service;


import comercio.negocio.security.entity.Rol;
import comercio.negocio.security.enums.RolNombre;
import comercio.negocio.security.repository.I_RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class RolService {
    @Autowired
    I_RolRepository repository;

    public Optional<Rol>getByRolNombre (RolNombre rolNombre){
        return repository.findByRolNombre(
                rolNombre);
    }

    public void save(Rol rol){
        repository.save(rol);
    }
}
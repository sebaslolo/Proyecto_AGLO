package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.TipoUsuario;
import com.Proyecto_Grupo_1.repository.TipoUsuarioRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public TipoUsuarioService(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoUsuario> getTiposUsuario(boolean sinFiltro) {
        return tipoUsuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TipoUsuario> getTipoUsuario(Integer idTipoUsuario) {
        return tipoUsuarioRepository.findById(idTipoUsuario);
    }

    @Transactional
    public TipoUsuario save(@Valid TipoUsuario tipoUsuario) {
        return tipoUsuarioRepository.save(tipoUsuario);
    }

    @Transactional
    public void delete(Integer idTipoUsuario) {
        if (!tipoUsuarioRepository.existsById(idTipoUsuario)) {
            throw new IllegalArgumentException("El tipo de usuario con ID " + idTipoUsuario + " no existe.");
        }
        try {
            tipoUsuarioRepository.deleteById(idTipoUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el tipo de usuario. Tiene datos asociados.", e);
        }
    }
}

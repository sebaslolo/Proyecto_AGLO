package com.Proyecto_Grupo_1.service;

import com.Proyecto_Grupo_1.domain.Cuenta;
import com.Proyecto_Grupo_1.repository.CuentaRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    public List<Cuenta> getCuentas(boolean sinFiltro) {
        return cuentaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cuenta> getCuenta(Integer idCuenta) {
        return cuentaRepository.findById(idCuenta);
    }

    @Transactional
    public Cuenta save(@Valid Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void delete(Integer idCuenta) {
        if (!cuentaRepository.existsById(idCuenta)) {
            throw new IllegalArgumentException("La cuenta con ID " + idCuenta + " no existe.");
        }
        try {
            cuentaRepository.deleteById(idCuenta);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la cuenta. Tiene datos asociados.", e);
        }
    }
}

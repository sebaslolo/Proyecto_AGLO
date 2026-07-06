package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Cuenta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    Optional<Cuenta> findByCorreoElectronicoIgnoreCase(String correoElectronico);

    boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);
}

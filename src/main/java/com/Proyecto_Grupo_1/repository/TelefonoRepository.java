package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Telefono;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TelefonoRepository extends JpaRepository<Telefono, Integer> {

    Optional<Telefono> findByNumeroTelefono(String numeroTelefono);
}

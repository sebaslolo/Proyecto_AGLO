package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Ruta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    Optional<Ruta> findByRuta(String ruta);

    List<Ruta> findByRolIdRol(Integer idRol);
}

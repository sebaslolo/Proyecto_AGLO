/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.Proyecto_Grupo_1.repository;

/*
 *
 * @author milu
 */
import com.Proyecto_Grupo_1.domain.Voluntariado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VoluntariadoRepository extends JpaRepository<Voluntariado, Integer> {
 boolean existsByUsuarioIdUsuarioAndActividadIdActividad(
 Integer idUsuario,
 Integer idActividad
 );
 long countByActividadIdActividad(Integer idActividad);
 List<Voluntariado> findByUsuarioIdUsuarioOrderByFechaInscripcionDesc(
 Integer idUsuario
 );
}


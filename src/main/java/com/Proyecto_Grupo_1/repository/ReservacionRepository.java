package com.Proyecto_Grupo_1.repository;

import com.Proyecto_Grupo_1.domain.Reservacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservacionRepository extends JpaRepository<Reservacion, Integer> {

    List<Reservacion> findByUsuarioIdUsuarioOrderByFechaReservacionDesc(Integer idUsuario);
}

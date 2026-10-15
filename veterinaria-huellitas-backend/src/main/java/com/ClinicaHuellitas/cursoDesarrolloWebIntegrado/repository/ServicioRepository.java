package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Consulta derivada del nombre del método: solo los servicios activos, ordenados por nombre
    List<Servicio> findByActivoTrueOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    // Consulta JPQL: busca servicios activos cuyo nombre contenga el texto (sin importar mayúsculas)
    @Query("SELECT s FROM Servicio s "
            + "WHERE s.activo = true AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) "
            + "ORDER BY s.nombre")
    List<Servicio> buscarPorNombre(@Param("nombre") String nombre);
}

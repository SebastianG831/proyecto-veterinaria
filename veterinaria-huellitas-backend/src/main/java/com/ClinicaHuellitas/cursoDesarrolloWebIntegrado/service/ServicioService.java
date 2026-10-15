package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.MensajeResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ServicioRequest;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.dto.ServicioResponse;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.exception.ApiException;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.model.Servicio;
import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.repository.ServicioRepository;

/**
 * CRUD de servicios de la veterinaria.
 * "Eliminar" desactiva el servicio (baja lógica) para conservar el historial de reservas.
 */
@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(ServicioResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        return ServicioResponse.desde(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> buscar(String nombre) {
        return servicioRepository.buscarPorNombre(nombre.trim()).stream()
                .map(ServicioResponse::desde)
                .toList();
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest request) {
        String nombre = request.nombre().trim();
        if (servicioRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un servicio con ese nombre");
        }

        Servicio servicio = new Servicio();
        copiarDatos(servicio, request, nombre);
        servicio.setActivo(request.activo() == null || request.activo());
        return ServicioResponse.desde(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest request) {
        Servicio servicio = buscarPorId(id);
        String nombre = request.nombre().trim();
        if (servicioRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe otro servicio con ese nombre");
        }

        copiarDatos(servicio, request, nombre);
        // "activo" es opcional: si no viene, se mantiene el valor actual
        if (request.activo() != null) {
            servicio.setActivo(request.activo());
        }
        return ServicioResponse.desde(servicioRepository.save(servicio));
    }

    @Transactional
    public MensajeResponse desactivar(Long id) {
        Servicio servicio = buscarPorId(id);
        servicio.setActivo(false);
        servicioRepository.save(servicio);
        return new MensajeResponse("Servicio desactivado correctamente");
    }

    private void copiarDatos(Servicio servicio, ServicioRequest request, String nombre) {
        servicio.setNombre(nombre);
        servicio.setDescripcion(request.descripcion());
        servicio.setPrecio(request.precio());
        servicio.setDuracionMinutos(request.duracionMinutos());
    }

    private Servicio buscarPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El servicio no existe"));
    }
}

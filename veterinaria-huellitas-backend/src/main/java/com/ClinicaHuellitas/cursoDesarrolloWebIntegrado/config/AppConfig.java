package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.config;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.service.HorarioAtencion;

@Configuration
public class AppConfig {

    // Reloj con la zona horaria de Lima: define qué significa "ahora" al validar fechas futuras
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Lima"));
    }

    // El horario se lee de application.properties (huellitas.horario.apertura / cierre)
    @Bean
    public HorarioAtencion horarioAtencion(
            @Value("${huellitas.horario.apertura:09:00}") String apertura,
            @Value("${huellitas.horario.cierre:21:00}") String cierre) {
        return new HorarioAtencion(LocalTime.parse(apertura), LocalTime.parse(cierre));
    }
}

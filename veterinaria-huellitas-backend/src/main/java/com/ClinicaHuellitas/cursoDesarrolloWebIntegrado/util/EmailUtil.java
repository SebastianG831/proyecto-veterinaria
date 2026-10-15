package com.ClinicaHuellitas.cursoDesarrolloWebIntegrado.util;

/**
 * Utilidad para tratar los correos siempre igual:
 * sin espacios y en minúsculas ("Ana@Mail.com " y "ana@mail.com" son el mismo usuario).
 */
public final class EmailUtil {

    private EmailUtil() {
    }

    public static String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

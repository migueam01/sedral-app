package com.mam.alicamp.servicios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManejoFechas {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

    public static String obtenerFechaActual() {
        DateTimeFormatter formatterString = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");
        LocalDateTime local = LocalDateTime.now();
        return local.format(formatterString);
    }

    public static String convertirFechaAString(LocalDateTime fecha) {
        return fecha.format(formatter);
    }

    public static LocalDateTime convertirFechaADateTime(String fechaString) {
        return LocalDateTime.parse(fechaString, formatter);
    }
}

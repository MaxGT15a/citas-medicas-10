package com.max.commons.utils;

import com.max.commons.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {
    private static final String NUMEROS = "0123456789";

    public static void validarEnteroPositvo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecimalPositvo(BigDecimal numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if (numero.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarRangoShort(short numero, int min, int max, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if (numero < min || numero > max)
            throw new DatoInvalidoException(mensaje);
    }

    public static <N extends Number> void validarRango(
            N numero,
            N minimo,
            N maximo,
            String mensajeError
    ) {
        validarNumeroRequerido(numero, mensajeError);
        validarNumeroRequerido(minimo, mensajeError);
        validarNumeroRequerido(maximo, mensajeError);

        BigDecimal valor = convertir(numero);
        BigDecimal min = convertir(minimo);
        BigDecimal max = convertir(maximo);

        if (valor.compareTo(min) < 0 || valor.compareTo(max) > 0)
            throw new DatoInvalidoException(mensajeError);
    }

    private static BigDecimal convertir(Number numero) {
        return new BigDecimal(numero.toString());
    }

    public static <N extends Number> void validarNumeroRequerido(
            N numero,
            String mensajeError
    ) {
        if (numero == null)
            throw new DatoInvalidoException(mensajeError);
    }

    public static <N extends Number> void validarPositivo(
            N numero,
            String mensajeError
    ) {
        validarNumeroRequerido(numero, mensajeError);

        BigDecimal valor = convertir(numero);

        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensajeError);
    }

public static void validarFloatPositvo(Float numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarCharSeaNumero(char c, String mensaje) {
        if(!NUMEROS.contains(String.valueOf(c)))
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarStringSoloNumeros(String texto, String mensaje){
        for(int i = 0; i < texto.length(); i++){
            validarCharSeaNumero(texto.charAt(i), mensaje);
        }
    }
}

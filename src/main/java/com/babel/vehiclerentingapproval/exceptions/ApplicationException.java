package com.babel.vehiclerentingapproval.exceptions;

import org.springframework.http.HttpStatus;

/**
 * La clase ApplicationException representa una excepción personalizada para la validación
 * de solicitudes de API. Esta excepción es principal, es decir, es el padre del resto de excepciones personalizadas de la
 * aplicación. Extiende la clase RuntimeException y contiene información adicional
 * como un mensaje de error específico y un código de estado HTTP.
 */
public class ApplicationException extends RuntimeException{
    private final String externalMessage;
    private final HttpStatus statusCode;

    /**
     * Crea una nueva instancia de ApplicationException con el mensaje de error y el código
     * de estado HTTP proporcionados.
     *
     * @param externalMessage el mensaje de error específico
     * @param statusCode      el código de estado HTTP asociado
     */
    public ApplicationException (String externalMessage, HttpStatus statusCode) {
        this.externalMessage = externalMessage;
        this.statusCode = statusCode;
    }

    /**
     * Crea una nueva instancia de ApplicationException con el mensaje de error formateado,
     * el código de estado HTTP y los argumentos proporcionados para formatear el mensaje de error.
     *
     * @param externalMessage el mensaje de error específico
     * @param statusCode      el código de estado HTTP asociado
     * @param args            los argumentos utilizados para formatear el mensaje de error
     */
    public ApplicationException (String externalMessage, HttpStatus statusCode, String[] args) {
        this.externalMessage = String.format(externalMessage, args);
        this.statusCode = statusCode;
    }

    /**
     * Obtiene el mensaje de error específico.
     *
     * @return el mensaje de error
     */
    public String getExternalMessage ( ) {
        return externalMessage;
    }

    /**
     * Obtiene el código de estado HTTP asociado.
     *
     * @return el código de estado HTTP
     */
    public HttpStatus getStatusCode ( ) {
        return statusCode;
    }
}

package com.babel.vehiclerentingapproval.exceptions;

import org.springframework.http.HttpStatus;

/**
 * La clase PersonaNotFoundException representa una excepción personalizada que se lanza cuando
 * no se encuentra una persona con el ID proporcionado. Esta excepción extiende la clase
 * RequestApiValidationException para proporcionar un mensaje específico y un código de estado HTTP.
 */
public class PersonaNotFoundException extends ApplicationException {
    private static final String EXTERNAL_MESSAGE = "La persona con id: %s no existe.";

    /**
     * Crea una nueva instancia de PersonaNotFoundException sin especificar el ID de la persona.
     * Utilice este constructor cuando no se requiera información adicional sobre el ID de la persona no encontrada.
     */
    public PersonaNotFoundException ( ) {
        super(EXTERNAL_MESSAGE, HttpStatus.NOT_FOUND);
    }
    public PersonaNotFoundException (int idPersona, HttpStatus badRequest) {
        super(EXTERNAL_MESSAGE, badRequest, new String[]{String.valueOf(idPersona)});
    }
        public PersonaNotFoundException (HttpStatus statusCode ) {
        super(EXTERNAL_MESSAGE, statusCode);
    }
}

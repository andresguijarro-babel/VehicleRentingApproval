package com.babel.vehiclerentingapproval.controllers;

import com.babel.vehiclerentingapproval.exceptions.ApplicationException;
import com.babel.vehiclerentingapproval.exceptions.ChuckExceptions.ChuckNorrisException;
import com.babel.vehiclerentingapproval.exceptions.ChuckExceptions.CuckNorrisResourceAccessException;
import com.babel.vehiclerentingapproval.exceptions.ChuckExceptions.CuckNorrisServerErrorException;
import com.babel.vehiclerentingapproval.exceptions.RequestApiValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Map;

/**
 * Manejador global de excepciones.
 */

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String DESCRIPCION = "Descripcion: ";


    /**
     * Metodo que maneja todas las excepciones personalizadas, ya que RequestApiValidationException es la excepción padre de la que
     * heredan el resto de excepciones creadas.
     * <p>
     *
     * @param e Excepción que hereda de RequestApiValidationException
     * @return un objeto ResponseEntity que contiene la respuesta y el código de error de la excepción lanzada
     * @see RequestApiValidationException
     */
    @ExceptionHandler(RequestApiValidationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleRequestApiValidationException (RequestApiValidationException e) {
        return new ResponseEntity<>(Map.of(DESCRIPCION, e.getExternalMessage()), e.getStatusCode());
    }

    /**
     * Metodo que maneja el resto de excepciones que puedan generarse que no sean excepciones personalizadas creadas
     * para esta aplicación.
     * <p>
     *
     * @param e Excepción genérica
     * @return un objeto ResponseEntity que contiene una descripción y estado de error interno del servidor.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException (Exception e) {
        log.error("Mensaje de error: " + e.getMessage());
        return new ResponseEntity<>(Map.of(DESCRIPCION, "Error: Ha ocurrido un error. Contacte con el equipo ING BOOTCAMP."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleApplicationException(ApplicationException e) {
        log.error("Mensaje de error: " + e.getMessage());

        return new ResponseEntity<>(Map.of(DESCRIPCION, e.getExternalMessage()), e.getStatusCode());
    }//TODO apl tests

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
        // Manejar la excepción de la API externa
        //System.out.println(ex.getCause());
        if(ex.getCause().toString().contains("handshake_failure")){
            return new ResponseEntity<>(new CuckNorrisResourceAccessException().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(new CuckNorrisServerErrorException().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}


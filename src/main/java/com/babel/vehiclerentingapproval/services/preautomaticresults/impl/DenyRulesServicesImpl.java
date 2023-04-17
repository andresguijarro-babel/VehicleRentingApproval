package com.babel.vehiclerentingapproval.services.preautomaticresults.impl;

import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.services.preautomaticresults.DenyRulesService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Esta clase implementa las comprobaciones sobre las reglas de negación de la solicitud de renting
 * @author alvaro.dorado@babelgroup.com
 * @author ismael.mesa@babelgroup.com
 *
 */
@Service
public class DenyRulesServicesImpl implements DenyRulesService {
    /**
     * Contiene la fecha de hoy
     */
    private static final LocalDate fechaActual = LocalDate.now();
    /**
     * Constante que contiene la mayoría de edad legal
     */
    private static final int anyosMayor = 18;
    /**
     * Constante que contiene el límite de scoring para la solicitud del renting
     */
    private static final int scoringRating = 6;
    /**
     * Constante con el plazo de años máximo a añadir al plazo
     */
    private static final int anyosPlazo = 80;

    /**
     * Este método comprueba la edad del cliente (que se recoge en la solicitudRenting) y comprueba si es menor o mayor que 18 años
     * @param solicitudRenting la solicitud de Renting
     * @return devuelve true si es mayor o igual que 18 y false en caso contrario
     */
    @Override
    public Boolean validateClientAge(SolicitudRenting solicitudRenting) {

        Date fechaNacimiento = solicitudRenting.getPersona().getFechaNacimiento();
        LocalDate fechaNacimientoLocalDate = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int anyo = fechaNacimientoLocalDate.getYear();
        int day = fechaNacimientoLocalDate.getDayOfMonth();
        int month = fechaNacimientoLocalDate.getMonthValue();

        var fechaConcreta = LocalDate.of(anyo, month, day);
        long anios = ChronoUnit.YEARS.between(fechaConcreta, fechaActual);
        if (anios < anyosMayor) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Este método comprueba la puntuación del cliente (que se recoge en la solicitudRenting) y comprueba si es menor o mayor que una constante definida
     * @param solicitudRenting la solicitud de Renting
     * @return devuelve true si es mayor que la constante de scoring y false en caso contrario
     */
    @Override
    public Boolean validateScoringTitular(SolicitudRenting solicitudRenting) {
        if (solicitudRenting.getPersona().getScoring() >= scoringRating) {
            return true;

        } else {
            return false;
        }
    }
    /**
     * Este método comprueba que el plazo de la solicitud supera o no un tiempo límite definido
     * @param solicitudRenting la solicitud de Renting
     * @return devuelve true si es mayor de lo establecidoy falso en caso contrario
     */
    @Override
    public Boolean validateClientAgePlusPlazo(SolicitudRenting solicitudRenting) {
        Date fechaNacimiento = solicitudRenting.getPersona().getFechaNacimiento();
        int anyo = fechaNacimiento.getYear() + 1900;
        int day = fechaNacimiento.getDate();
        int month = fechaNacimiento.getMonth() + 1;
        LocalDate fechaConcreta = LocalDate.of(anyo, month, day);
        long anios = ChronoUnit.YEARS.between(fechaConcreta, fechaActual);
        if (anios + solicitudRenting.getPlazo().intValue() >= anyosPlazo) {
            return true;
        } else {
            return false;
        }
    }

}

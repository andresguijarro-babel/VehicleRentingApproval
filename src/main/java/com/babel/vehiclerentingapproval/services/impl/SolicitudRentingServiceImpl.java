package com.babel.vehiclerentingapproval.services.impl;

import com.babel.vehiclerentingapproval.exceptions.*;
import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.models.TipoResultadoSolicitud;
import com.babel.vehiclerentingapproval.persistance.database.mappers.PersonaMapper;
import com.babel.vehiclerentingapproval.persistance.database.mappers.SolicitudRentingMapper;
import com.babel.vehiclerentingapproval.persistance.database.mappers.TipoResultadoSolicitudMapper;
import com.babel.vehiclerentingapproval.services.CodigoResolucionValidator;
import com.babel.vehiclerentingapproval.services.SolicitudRentingService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class SolicitudRentingServiceImpl implements SolicitudRentingService {
    private final SolicitudRentingMapper solicitudRentingMapper;
    private final TipoResultadoSolicitudMapper tipoResultadoSolicitudMapper;
    private final PersonaMapper personaMapper;
    private final CodigoResolucionValidator codigoResolucionValidator;

    public SolicitudRentingServiceImpl(SolicitudRentingMapper solicitudRentingMapper,TipoResultadoSolicitudMapper tipoResultadoSolicitudMapper, PersonaMapper personaMapper,CodigoResolucionValidator codigoResolucionValidator) {
        this.solicitudRentingMapper = solicitudRentingMapper;
        this.tipoResultadoSolicitudMapper = tipoResultadoSolicitudMapper;
        this.personaMapper = personaMapper;
        this.codigoResolucionValidator = codigoResolucionValidator;
    }

    @Override
    public SolicitudRenting addSolicitudRenting (SolicitudRenting solicitudRenting) throws PersonaNotFoundException, WrongLenghtFieldException, InputIsNullOrIsEmpty, DateIsBeforeException {
        validatePersona(solicitudRenting.getPersona().getPersonaId());
        validateNumber(solicitudRenting);
        validateNotNullOrIsEmpty(solicitudRenting);
        validateFecha(solicitudRenting);
         solicitudRentingMapper.insertSolicitudRenting(solicitudRenting);
         return solicitudRenting;
    }


    private void existIdPersona (int idPersona) throws PersonaNotFoundException {
        if (personaMapper.existePersona(idPersona) < 1){
            throw new PersonaNotFoundException();
        }
    }

    @Override
    public String verEstadoSolicitud(int idSolicitud) throws EstadoSolicitudNotFoundException, EstadoSolicitudInvalidException {
        int codigoExiste = tipoResultadoSolicitudMapper.existeCodigoResolucion(idSolicitud);

        if(codigoExiste == 0){ //idSolicitud or codResolucion null
            throw new EstadoSolicitudNotFoundException();
        }

        TipoResultadoSolicitud resultadoSolicitud = this.tipoResultadoSolicitudMapper.getResultadoSolicitud(idSolicitud);
        this.validarCodigoResolucion(resultadoSolicitud.getCodResultado());

        return resultadoSolicitud.getDescripcion();


    }
    private void validarCodigoResolucion(String CodResolucion) throws EstadoSolicitudInvalidException{
        this.codigoResolucionValidator.validarCodResolucion(CodResolucion);

    }
    public SolicitudRenting getSolicitudById(int id) throws SolicitudRentingNotFoundException {
        int existe = this.solicitudRentingMapper.existeSolicitud(id);
        SolicitudRenting solicitudRenting;
        if (existe == 1) {
            solicitudRenting = this.solicitudRentingMapper.getSolicitudByID(id);
        }else{
            throw new SolicitudRentingNotFoundException();
        }

        return solicitudRenting;
    }

    @Override
    public void modificaSolicitud(Integer solicitudId, SolicitudRenting nuevoRenting) {
        this.solicitudRentingMapper.modificaSolicitud(solicitudId,nuevoRenting);
    }

     private int lenghtNumber(BigInteger number){
        if(number != null){
            String numeroString = number.toString();
            return numeroString.length();
        }
        return 0;
     }

    private void validatePersona(int idPersona) throws PersonaNotFoundException {
        existIdPersona(idPersona);
    }

     private void validateNumber(SolicitudRenting solicitudRenting) throws WrongLenghtFieldException {
        if(lenghtNumber(solicitudRenting.getNumVehiculos()) > 38 || lenghtNumber(solicitudRenting.getPlazo()) > 38){
            throw new WrongLenghtFieldException();
        }
     }

     private void validateNotNullOrIsEmpty (SolicitudRenting solicitudRenting) throws InputIsNullOrIsEmpty {
        if(solicitudRenting.getNumVehiculos() == null || solicitudRenting.getNumVehiculos().toString().isEmpty()
                || solicitudRenting.getInversion() == null || solicitudRenting.getInversion().toString().isEmpty()
                    || solicitudRenting.getCuota() == null || solicitudRenting.getCuota().toString().isEmpty()){
            throw new InputIsNullOrIsEmpty();
        }
     }

     private void validateFecha(SolicitudRenting solicitudRenting) throws DateIsBeforeException {
            if(solicitudRenting.getFechaInicioVigor() != null && solicitudRenting.getFechaResolucion() != null){
                if(solicitudRenting.getFechaInicioVigor().before(solicitudRenting.getFechaResolucion())){
                    throw new DateIsBeforeException();
                }
            }
     }
    public void cancelarSolicitud(int id) throws SolicitudRentingNotFoundException{
        SolicitudRenting solicitudRenting = this.solicitudRentingMapper.getSolicitudByID(id);
        if(solicitudRenting==null){
            throw new SolicitudRentingNotFoundException();
        }
        solicitudRentingMapper.cancelarSolicitud(solicitudRenting);

    }
   
}

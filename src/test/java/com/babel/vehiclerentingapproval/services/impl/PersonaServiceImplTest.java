package com.babel.vehiclerentingapproval.services.impl;

import com.babel.vehiclerentingapproval.exceptions.*;
import com.babel.vehiclerentingapproval.models.*;
import com.babel.vehiclerentingapproval.persistance.database.mappers.*;
import com.babel.vehiclerentingapproval.services.PersonaService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PersonaServiceImplTest {
    PersonaMapper personaMapper;

    DireccionMapper direccionMapper;
    PersonaService personaService;
    PaisMapper paisMapper;
    TelefonoMapper telefonoMapper;

    @BeforeEach
    void setupAll() {
        personaMapper = Mockito.mock(PersonaMapper.class);
        when(personaMapper.existePersona(100)).thenReturn(0); //No existe la persona
        when(personaMapper.existePersona(1)).thenReturn(1); //Existe la persona

        direccionMapper = Mockito.mock(DireccionMapper.class);
        when(direccionMapper.existeDireccion(1)).thenReturn(0);
        when(direccionMapper.existeDireccion(100)).thenReturn(1);

        telefonoMapper = Mockito.mock(TelefonoMapper.class);
        TipoViaMapper tipoViaMapper = Mockito.mock(TipoViaMapper.class);
        ProvinciaMapper provinciaMapper = Mockito.mock(ProvinciaMapper.class);
        paisMapper = Mockito.mock(PaisMapper.class);


        personaService = new PersonaServiceImpl(direccionMapper, personaMapper, telefonoMapper, tipoViaMapper, provinciaMapper, paisMapper);

    }

    @Test
    void addPersona_should_throwRequiredMissingFieldException_when_nombreIsNull() {
        assertThrows(Exception.class, () -> {
            Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());

            persona.setNombre(null);
            this.personaService.addPersona(persona);
        });
    }

    @Test
    void addPersona_should_throwWrongLenghtFieldException_when_nombreIsBiggerThan50() {
        Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());
        persona.setNombre("nombre de persona muy largo de mas de cincuenta caracteres");
        assertThrows(WrongLenghtFieldException.class, () -> {
            this.personaService.addPersona(persona);
        });
    }


    @Test
    void addPersona_should_throwRequiredMissingFieldException_when_apellido1Null() {
        assertThrows(Exception.class, () -> {
            Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());

            persona.setApellido1(null);
            this.personaService.addPersona(persona);
        });
    }

    @Test
    void addPersona_should_throwRequiredMissingFieldException_when_nifNull() {
        assertThrows(Exception.class, () -> {
            Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());

            persona.setNif(null);
            this.personaService.addPersona(persona);
        });
    }


    @Test
    void addPersona_should_not_anyExceptions_when_personaDataIsCorrect() {
        when(paisMapper.getPais(anyString())).thenReturn(new Pais("ES", 1, "ES", "ESPAÑA", 1));

        assertDoesNotThrow(() -> {
            Persona persona = createPersona();

            this.personaService.addPersona(persona);
        });
    }

    @Test
    void modificarPersona_should_throwPersonaNotFoundException_when_personaNoExisteEnBaseDeDatos() {
        Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());
        persona.setPersonaId(100); //Persona no existente
        assertThrows(PersonaNotFoundException.class, () -> {
            personaService.modificarPersona(persona);
        });
    }

    @Test
    void modificarPersona_should_throwDireccionNotFoundException_when_direccionNoExisteEnBaseDeDatos() {
        Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());

        Direccion direccion = new Direccion();
        direccion.setDireccionId(1); //Direccion no existente
        persona.setPersonaId(1); //Persona existente
        persona.setDireccionDomicilio(direccion);
        persona.setDireccionNotificacion(direccion);
        assertThrows(DireccionNotFoundException.class, () -> {
            personaService.modificarPersona(persona);
        });
    }

    @Test
    void modificarPersona_should_not_throwAnyException_when_DatosSonCorrectos() {
        when(direccionMapper.updateDireccion(any())).thenReturn(1);
        when(direccionMapper.existeDireccion(anyInt())).thenReturn((1));
        when(personaMapper.existePersona(anyInt())).thenReturn(1);

        assertDoesNotThrow(() -> {
            Persona persona = createPersona();

            this.personaService.modificarPersona(persona);
        });

    }
    @SneakyThrows
    @Test
    void modificarTelefono_should_throwPersonaNotFoundException_when_idPersonaNoExisteEnBaseDeDatos() {
        int idPersona = 1;
        when(personaMapper.existePersona(idPersona)).thenReturn(0);

        Persona persona = createPersona();
        PersonaNotFoundException exception = assertThrows(PersonaNotFoundException.class,
                () -> personaService.modificarTelefono(persona),
                "Expected PersonaNotFoundException to be thrown for persona: " + persona);
    }


    @Test
    void modificarTelefono_should_not_throwAnyException_when_datosSonCorrectos(){

        when(personaMapper.existePersona(anyInt())).thenReturn(1);

        List<TelefonoContacto> telefonoContactoList = new ArrayList<TelefonoContacto>();
        telefonoContactoList.add(new TelefonoContacto(1, "665453634"));
        when(telefonoMapper.listarTelefonos(anyInt())).thenReturn(telefonoContactoList);
        Assertions.assertDoesNotThrow(() -> {
            personaService.modificarTelefono(createPersona());
        });
    }


    @Test
    void addPersonaDireccion_should_not_ThrowException_when_direccionDomicilioDistintaNotificacion() throws ParseException {

        when(personaMapper.existePersona(anyInt())).thenReturn(1);
        Persona persona = createPersona();
        persona.setDireccionDomicilioSameAsNotificacion(false);

        Direccion notificacion = new Direccion();

        notificacion.setNombreCalle("Avda constitucion");
        notificacion.setNumero("3");
        notificacion.setCodPostal("41002");
        notificacion.setMunicipio("SEVILLA");
        notificacion.setTipoViaId(new TipoVia(2, "Avenida"));
        notificacion.setProvincia(new Provincia("SVQ", "SEVILLA"));

        persona.setDireccionNotificacion(notificacion);

        assertDoesNotThrow(() -> {
            personaService.addPersona(persona);
        });
    }
    @SneakyThrows
    @Test
    void viewPersonaProducto_should_throwPersonaNotFoundException_when_personaNoExiste() {
        var persona = createPersona();
        int personaId = persona.getPersonaId();
        when(personaMapper.existePersona(anyInt())).thenReturn(0);
        assertThrows(PersonaNotFoundException.class, () -> {
            personaService.viewPersonaProducto(personaId);
        });
    }

    @SneakyThrows
    @Test
    void viewPersonaProducto_should_not_throwThrowPersonaNotFoundException_when_personaExiste() {
        int personaId = createPersona().getPersonaId();
        when(personaMapper.existePersona(anyInt())).thenReturn(1);
        assertDoesNotThrow(() -> {
            personaService.viewPersonaProducto(personaId);
        });
    }
    @Test
    void updateEstadoPersonaProducto_should_setEstadoVigente_when_fechaBajaIsNull() throws ParseException {

        Persona persona = createPersona();
        persona.getProductosContratados().get(0).setFechaBaja(null);
        persona.getProductosContratados().get(0).setEstado(null);

        personaService.updateEstadoPersonaProducto(persona.getProductosContratados());
        Assertions.assertEquals(EstadoProductoContratado.VIGENTE, persona.getProductosContratados().get(0).getEstado());
    }

    @Test
    void updateEstadoPersonaProducto_should_setEstadoVencido_when_fechaBajaIsNotNull() throws ParseException {

        Persona persona = createPersona();
        persona.getProductosContratados().get(0).setEstado(null);
        persona.getProductosContratados().get(0).setFechaBaja(new SimpleDateFormat("dd-MM-yyyy").parse("29-12-2000"));

        personaService.updateEstadoPersonaProducto(persona.getProductosContratados());
        Assertions.assertEquals(EstadoProductoContratado.VENCIDO, persona.getProductosContratados().get(0).getEstado());
    }

    @Test
    void validateNif_should_throwDniFoundException_when_dniExiste() {
        when(personaMapper.existeNif(anyString())).thenReturn(1);

        assertThrows(DniFoundException.class, () -> {
            personaService.validateNif("1234567");
        });
    }

    @Test
    void existPerson_should_throwPersonaNotFoundException_when_idPersonaNegativo() {


        assertThrows(PersonaNotFoundException.class, () -> {

            this.personaService.invalidPersonId(-1);
        });

    }

    @Test
    void existPerson_should_returnNull_when_idPersonaPositivo() throws RequestApiValidationException {
        Assertions.assertEquals(null, personaService.invalidPersonId(10));

    }

    private boolean existePersonaMockFalse(Integer personaId) {
        return false;
    }

    private Persona createPersona() throws ParseException {
        Persona persona = new Persona(persona.getPersonaId(), persona.getNombre(), persona.getEmail(), persona.getPassword());
        persona.setNombre("Juan");
        persona.setApellido1("Francés");
        persona.setApellido2("Atúñez");
        persona.setFechaNacimiento(new SimpleDateFormat("dd-MM-yyyy").parse("29-12-1980"));

        Direccion domicilio = new Direccion();

        domicilio.setNombreCalle("Gran via");
        domicilio.setNumero("120");
        domicilio.setCodPostal("36201");
        domicilio.setMunicipio("Pontevedra");
        domicilio.setTipoViaId(new TipoVia(1, "Calle"));
        domicilio.setProvincia(new Provincia("SVQ", "Sevilla"));

        Direccion notificacion = new Direccion();

        notificacion.setNombreCalle("Plaza nueva");
        notificacion.setNumero("44");
        notificacion.setCodPostal("41001");
        notificacion.setMunicipio("Sevilla");
        notificacion.setTipoViaId(new TipoVia(1, "Calle"));
        notificacion.setProvincia(new Provincia("SVQ", "Sevilla"));

        persona.setDireccionDomicilio(domicilio);
        persona.setDireccionDomicilioSameAsNotificacion(true);
        persona.setDireccionNotificacion(notificacion);

        persona.setNacionalidad(new Pais("ES", 1, "ES", "ESPAÑA", 1));

        List<ProductoContratado> productosContratados = new ArrayList<ProductoContratado>();
        productosContratados.add(new ProductoContratado(1, 2, "Coche", 100, new SimpleDateFormat("dd-MM-yyyy").parse("29-12-2000")));
        persona.setProductosContratados(productosContratados);

        List<TelefonoContacto> telefonos = new ArrayList<TelefonoContacto>();
        telefonos.add(new TelefonoContacto(1, "677645552"));
        persona.setTelefonos(telefonos);
        persona.setEmail("holahola@email.com");
        persona.setNif("1111112F");
        persona.setFechaScoring(new SimpleDateFormat("dd-MM-yyyy").parse("29-12-1980"));


        return persona;
    }
}

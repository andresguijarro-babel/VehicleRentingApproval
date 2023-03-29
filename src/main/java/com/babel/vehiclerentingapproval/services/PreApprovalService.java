package com.babel.vehiclerentingapproval.services;

public interface PreApprovalService {


    Boolean validateInversionIngresos(int solicitudId);

    Boolean validateScoringPersona(int solicitudId);

    Boolean validateCIFCliente(int solicitudId);

    Boolean validateInversion(int solicitudId);


    Boolean validateYearsExperience(int solicitudId);
}
package com.babel.vehiclerentingapproval.services.preautomaticresults.impl;

import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.persistance.database.mappers.AutomaticResultMapper;
import com.babel.vehiclerentingapproval.services.preautomaticresults.ApprovalRulesService;
import com.babel.vehiclerentingapproval.services.preautomaticresults.CalculateAutomaticResult;
import com.babel.vehiclerentingapproval.services.preautomaticresults.DenyRulesService;
import org.springframework.stereotype.Service;

@Service
public class CalculateAutomaticResultImpl implements CalculateAutomaticResult {

    private DenyRulesService denyRulesService;
    private ApprovalRulesService approvalRulesService;
    private AutomaticResultMapper automaticResultMapper;

    public CalculateAutomaticResultImpl(DenyRulesService denyRulesService, ApprovalRulesService approvalRulesService, AutomaticResultMapper automaticResultMapper) {
        this.denyRulesService = denyRulesService;
        this.approvalRulesService = approvalRulesService;
        this.automaticResultMapper = automaticResultMapper;
    }

    @Override
    public Boolean findAnyDeny(SolicitudRenting solicitudRenting) {
        if (!denyRulesService.validateClientAge(solicitudRenting) &&
                !denyRulesService.validateClientAgePlusPlazo(solicitudRenting) &&
                !denyRulesService.validateScoringTitular(solicitudRenting)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean findAllApproval(SolicitudRenting solicitudRenting) {

        if(approvalRulesService.validateCIFCliente(solicitudRenting) &&
            approvalRulesService.validateInversion(solicitudRenting) &&
            approvalRulesService.validateInversionIngresos(solicitudRenting) &&
            approvalRulesService.validateScoringPersona(solicitudRenting) &&
            approvalRulesService.validateImpagoCuota(solicitudRenting) &&
            approvalRulesService.validateNationality(solicitudRenting) &&
            approvalRulesService.validateYearsExperience(solicitudRenting)) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean findAnyApproval(SolicitudRenting solicitudRenting) {
        if(this.approvalRulesService.validateCIFCliente(solicitudRenting) ||
                this.approvalRulesService.validateInversion(solicitudRenting) ||
                this.approvalRulesService.validateInversionIngresos(solicitudRenting) ||
                this.approvalRulesService.validateScoringPersona(solicitudRenting) ||
                this.approvalRulesService.validateImpagoCuota(solicitudRenting) ||
                this.approvalRulesService.validateNationality(solicitudRenting) ||
                this.approvalRulesService.validateYearsExperience(solicitudRenting)) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void totalResult(SolicitudRenting solicitudRenting) {

        if(this.findAnyDeny(solicitudRenting)) {
            if(this.findAllApproval(solicitudRenting)) {
                this.automaticResultMapper.insertApproval(solicitudRenting);

            }else if (this.findAnyApproval(solicitudRenting)) {
                this.automaticResultMapper.insertPendingResult(solicitudRenting);
            }else {
                this.automaticResultMapper.insertDeny(solicitudRenting);
            }
        }else {
            this.automaticResultMapper.insertDeny(solicitudRenting);

        }
    }
}

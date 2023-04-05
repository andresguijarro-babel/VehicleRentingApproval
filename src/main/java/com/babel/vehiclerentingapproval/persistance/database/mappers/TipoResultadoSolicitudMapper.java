package com.babel.vehiclerentingapproval.persistance.database.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TipoResultadoSolicitudMapper {

    @Select("SELECT DESCRIPCION FROM TIPO_RESULTADO_SOLICITUD tresultado INNER JOIN SOLICITUD_RENTING tSolicitud ON tSolicitud.COD_RESOLUCION = tresultado.COD_RESULTADO  WHERE tSolicitud.SOLICITUD_ID=#{idSolicitud}")
    String getEstadoSolicitud(int idSolicitud);

    @Select("SELECT COUNT (DESCRIPCION) FROM TIPO_RESULTADO_SOLICITUD tresultado INNER JOIN SOLICITUD_RENTING tSolicitud ON tSolicitud.COD_RESOLUCION = tresultado.COD_RESULTADO  WHERE tSolicitud.SOLICITUD_ID=#{idSolicitud}")
    int existeCodigoResolucion(int idSolicitud);

    @Select("SELECT COD_RESULTADO FROM TIPO_RESULTADO_SOLICITUD")
    List<String> getListaEstados();
}
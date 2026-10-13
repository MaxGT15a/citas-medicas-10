package com.max.citas.service;

import com.max.citas.dto.CitaRequest;
import com.max.citas.dto.CitaResponse;
import com.max.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);
}

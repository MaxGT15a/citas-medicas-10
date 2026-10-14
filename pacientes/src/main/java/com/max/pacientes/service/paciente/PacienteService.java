package com.max.pacientes.service.paciente;


import com.max.commons.dto.pacientes.PacienteRequest;
import com.max.commons.dto.pacientes.PacienteResponse;
import com.max.commons.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPorIdSinEstado(Long id);
}

package com.max.medicos.service;

import com.max.commons.dto.medicos.MedicoRequest;
import com.max.commons.dto.medicos.MedicoResponse;
import com.max.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}

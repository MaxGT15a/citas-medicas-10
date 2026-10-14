package com.max.pacientes.mapper;

import com.max.commons.dto.pacientes.PacienteRequest;
import com.max.commons.dto.pacientes.PacienteResponse;
import com.max.commons.enums.EstadoRegistro;
import com.max.commons.mapper.CommonMapper;
import com.max.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        return request == null ? null
                : Paciente.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente paciente) {
        return paciente == null ? null
                : new PacienteResponse(
                paciente.getId(),
                paciente.obtenerNombreCompleto(),
                paciente.getEdad(),
                paciente.getPeso(),
                paciente.getEstatura(),
                paciente.getImc(),
                paciente.getEmail(),
                paciente.getTelefono(),
                paciente.getDireccion(),
                paciente.getNumExpediente()
        );
    }
}
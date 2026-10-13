package com.max.pacientes.service.paciente;


import com.max.commons.dto.pacientes.PacienteRequest;
import com.max.commons.dto.pacientes.PacienteResponse;
import com.max.commons.enums.EstadoRegistro;
import com.max.commons.exceptions.RecursoNoEncontradoException;
import com.max.pacientes.entity.Paciente;
import com.max.pacientes.mapper.PacienteMapper;
import com.max.pacientes.repository.PacienteRepository;
import com.max.pacientes.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService{
    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listando pacientes...");
        return pacienteRepository.findAllByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPacienteActivo(id));
    }

    @Override
    public PacienteResponse obtenerPacientePorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPaciente(id));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        Paciente paciente = pacienteMapper.requestAEntidad(
                request,
                EstadoRegistro.obtenerPorDescripcion(request.estadoRegistro())
        );

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente creado con id: {}", paciente.getId());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerPaciente(id);
        EstadoRegistro estadoRegistro = EstadoRegistro.obtenerPorDescripcion(
                request.estadoRegistro()
        );

        if(paciente.cambiosEnDatos(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion(),
                estadoRegistro
        )){
            paciente.actualizar(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno(),
                    request.edad(),
                    request.peso(),
                    request.estatura(),
                    request.email(),
                    request.telefono(),
                    request.direccion(),
                    estadoRegistro
            );
            pacienteRepository.saveAndFlush(paciente);

            log.info("Paciente actualizado con id: {}", id);
        }

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPaciente(id);

        paciente.actualizarEstadoRegistro(EstadoRegistro.ELIMINADO);

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente eliminado con id:{}", id);
    }

    private Paciente obtenerPaciente(Long id){
        return ServiceUtils.obtenerEntidadOExcepcion(
                pacienteRepository,
                id,
                Paciente.class
        );
    }

    private Paciente obtenerPacienteActivo(Long id){
        return pacienteRepository.findByIdAndEstadoRegistro(
                id, EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Paciente no encontrado con id: " + id
                )
        );
    }
}

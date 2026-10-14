package com.max.citas.service;

import com.max.citas.dto.CitaRequest;
import com.max.citas.dto.CitaResponse;
import com.max.citas.entity.Cita;
import com.max.citas.enums.EstadoCita;
import com.max.citas.mapper.CitaMapper;
import com.max.citas.repository.CitaRepository;
import com.max.citas.utils.ServiceUtils;
import com.max.commons.clients.MedicoClient;
import com.max.commons.clients.PacienteClient;
import com.max.commons.dto.medicos.MedicoResponse;
import com.max.commons.dto.pacientes.PacienteResponse;
import com.max.commons.enums.DisponibilidadMedico;
import com.max.commons.enums.EstadoRegistro;
import com.max.commons.exceptions.ConflictoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    private final List<EstadoCita> estadosPermitidos = List.of(
            EstadoCita.PENDIENTE,
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

    @Override
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita ->
                        citaMapper.entidadAResponse(
                                cita,
                                obtenerPaciente(cita.getIdPaciente()),
                                obtenerMedico(cita.getIdMedico())
                        )
                ).toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCita(id);
        log.info("Obteniendo cita con id: {}", id);
        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteActivo(cita.getIdPaciente()),
                obtenerMedico(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita: {}", request);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        //hola profe 🤠

        validarMedicoDisponible(medico);

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        pacienteTieneCitaActiva(request.idPaciente());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(cita, paciente, medico
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        log.info("Actualizando cita con id: {}", id);

        Cita cita = obtenerCita(id);
        cita.validarActualizacionPermitida();

        if (cita.getEstadoCita() == EstadoCita.EN_CURSO) {
            throw new ConflictoException("No es posible actualizar la cita con id: " + id + " porque está en curso");
        }

        Long idMedicoAnterior = cita.getIdMedico();
        Long idPacienteAnterior = cita.getIdPaciente();

        boolean cambiaMedico = !idMedicoAnterior.equals(request.idMedico());
        boolean cambiaPaciente = !idPacienteAnterior.equals(request.idPaciente());

        MedicoResponse medicoActual = null;

        if (cambiaMedico) {
            log.info("El médico de la cita con id: {} ha cambiado de {} a {}", id, idMedicoAnterior, request.idMedico());
            medicoActual = obtenerMedicoActivo(request.idMedico());
            validarMedicoDisponible(medicoActual);
        }

        if (cambiaPaciente) {
            log.info("El paciente de la cita con id: {} ha cambiado de {} a {}", id, idPacienteAnterior, request.idPaciente());
            PacienteResponse nuevoPaciente = obtenerPacienteActivo(request.idPaciente());
            pacienteTieneCitaActiva(nuevoPaciente.id());
        }

        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());
        citaRepository.saveAndFlush(cita);

        if (cambiaMedico) {
            actualizarDisponibilidadMedico(idMedicoAnterior, DisponibilidadMedico.DISPONIBLE.getCodigo());
            actualizarDisponibilidadMedico(medicoActual.id(), DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
        }

        return citaMapper.entidadAResponse(
                cita,
                obtenerPaciente(cita.getIdPaciente()),
                medicoActual != null ? medicoActual : obtenerMedico(cita.getIdMedico())
        );
    }

    @Override
    public void actualizarEstadoCita(Long id, Long idEstado) {

        Cita cita = obtenerCita(id);

        log.info("Actualizando estado de la cita con id: {} a estado con id: {}", id, idEstado);

        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstado));

        citaRepository.saveAndFlush(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());

        log.info("Estado de la cita con id: {} actualizado a estado con id: {}", id, idEstado);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCita(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        citaRepository.saveAndFlush(cita);

        if(cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());
        log.info("Cita con id: {} eliminada", id);
    }

    @Override
    public void pacienteTieneCitaActiva(Long idPaciente) {
        log.info("Validando que el paciente con id: {} no tenga citas activas", idPaciente);

        if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(idPaciente, EstadoRegistro.ACTIVO, estadosPermitidos))
            throw new ConflictoException("El paciente con id: " + idPaciente + " ya tiene una cita activa");
    }

    @Override
    public void medicoTieneCitaActiva(Long idMedico) {
        log.info("Validando que el medico con id: {} no tenga citas activas", idMedico);

        if(citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(idMedico, EstadoRegistro.ACTIVO, estadosPermitidos))
            throw new ConflictoException("El medico con id: " + idMedico + " ya tiene una cita activa");
    }

    private Cita obtenerCita(Long id){
        return ServiceUtils.obtenerEntidadOExcepcion(citaRepository, id, Cita.class);
    }

    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Obteniendo medico activo con id: {}", id);
        return medicoClient.obtenerMedicoPorId(id).getBody();
    }

    private MedicoResponse obtenerMedico(Long id){
        log.info("Obteniendo medico con id: {}", id);
        return medicoClient.obtenerMedicoPorIdSinEstado(id).getBody();
    }

    private PacienteResponse obtenerPacienteActivo(Long id){
        log.info("Obteniendo paciente con id: {}", id);
        return pacienteClient.obtenerPacientePorIdActivo(id).getBody();
    }

    private PacienteResponse obtenerPaciente(Long id){
        log.info("Obteniendo paciente sin estado con id: {}", id);
        return pacienteClient.obtenerPorIdSinEstado(id).getBody();
    }

    private void validarMedicoDisponible(MedicoResponse medico){
        log.info("Validando que el medico con id: {} este activo y disponible", medico.id());

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El medico con id: " + medico.id() + " no esta disponible");
    }


    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponibilidad del medico con id: {} a {}", idMedico, idDisponibilidad);
        medicoClient.actualizarDisponibilidadMedicoPorCita(idMedico, idDisponibilidad);
        log.info("Disponibilidad del medico con id: {} actualizada a {}", idMedico, idDisponibilidad);
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }
}

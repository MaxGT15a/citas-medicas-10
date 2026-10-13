package com.max.citas.service;

import com.max.citas.dto.CitaRequest;
import com.max.citas.dto.CitaResponse;
import com.max.citas.entity.Cita;
import com.max.citas.enums.EstadoCita;
import com.max.citas.mapper.CitaMapper;
import com.max.citas.repository.CitaRepository;
import com.max.commons.clients.MedicoClient;
import com.max.commons.dto.medicos.MedicoResponse;
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
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCita0Exception(idCita);

        log.info("Actualizando estado de cita con id: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerPorCodigo(idEstadoCita));

        citaRepository.saveAndFlush(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(
                cita.getIdMedico(), cita.getEstadoCita());

        log.info("Cita actualizada con id: {}", cita.getId());

    }

    @Override
    public List<CitaResponse> listar() {

        log.info("Listando citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        null,
                        obtenerMedicoSinEstado(cita.getIdMedico())))
                .toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCita0Exception(id);

        return citaMapper.entidadAResponse(
                cita,
                null,
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando cita");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        validarMedicoActivoDisponible(medico);

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada con id: {}", cita.getId());

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                null,
                medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCita0Exception(id);

        if (cita.getEstadoCita() == EstadoCita.EN_CURSO)
            throw new ConflictoException("La cita está en curso y no se puede actualizar.");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        log.info("Actualizando cita con id: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada con id: {}", cita.getId());

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                null,
                medico);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCita0Exception(id);

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo());

        log.info("Cita eliminada con id: {}", cita.getId());
    }

    private Cita obtenerCita0Exception(Long id) {
        log.info("Obteniendo cita por id: {}", id);

        return citaRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Obteniendo medico activo por id: {}", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id){
        log.info("Obteniendo medico sin estado por id: {}", id);

        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico){
        log.info("Validando medico activo disponible");

        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El médico no está disponible para atender citas.");
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad del médico con id: {}", idMedico);

        medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);

        log.info("Disponibilidad del médico actualizada en servicio remoto con id: {}", idMedico);
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita nuevoEstado) {

        switch (nuevoEstado){
            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            case EN_CURSO ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
        }
    }
}

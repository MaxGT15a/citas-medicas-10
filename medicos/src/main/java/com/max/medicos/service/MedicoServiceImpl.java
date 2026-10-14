package com.max.medicos.service;

import com.max.commons.clients.CitaClient;
import com.max.commons.dto.medicos.MedicoRequest;
import com.max.commons.dto.medicos.MedicoResponse;
import com.max.commons.enums.DisponibilidadMedico;
import com.max.commons.enums.EspecialidadMedico;
import com.max.commons.enums.EstadoRegistro;
import com.max.commons.exceptions.ConflictoException;
import com.max.commons.exceptions.DatoInvalidoException;
import com.max.commons.exceptions.RecursoNoEncontradoException;
import com.max.medicos.entity.Medico;
import com.max.medicos.mapper.MedicoMapper;
import com.max.medicos.repository.MedicoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;

    private final MedicoMapper medicoMapper;

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando medicos");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Obteniendo medico por id sin estado: {}", id);
        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado")));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando medico nuevo: {}", request);

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request,
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Medico nuevo {} registrado exitosamente", medico.getNombre());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoEliminableOActualizable(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando medico: {}", medico);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Medico {} actualizado exitosamente", medico.getNombre());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Medico: {}", medico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico anteriorDisponibilidad = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del medico {} actualizada exitosamente: {} -> {}",
                idMedico, anteriorDisponibilidad, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoEliminableOActualizable(id);

        log.info("Eliminando medico: {}", medico);

        medico.eliminarMedico();

        medicoRepository.saveAndFlush(medico);

        log.info("Medico {} crucificado exitosamente", medico.getNombre());
    }

    private Medico obtenerMedicoActivoPorId(Long id) {
        log.info("Obteniendo medico activo por id: {}", id);
        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validando datos unicos para medico: {}", request);

        log.info("Validando cambio en email unico");
        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un medico con el mismo email: "
                    + request.email());

        log.info("Validando cambio en telefono unico");
        if(medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un medico activo con el mismo teléfono: "
                    + request.telefono());

        log.info("Validando cambio en cedula profesional unico");
        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO))
            throw new DatoInvalidoException("Ya existe un medico activo con la misma cédula profesional: "
                    + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){
        log.info("Validando cambios unicos para medico: {}", request);

        log.info("Validando email unico");
        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un medico con el mismo email: "
                    + request.email());

        log.info("Validando telefono unico");
        if(medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un medico activo con el mismo teléfono: "
                    + request.telefono());

        log.info("Validando cedula profesional unico");
        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional(), EstadoRegistro.ACTIVO, id))
            throw new DatoInvalidoException("Ya existe un medico activo con la misma cédula profesional: "
                    + request.cedulaProfesional());
    }

    private void validarMedicoEliminableOActualizable(Long id){
        try {
            citaClient.medicoTieneCitaActiva(id);
        } catch (FeignException.Conflict e){
            throw new ConflictoException("El medico con id: " + id + " tiene citas activas y no puede ser eliminado o actualizado");
        }
    }
}

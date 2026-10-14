package com.max.citas.repository;

import com.max.citas.entity.Cita;
import com.max.citas.enums.EstadoCita;
import com.max.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadoCita
    );

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            List<EstadoCita> estadoCita
    );
}

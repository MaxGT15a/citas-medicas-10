package com.max.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.max.commons.dto.medicos.DatosMedico;
import com.max.commons.dto.pacientes.DatosPaciente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Información de una cita médica registrada")
public record CitaResponse(

        @Schema(description = "ID de la cita", example = "1234567890")
        Long id,

        @Schema(description = "Información del paciente")
        DatosPaciente datosPaciente,

        @Schema(description = "Información del médico")
        DatosMedico datosMedico,

        @Schema(description = "Fecha y hora de la cita", example = "25/09/2026 10:30",
                type = "string",
               format = "date-time"
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(description = "Síntomas de la cita", example = "Fiebre, dolor de cabeza")
        String sintomas,

        @Schema(description = "Estado actual de la cita", example = "Confirmada por el paciente")
        String estadoCita
) {
}

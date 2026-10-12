package com.max.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "MedicoResponse", description = "Response DTO for Medico entity")
public record MedicoResponse(

        @Schema(description = "ID del medico", example = "1")
        Long id,

        @Schema(description = "Nombre completo del medico", example = "Mauricio García Ramírez")
        String nombre,

        @Schema(description = "La edad del medico", example = "20")
        Short edad,

        @Schema(description = "Email del medico", example = "laura.martinez@medical.com")
        String email,

        @Schema(description = "El teléfono del medico", example = "1234567890")
        String telefono,

        @Schema(description = "La cedula profesional del medico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "La especialidad del médico", example = "Cardiología")
        String especialidad,

        @Schema(description = "La disponibilidad del médico", example = "ACTIVO", minimum = "1")
        String disponibilidad,

        @Schema(description = "El id de la disponibilidad del médico", example = "1")
        Long idDisponibilidad
) {
}

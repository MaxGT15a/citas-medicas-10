package com.max.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "MedicoResponse", description = "Response DTO for Medico entity")
public record MedicoResponse(

        @Schema(description = "ID del medico", example = "1")
        Long id,

        @Schema(description = "Nombre del medico", example = "Mauricio")
        String nombre,

        @Schema(description = "El apellido paterno del medico", example = "García")
        String apellidoPaterno,

        @Schema(description = "El apellido materno del medico", example = "Ramírez")
        String apellidoMaterno,

        @Schema(description = "La edad del medico", example = "20")
        Short edad,

        @Schema(description = "Email del medico", example = "laura.martinez@medical.com")
        String email,

        @Schema(description = "El teléfono del medico", example = "1234567890")
        String telefono,

        @Schema(description = "La cedula profesional del medico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "La especialidad del médico", example = "5", minimum = "1")
        Long idEspecialidad
) {
}

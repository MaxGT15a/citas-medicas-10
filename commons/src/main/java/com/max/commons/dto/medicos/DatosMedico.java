package com.max.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del médico")
public record DatosMedico(

        @Schema(description = "Nombre del médico", example = "Juan Pérez")
        String nombre,

        @Schema(description = "Cédula profesional del médico", example = "123456789")
        String cedulaProfesional,

        @Schema(description = "Especialidad del médico", example = "Cardiología")
        String especialidad
) {
}

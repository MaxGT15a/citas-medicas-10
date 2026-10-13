package com.max.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del paciente")
public record DatosPaciente(

        @Schema(description = "Nombre del paciente", example = "Juan Pérez López")
        String nombre,

        @Schema(description = "Número de expediente", example = "1x2x3x4x5x67x8x9x0x")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "18")
        String edad,

        @Schema(description = "Peso del paciente", example = "80.5")
        String peso,

        @Schema(description = "Estatura del paciente", example = "1.75")
        String estatura,

        @Schema(description = "IMC del paciente", example = "25.5")
        String imc,

        @Schema(description = "Telefono del paciente", example = "1234567890")
        String telefono
) {
}

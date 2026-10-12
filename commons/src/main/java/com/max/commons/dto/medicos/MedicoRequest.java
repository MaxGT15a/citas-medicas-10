package com.max.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request para criar um médico")
public record MedicoRequest(
        @Schema(description = "Nombre del medico", example = "Mauricio")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "El apellido paterno del medico", example = "García")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "El apellido materno del medico", example = "Ramírez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "La edad del medico", example = "20")
        @NotNull(message = "La edad es requerida")
        @Positive(message = "La edad debe ser un numero positivo")
        Short edad,

        @Schema(description = "Email del medico", example = "laura.martinez@medical.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
        @Email(message = "El email debe tener un formato valido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "El teléfono del medico", example = "1234567890",
            pattern = "^[0-9]{10}$")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener entre 10 digitos")
        String telefono,

        @Schema(description = "La cedula profesional del medico", example = "123456789012")
        @NotBlank(message = "La cedula es requerida")
        @Size(min = 12, max = 12, message = "La cedula debe de tener 12 cacracteres")
        String cedulaProfesional,

        @Schema(description = "La especialidad del médico", example = "5", minimum = "1")
        @NotNull(message = "La especialidad es requerida")
        @Positive(message = "La especialidad debe ser positiva")
        Long idEspecialidad
) {}

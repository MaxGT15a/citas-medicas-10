package com.max.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para gestionar un paciente")
public record PacienteRequest(
        @Schema(description = "Nombre del paciente", example = "Mauricio")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "El apellido paterno del paciente", example = "García")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "El apellido materno del paciente", example = "Ramírez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "La edad del paciente", example = "20")
        @NotNull(message = "La edad es requerida")
        @Positive(message = "La edad debe ser un numero positivo")
        Short edad,

        @Schema(description = "Peso del paciente", example = "70.5")
        @NotNull(message = "El peso es requerida")
        @Positive(message = "El peso debe ser un numero positivo")
        Double peso,

        @Schema(description = "Estatura del paciente", example = "1.8")
        @NotNull(message = "La estatura es requerida")
        @Positive(message = "La estatura debe ser un numero positivo")
        Double estatura,

        @Schema(description = "Email del paciente", example = "laura.martinez@medical.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
        @Email(message = "El email debe tener un formato valido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "El telefono del paciente", example = "9191011001")
        @Pattern(regexp = "^[0-9]{10}", message = "El telefono debe tener 10 caracteres numericos")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "5A Oriente Norte num 123, Colonia San Pedro, C.P 12345, Puebla, Pue.")
        @NotNull(message = "La direccion es requerida")
        @NotBlank(message = "La direccion es requerida")
        @Size(min = 20, max = 150, message = "La direccion debe tener entre 20 y 150 caracteres")
        String direccion,

        @Schema(description = "Estado del registro", example = "Activo")
        @NotNull(message = "La direccion es requerida")
        @NotBlank(message = "La direccion es requerida")
        @Size(min = 1, max = 150, message = "La direccion debe tener entre 1 y 150 caracteres")
        String estadoRegistro

) { }
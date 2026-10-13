package com.max.citas.controller;

import com.max.citas.dto.CitaRequest;
import com.max.citas.dto.CitaResponse;
import com.max.citas.service.CitaService;
import com.max.commons.controller.CrudController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Citas", description = "API para gestionar citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {

    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(summary = "Actualizar estado de cita",
    description = "Actualiza el estado de una cita por su ID")
    @ApiResponse(
        responseCode = "204",
        description = "Estado de cita actualizado correctamente"
    )
    @ApiResponse(
        responseCode = "409",
        description = "El estado no puede actualizarse debido a regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = CitaResponse.class)
            )
    )
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
           @PathVariable @Positive(message = "El ID de la cita es requerido") Long idCita,
           @PathVariable @Positive(message = "El ID del estado es requerido") Long idEstado) {

        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }
}

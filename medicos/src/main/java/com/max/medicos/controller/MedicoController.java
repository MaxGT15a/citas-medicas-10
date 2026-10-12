package com.max.medicos.controller;

import com.max.commons.controller.CrudController;
import com.max.commons.docs.ProblemaDoc;
import com.max.commons.dto.medicos.MedicoRequest;
import com.max.commons.dto.medicos.MedicoResponse;
import com.max.medicos.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "APIMedicos", description = "Endpoints para gerenciar medicos")
public class MedicoController extends CrudController<MedicoRequest, MedicoResponse, MedicoService> {

    public MedicoController(MedicoService service) {
        super(service);
    }

    @GetMapping("/id-medico/{id}")
    @Operation(summary = "Obtener medico por id sin estado")
    @ApiResponse(
        responseCode = "200",
        description = "Medico sin estado encontrado")
    @ApiResponse(
            responseCode = "404",
            description = "Medico sin estado no encontrado",
            content = @Content(mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemaDoc.class)))

    public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive(message = "El id del medico debe ser positivo") Long id) {
        return ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    @Operation(summary = "Actualizar disponibilidad de medico")
    @ApiResponse(
        responseCode = "204",
        description = "Disponibilidad actualizada")
    @ApiResponse(
        responseCode = "409",
        description = "Medico o disponibilidad no encontrados",
        content = @Content(mediaType = "application/problem+json",
        schema = @Schema(implementation = ProblemaDoc.class)))

    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive(message = "El id del medico debe ser positivo")
            Long idMedico,
            @PathVariable @Positive(message = "El id de la disponibilidad debe ser positivo")
            Long idDisponibilidad) {

        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build(); // No content response
    }
}

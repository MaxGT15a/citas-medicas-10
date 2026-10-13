package com.max.pacientes.controller;

import com.max.commons.controller.CrudController;
import com.max.commons.dto.pacientes.PacienteRequest;
import com.max.commons.dto.pacientes.PacienteResponse;
import com.max.pacientes.service.paciente.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "APIPacientes", description = "Gestion de pacientes medicos")
public class PacienteController extends CrudController<PacienteRequest, PacienteResponse, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }


    @GetMapping("/id-paciente/{id}")
    @Operation(summary = "Obtener paciente por id")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado")
    public ResponseEntity<PacienteResponse> obtenerPorId(
            @PathVariable @Positive(message = "El id debe ser positivo") Long id
    ){
        return ResponseEntity.ok(service.obtenerPacientePorId(id));
    }
}
package com.max.medicos.entity;

import com.max.commons.enums.DisponibilidadMedico;
import com.max.commons.enums.EspecialidadMedico;
import com.max.commons.enums.EstadoRegistro;
import com.max.commons.exceptions.DatoInvalidoException;
import com.max.commons.utils.StringCustomUtils;
import com.max.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.Locale;

@Entity
@Table(name = "medicos")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD")
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12, nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                              Short edad, String email, String telefono, String cedulaProfesional){

        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe de tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe de tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe de tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe de tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El teléfono es requerido y debe de tener 10 dígitos");
        StringCustomUtils.validarTamanio(cedulaProfesional, 12, 12,
                "La cédula profesional es requerida y debe de tener 12 dígitos");
        ValoresNumericosUtils.validarRangoShort(edad, 1, 120,
                "La edad debe de estar entre 18 y 120 años");
    }

    private void validarNoEliminado(){
        if (estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El médico está eliminado");
    }

    public void eliminarMedico(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEspecialidad(EspecialidadMedico nuevaEspecialidad){
        validarNoEliminado();

        if (nuevaEspecialidad == null)
            throw new IllegalArgumentException("La nueva especialidad es requerida");
        this.especialidad = nuevaEspecialidad;
    }

    public void actualizarDisponibilidad(DisponibilidadMedico nuevaDisponibilidad){
        validarNoEliminado();

        if (nuevaDisponibilidad == null)
            throw new IllegalArgumentException("La nueva disponibilidad es requerida");

        this.disponibilidad = nuevaDisponibilidad;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                              Short edad, String email, String telefono, String cedulaProfesional,
                           EspecialidadMedico especialidad){
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad,
                email, telefono, cedulaProfesional);

        actualizarEspecialidad(especialidad);
        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();

    }

    public static Medico crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, String email, String telefono, String cedulaProfesional){

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad,
                email, telefono, cedulaProfesional);

        return Medico.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .cedulaProfesional(cedulaProfesional.trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

    }
}

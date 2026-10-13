package com.max.pacientes.entity;

import com.max.commons.enums.EstadoRegistro;
import com.max.commons.exceptions.DatoInvalidoException;
import com.max.commons.utils.StringCustomUtils;
import com.max.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE ")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", length = 20, nullable = false)
    private String numExpediente;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode((SqlTypes.NAMED_ENUM))
    @Column(name = "ESTADO_REGISTRO", length = 10, nullable = false)
    private EstadoRegistro estadoRegistro;

    public static void validarDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion
    ){
        // Nombre, Apellido Paterno y Apellido Materno
        StringCustomUtils.validarTamanio(
                nombre, 1, 50,
                "El nombre es requerido y debe tener entre 5 y 50 caracteres"
        );
        StringCustomUtils.validarTamanio(
                apellidoPaterno, 1 ,50,
                "El apellido paterno es requerido y debe tener entre 5 y 50 caracteres"
        );
        StringCustomUtils.validarTamanio(
                apellidoMaterno, 1 ,50,
                "El apellido materno es requerido y debe tener entre 5 y 50 caracteres"
        );

        // Validar edad
        ValoresNumericosUtils.validarPositivo(
                edad,
                "La edad es requerida y debe ser positiva"
        );
        ValoresNumericosUtils.validarRango(
                edad, 1, 100,
                "La edad es requerida y debe estar entre 1 y 100 años"
        );

        // Validar peso
        ValoresNumericosUtils.validarPositivo(
                peso,
                "El peso es requerido y debe ser positiva"
        );
        ValoresNumericosUtils.validarRango(
                peso, 0.1, 200,
                "El peso es requerido y debe estar entre 0.1 y 200 kg"
        );

        // Validar estatura
        ValoresNumericosUtils.validarPositivo(
                estatura,
                "La estatura es requerida y debe ser positiva"
        );
        ValoresNumericosUtils.validarRango(
                estatura, 1, 2,
                "La estatura es requerida y debe estar entre 1 mts y 2 mts"
        );

        StringCustomUtils.validarTamanio(
                email, 5, 100,
                "El email es requerido y debe contener entre 5 y 100 caracteres"
        );

        StringCustomUtils.validarTextoNumerico(
                telefono,
                "El telefono es requerido y debe contener solo numeros"
        );

        StringCustomUtils.validarTamanio(
                direccion, 1, 150,
                "La direccion es requerida y debe tener entre 20 y 150 caracteres"
        );
    }

    public boolean cambiosEnDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion,
            EstadoRegistro estadoRegistro
    ){
        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                edad,
                peso,
                estatura,
                email,
                telefono,
                direccion
        );

        return !this.nombre.equals(nombre.trim()) ||
            !this.apellidoPaterno.equals(apellidoMaterno.trim()) ||
            !this.apellidoMaterno.equals(apellidoMaterno.trim()) ||
            this.edad != edad ||
            this.peso != peso ||
            this.estatura != estatura ||
            !this.telefono.equals(telefono.trim()) ||
            !this.direccion.equals(direccion.trim()) ||
            !this.estadoRegistro.equals(estadoRegistro);
    }

    public void actualizar(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion,
            EstadoRegistro estadoRegistro
    ){
        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                edad,
                peso,
                estatura,
                email,
                telefono,
                direccion
        );

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = peso / (estatura * estatura);
        this.email = email.toLowerCase().trim();
        this.numExpediente = generarNumeroDeExpediente(telefono.trim());
        this.telefono = telefono.trim();
        this.direccion = direccion.trim();
        this.estadoRegistro = estadoRegistro;
    }

    public void actualizarEstadoRegistro(EstadoRegistro estadoRegistro){
        if(estadoRegistro == null)
            throw new DatoInvalidoException("El estado de registro no puede ser nulo");

        this.estadoRegistro = estadoRegistro;
    }

    private static String generarNumeroDeExpediente(String telefono){
        // Número de expediente: se genera a partir del teléfono.
        // Según el ejemplo del anexo, cada dígito del teléfono va
        // seguido de una X (5512345678 → 5X5X1X2X3X4X5X6X7X8X).
        return telefono.chars()
                .mapToObj(c -> (char) c + "X")
                .reduce("", String::concat);
    }

    public void asignarEstadoRegistro(EstadoRegistro estadoRegistro){
        if (estadoRegistro == null)
            throw new DatoInvalidoException("El estado de registro no puede ser nulo");

        this.estadoRegistro = estadoRegistro;
    }

    public static Paciente crear(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            short edad,
            Double peso,
            Double estatura,
            String email,
            String telefono,
            String direccion
    ){
        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                edad,
                peso,
                estatura,
                email,
                telefono,
                direccion
        );
        return Paciente.builder()
                .nombre(nombre)
                .apellidoPaterno(apellidoPaterno)
                .apellidoMaterno(apellidoMaterno)
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(peso / (estatura * estatura))
                .email(email)
                .numExpediente(generarNumeroDeExpediente(telefono.trim()))
                .telefono(telefono)
                .direccion(direccion)
                .build();

    }
}

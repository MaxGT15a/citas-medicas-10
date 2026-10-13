package com.max.citas.enums;

import com.max.commons.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum EstadoCita {
    PENDIENTE(1L, "Pendiente", true, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(CONFIRMADA, CANCELADA);
        }
    },
    CONFIRMADA(2L, "Confirmada", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(EN_CURSO, CANCELADA);
        }
    },
    EN_CURSO(3L, "En curso", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },
    FINALIZADA(4L, "Finalizada", false, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    },
    CANCELADA(5L, "Cancelada", false, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    };

    private final Long codigo;
    private final String descripcion;
    private final boolean actualizable;
    private final boolean eliminable;

    public abstract Set<EstadoCita> puedeCambiar();
    public boolean puedeCambiarA(EstadoCita estadoCita){
        return puedeCambiar().contains(estadoCita);
    }

    public static EstadoCita obtenerPorCodigo(Long codigo) {
        for (EstadoCita estado : values()) {
            if (Objects.equals(estado.codigo, codigo)) {
                return estado;
            }
        }
        throw new RecursoNoEncontradoException("Código de cita no válido: " + codigo);
    }

}

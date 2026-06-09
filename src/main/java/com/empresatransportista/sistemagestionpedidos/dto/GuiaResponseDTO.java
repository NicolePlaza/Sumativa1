package com.empresatransportista.sistemagestionpedidos.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.empresatransportista.sistemagestionpedidos.model.EstadoGuia;

@Getter
@Setter
@Builder
public class GuiaResponseDTO {
    private Long id;
    private String numeroGuia;
    private LocalDate fecha;
    private String transportistaCodigo;
    private String transportistaNombre; 
    private String origen;
    private String destino;
    private String descripcion;
    private Double peso;
    private EstadoGuia estado;
    private String s3Key;
    private LocalDateTime createdAt;
}
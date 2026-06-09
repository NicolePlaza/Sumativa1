package com.empresatransportista.sistemagestionpedidos.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class GuiaRequestDTO {
    private String numeroGuia;
    private LocalDate fecha;
    private Long transportistaId;
    private String origen;
    private String destino;
    private String descripcion;
    private Double peso;
}

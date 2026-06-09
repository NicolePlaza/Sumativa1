package com.empresatransportista.sistemagestionpedidos.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "guias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroGuia;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id", nullable = false)
    private Transportista transportista;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    private String descripcion;

    private Double peso;

    @Enumerated(EnumType.STRING)
    private EstadoGuia estado;

    private String s3Key;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoGuia.CREADA;
        }
    }
}

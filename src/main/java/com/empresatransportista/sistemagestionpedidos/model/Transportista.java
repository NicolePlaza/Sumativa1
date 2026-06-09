package com.empresatransportista.sistemagestionpedidos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transportistas")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;

    private String rut;
    private String email;
}
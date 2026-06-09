package com.empresatransportista.sistemagestionpedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresatransportista.sistemagestionpedidos.model.Guia;

import java.time.LocalDate;
import java.util.List;  

public interface GuiaRepository extends JpaRepository<Guia, Long> {
    List<Guia> findByTransportistaCodigoAndFecha(String codigo, LocalDate fecha);
    List<Guia> findByTransportistaCodigo(String codigo);
    List<Guia> findByFecha(LocalDate fecha);
}
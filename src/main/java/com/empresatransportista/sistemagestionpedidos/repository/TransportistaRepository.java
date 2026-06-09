package com.empresatransportista.sistemagestionpedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresatransportista.sistemagestionpedidos.model.Transportista;

import java.util.Optional;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    Optional<Transportista> findByCodigo(String codigo);
    
}

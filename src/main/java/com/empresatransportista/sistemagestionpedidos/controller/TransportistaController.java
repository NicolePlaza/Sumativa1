package com.empresatransportista.sistemagestionpedidos.controller;

import com.empresatransportista.sistemagestionpedidos.model.Transportista;
import com.empresatransportista.sistemagestionpedidos.repository.TransportistaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {
    
    private final TransportistaRepository repository;

    @PostMapping
    public Transportista crear(@RequestBody Transportista transportista) {
        return repository.save(transportista);
    }

    @GetMapping
    public List<Transportista> listar() {
        return repository.findAll();
    }
}

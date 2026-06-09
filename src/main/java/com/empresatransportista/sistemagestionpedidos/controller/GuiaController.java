package com.empresatransportista.sistemagestionpedidos.controller;

import com.empresatransportista.sistemagestionpedidos.dto.GuiaRequestDTO;
import com.empresatransportista.sistemagestionpedidos.dto.GuiaResponseDTO;
import com.empresatransportista.sistemagestionpedidos.service.GuiaService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
@RequiredArgsConstructor

public class GuiaController {
    private final GuiaService guiaService;

    @PostMapping
    public ResponseEntity<GuiaResponseDTO> crear(@RequestBody GuiaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guiaService.crear(dto));
    }

    @GetMapping
    public List <GuiaResponseDTO> listar(
            @RequestParam(required = false) String transportista,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return guiaService.listar(transportista, fecha);
    }

    @GetMapping("/{id}")
    public GuiaResponseDTO obtener(@PathVariable Long id) {
        return guiaService.obtener(id);
    }

    @PutMapping("/{id}")
    public GuiaResponseDTO actualizar(@PathVariable Long id, @RequestBody GuiaRequestDTO dto) {
        return guiaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        guiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> descargar(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Token", required = false) String userToken
    ) {
        byte[] contenido = guiaService.descargar(id, userToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia" + id + ".pdf")
            .body(contenido);
    }
}
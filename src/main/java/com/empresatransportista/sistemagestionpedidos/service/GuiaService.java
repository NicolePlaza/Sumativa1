package com.empresatransportista.sistemagestionpedidos.service;

import com.empresatransportista.sistemagestionpedidos.dto.GuiaRequestDTO;
import com.empresatransportista.sistemagestionpedidos.dto.GuiaResponseDTO;
import com.empresatransportista.sistemagestionpedidos.model.Guia;
import com.empresatransportista.sistemagestionpedidos.model.Transportista;
import com.empresatransportista.sistemagestionpedidos.repository.GuiaRepository;
import com.empresatransportista.sistemagestionpedidos.repository.TransportistaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaService {
    
    private final GuiaRepository guiaRepository;
    private final TransportistaRepository transportistaRepository;
    private final PdfService pdfService;

    public GuiaResponseDTO crear(GuiaRequestDTO dto){
        Transportista transportista = transportistaRepository.findById(dto.getTransportistaId())
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));

        Guia guia = Guia.builder()
                .numeroGuia(dto.getNumeroGuia())
                .fecha(dto.getFecha())
                .transportista(transportista)
                .origen(dto.getOrigen())
                .destino(dto.getDestino())
                .descripcion(dto.getDescripcion())
                .peso(dto.getPeso())
                .build();
        
        Guia guiaGuardada = guiaRepository.save(guia);

        try {
            pdfService.generarPdf(guiaGuardada);
        }catch (Exception e){
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }

        return toResponseDTO(guiaRepository.save(guia));
    }

    public List<GuiaResponseDTO> listar(String codigoTransportista, LocalDate fecha) {
        List<Guia> resultado;
        if (codigoTransportista != null && fecha != null) {
            resultado = guiaRepository.findByTransportistaCodigoAndFecha(codigoTransportista, fecha);
        } else if (codigoTransportista != null) {
            resultado = guiaRepository.findByTransportistaCodigo(codigoTransportista);
        } else if (fecha != null) {
            resultado = guiaRepository.findByFecha(fecha);
        } else {
            resultado = guiaRepository.findAll();
        }
        return resultado.stream().map(this::toResponseDTO).toList();
    }

    public GuiaResponseDTO obtener(Long id) {
        return guiaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
    }

    public GuiaResponseDTO actualizar(Long id, GuiaRequestDTO dto) {
        Guia guia = guiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

        if (dto.getTransportistaId() != null) {
            Transportista transportista = transportistaRepository.findById(dto.getTransportistaId())
                    .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));
            guia.setTransportista(transportista);
        }

        guia.setNumeroGuia(dto.getNumeroGuia());
        guia.setFecha(dto.getFecha());
        guia.setOrigen(dto.getOrigen());
        guia.setDestino(dto.getDestino());
        guia.setDescripcion(dto.getDescripcion());
        guia.setPeso(dto.getPeso());

        return toResponseDTO(guiaRepository.save(guia));
    }

    public void eliminar(Long id) {
        if (!guiaRepository.existsById(id)) {
            throw new RuntimeException("Guía no encontrada");
        }
        guiaRepository.deleteById(id);
    }

    private GuiaResponseDTO toResponseDTO(Guia guia) {
        return GuiaResponseDTO.builder()
                .id(guia.getId())
                .numeroGuia(guia.getNumeroGuia())
                .fecha(guia.getFecha())
                .transportistaCodigo(guia.getTransportista().getCodigo())

                .origen(guia.getOrigen())
                .destino(guia.getDestino())
                .descripcion(guia.getDescripcion())
                .peso(guia.getPeso())
                .build();

    }
}
package com.empresatransportista.sistemagestionpedidos.service;

import com.empresatransportista.sistemagestionpedidos.dto.GuiaRequestDTO;
import com.empresatransportista.sistemagestionpedidos.dto.GuiaResponseDTO;
import com.empresatransportista.sistemagestionpedidos.model.Guia;
import com.empresatransportista.sistemagestionpedidos.model.Transportista;
import com.empresatransportista.sistemagestionpedidos.repository.GuiaRepository;
import com.empresatransportista.sistemagestionpedidos.repository.TransportistaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuiaService {
    
    private final GuiaRepository guiaRepository;
    private final TransportistaRepository transportistaRepository;
    private final PdfService pdfService;
    private final S3Service s3Service;

public GuiaResponseDTO crear(GuiaRequestDTO dto) {
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
        Path pdfPath = pdfService.generarPdf(guiaGuardada);

        String s3Key = String.format("%s/%s/guia%d.pdf",
            guiaGuardada.getFecha().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            transportista.getCodigo(),
            guiaGuardada.getId());

        s3Service.upload(pdfPath, s3Key);

        guiaGuardada.setS3Key(s3Key);
        guiaGuardada = guiaRepository.save(guiaGuardada);

    } catch (Exception e) {
        throw new RuntimeException("Error procesando guía: " + e.getMessage(), e);
    }

    return toResponseDTO(guiaGuardada);
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
    Guia guia = guiaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

    // Borrar de S3 si existe
    if (guia.getS3Key() != null) {
        try {
            s3Service.delete(guia.getS3Key());
        } catch (Exception e) {
            System.err.println("No se pudo borrar de S3: " + e.getMessage());
        }
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

    public byte[] descargar(Long id, String userToken) {
    Guia guia = guiaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Guía no encontrada"));

    // Validación simple de permisos (en producción usarías JWT real)
    if (userToken == null || userToken.isBlank()) {
        throw new RuntimeException("Acceso denegado: se requiere autenticación");
    }

    if (guia.getS3Key() == null) {
        throw new RuntimeException("La guía no tiene PDF asociado en S3");
    }

    return s3Service.download(guia.getS3Key());
}


}
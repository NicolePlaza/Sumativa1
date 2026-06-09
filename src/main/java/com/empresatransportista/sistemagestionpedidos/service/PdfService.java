package com.empresatransportista.sistemagestionpedidos.service;

import com.empresatransportista.sistemagestionpedidos.model.Guia;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
    @Value ("${app.storage.temp-path}")
    private String tempPath;

    public Path generarPdf(Guia guia) throws Exception {
        Path carpeta = Paths.get(tempPath);
        Files.createDirectories(carpeta);

        String fileName = "guia" + guia.getId() + " .pdf";
        Path filePath = carpeta.resolve(fileName);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
        document.open();

        Font tituloFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.BLACK);
        Paragraph titulo = new Paragraph("GUIA DE DESPACHO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20f);
        document.add(titulo);

        Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 12);

        document.add(linea("N° Guia:", guia.getNumeroGuia(), labelFont, valueFont));
        document.add(linea("Fecha:", guia.getFecha().format(DateTimeFormatter.ISO_DATE), labelFont, valueFont));
        document.add(linea("Transportista:", guia.getTransportista().getNombre() + " (" + guia.getTransportista().getCodigo() + ")", labelFont, valueFont));
        document.add(linea("RUT Transportista:", guia.getTransportista().getRut(), labelFont, valueFont));
        document.add(linea("Origen:", guia.getOrigen(), labelFont, valueFont));
        document.add(linea("Destino:", guia.getDestino(), labelFont, valueFont));
        document.add(linea("Descripcion:", guia.getDescripcion(), labelFont, valueFont));
        document.add(linea("Peso (kg):", String.valueOf(guia.getPeso()), labelFont, valueFont));
        document.add(linea("Estado:", guia.getEstado().toString(), labelFont, valueFont));

        Paragraph footer = new Paragraph(
            "\nDocumento generado automáticamente - " + guia.getCreatedAt(),
            new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return filePath;
    }

    private Paragraph linea(String label, String value, Font labelFont, Font valueFont){
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value == null ? "-" : value, valueFont));
        p.setSpacingAfter(8f);
        return p;
    }
}
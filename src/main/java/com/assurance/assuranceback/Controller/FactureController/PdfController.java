package com.assurance.assuranceback.Controller.FactureController;

import com.assurance.assuranceback.Services.FactureService.PdfService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@AllArgsConstructor
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<InputStreamResource> downloadFacturePdf(@PathVariable Long factureId) {
        byte[] pdfBytes = pdfService.generateFacturePdf(factureId);
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=facture_" + factureId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
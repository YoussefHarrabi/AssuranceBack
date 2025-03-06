package com.assurance.assuranceback.Controller.FactureController;
import com.assurance.assuranceback.Services.FactureService.IQrCodeService;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/qrcode")
public class QrCodeController {

    private final IQrCodeService qrCodeService;

    @GetMapping("/facture/{factureId}")
    public ResponseEntity<InputStreamResource> generateQrCode(@PathVariable Long factureId) throws WriterException, IOException {
        String qrText = "http://localhost:4200/facture/download/" + factureId; // URL de téléchargement du PDF
        byte[] qrCode = qrCodeService.generateQrCode(qrText, 300, 300);
        ByteArrayInputStream bis = new ByteArrayInputStream(qrCode);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=qrcode_" + factureId + ".png");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(bis));
    }
}
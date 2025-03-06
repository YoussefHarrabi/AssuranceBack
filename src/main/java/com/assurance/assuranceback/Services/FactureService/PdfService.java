package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Repository.FactureRepository.FactureRepos;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@AllArgsConstructor
public class PdfService {

    @Autowired
    private FactureRepos factureRepos;

    public byte[] generateFacturePdf(Long factureId) {
        Facture facture = factureRepos.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add company logo
        // Image logo = new Image(ImageDataFactory.create("path/to/logo.png"));
        // logo.setWidth(UnitValue.createPercentValue(50));
        // document.add(logo);

        // Add facture details
        document.add(new Paragraph("Facture").setFontSize(20).setBold().setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell().add(new Paragraph("Facture ID:")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(facture.getFactureId()))));
        table.addCell(new Cell().add(new Paragraph("Date d'émission:")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph(facture.getDateEmission().toString())));
        table.addCell(new Cell().add(new Paragraph("Montant:")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph(String.format("%.2f", facture.getMontant()))));
        table.addCell(new Cell().add(new Paragraph("Statut:")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph(facture.getFactureStatut().toString())));
        table.addCell(new Cell().add(new Paragraph("Utilisateur:")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph(facture.getUser().getFirstName() + " " + facture.getUser().getLastName())));

        document.add(table);

        // Add paiement details if any
        if (!facture.getPaiements().isEmpty()) {
            document.add(new Paragraph("Détails des paiements").setFontSize(18).setBold().setMarginTop(20));

            Table paiementTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2}));
            paiementTable.setWidth(UnitValue.createPercentValue(100));

            paiementTable.addHeaderCell(new Cell().add(new Paragraph("Paiement ID").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            paiementTable.addHeaderCell(new Cell().add(new Paragraph("Date de paiement").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            paiementTable.addHeaderCell(new Cell().add(new Paragraph("Montant de paiement").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            paiementTable.addHeaderCell(new Cell().add(new Paragraph("Méthode de paiement").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            facture.getPaiements().forEach(paiement -> {
                paiementTable.addCell(new Cell().add(new Paragraph(String.valueOf(paiement.getId()))));
                paiementTable.addCell(new Cell().add(new Paragraph(paiement.getDatePaiement().toString())));
                paiementTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", paiement.getMontantPaiement()))));
                paiementTable.addCell(new Cell().add(new Paragraph(paiement.getMethodePaiement().toString())));
            });

            document.add(paiementTable);
        }

        document.close();
        return baos.toByteArray();
    }
}
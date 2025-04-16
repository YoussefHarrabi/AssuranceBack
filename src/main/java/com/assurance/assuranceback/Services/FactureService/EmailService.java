package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.ResponseMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail = "mohamednegzaoui8@gmail.com";  // Mise à jour de l'email

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public ResponseMessage sendUnpaidInvoiceNotification(Facture facture, String currentDate) {
        if (facture == null) {
            return new ResponseMessage("error", "Facture non trouvée");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(facture.getUser().getEmail());
            helper.setSubject("Rappel de paiement - Facture #" + facture.getFactureId());

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Rappel de paiement</h2>
                    <p>Cher(e) %s,</p>
                    <p>Nous vous rappelons que votre facture est en attente de paiement.</p>
                    
                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="color: #2c3e50; margin-top: 0;">Détails de la facture</h3>
                        <ul style="list-style: none; padding: 0;">
                            <li style="margin-bottom: 10px;">📄 <strong>Numéro de facture:</strong> #%d</li>
                            <li style="margin-bottom: 10px;">💰 <strong>Montant:</strong> %.2f TND</li>
                            <li style="margin-bottom: 10px;">📅 <strong>Date d'émission:</strong> %s</li>
                            <li style="margin-bottom: 10px;">⏰ <strong>Date de rappel:</strong> %s</li>
                        </ul>
                    </div>
                    
                    <p>Merci de procéder au règlement dans les plus brefs délais.</p>
                    <p style="color: #666; font-size: 0.9em;">Si vous avez déjà effectué le paiement, veuillez ne pas tenir compte de ce message.</p>
                    
                    <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;">
                        <p style="margin: 0;">Cordialement,<br>Service de facturation</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                    facture.getUser().getFirstName(),
                    facture.getFactureId(),
                    facture.getMontant(),
                    facture.getDateEmission(),
                    currentDate  // Date actuelle : 2025-04-15 22:52:35
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

            return new ResponseMessage("success", "Email de rappel envoyé avec succès à " + facture.getUser().getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseMessage("error", "Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
}
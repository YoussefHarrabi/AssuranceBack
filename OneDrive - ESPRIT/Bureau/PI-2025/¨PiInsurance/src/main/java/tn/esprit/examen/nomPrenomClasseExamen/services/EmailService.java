package tn.esprit.examen.nomPrenomClasseExamen.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String recipientEmail, String subject, String contentText) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(buildHtmlEmailContent(contentText), true); // HTML content

        try {
            mailSender.send(message);
            System.out.println("✅ Email envoyé à : " + recipientEmail);
        } catch (MailException ex) {
            System.err.println("❌ Échec de l'envoi de l'email : " + ex.getMessage());
            throw ex;
        }
    }

    private String buildHtmlEmailContent(String text) {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; }
                    .email-container { max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }
                    .email-header { background-color: #007BFF; color: white; padding: 10px; text-align: center; border-radius: 8px 8px 0 0; }
                    .email-body { padding: 20px; }
                    .email-footer { background-color: #f1f1f1; text-align: center; padding: 10px; border-radius: 0 0 8px 8px; }
                    h2 { color: #333; }
                    p { font-size: 16px; color: #666; }
                    .button { background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; margin-top: 20px; }
                    .button:hover { background-color: #218838; }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="email-header">
                        <h2>Notification Assurance</h2>
                    </div>
                    <div class="email-body">
                        <p>%s</p>
                        <p>Cordialement,<br>Équipe Assurance Maghrebia</p>
                    </div>
                    <div class="email-footer">
                        <p>Assurance Maghrebia | Tunis, Tunisie</p>
                        <a href="http://localhost:4200" class="button">Visiter notre site</a>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(text);
    }
}

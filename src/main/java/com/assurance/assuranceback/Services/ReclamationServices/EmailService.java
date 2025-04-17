package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Entity.ReclamationEntity.Response;
import com.assurance.assuranceback.Entity.ReclamationEntity.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendComplaintResponseNotification(Response response) throws MessagingException {
        Complaint complaint = response.getComplaint();
        String to = complaint.getClient().getEmail();
        String subject = "Votre réclamation a été traitée - Référence #" + complaint.getId();

        // Add logo as inline attachment
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
        helper.addInline("logo", logoResource);

        String htmlContent = String.format("""
            <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333333;
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: #f5f5f5;
                        }
                        .container {
                            background-color: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                            overflow: hidden;
                            margin: 20px;
                        }
                        .logo {
                            max-height: 60px;
                            margin: 10px auto;
                            display: block;
                        }
                        .header {
                            background: linear-gradient(135deg, #0056b3 0%%, #0077cc 100%%);
                            color: white;
                            padding: 20px;
                            text-align: center;
                            border-radius: 8px 8px 0 0;
                        }
                        .content {
                            padding: 30px;
                        }
                        .info-box {
                            background-color: #f8f9fa;
                            border-left: 4px solid #0056b3;
                            padding: 20px;
                            margin: 20px 0;
                            border-radius: 0 4px 4px 0;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
                        }
                        .response-box {
                            background-color: #f0f7ff;
                            border-left: 4px solid #4CAF50;
                            padding: 20px;
                            margin: 20px 0;
                            border-radius: 0 4px 4px 0;
                            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
                        }
                        .status {
                            display: inline-block;
                            padding: 8px 15px;
                            border-radius: 20px;
                            background-color: #4CAF50;
                            color: white;
                            font-weight: bold;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .metadata {
                            background-color: #f8f9fa;
                            padding: 15px;
                            border-radius: 4px;
                            font-size: 0.9em;
                            color: #666;
                            margin-top: 20px;
                            border: 1px solid #e9ecef;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #666666;
                            border-top: 1px solid #e9ecef;
                        }
                        .system-info {
                            font-size: 11px;
                            color: #999;
                            text-align: right;
                            padding: 5px;
                            margin-top: 10px;
                        }
                        .social-links {
                            margin-top: 15px;
                            padding-top: 15px;
                            border-top: 1px solid #e9ecef;
                        }
                        .social-links a {
                            color: #0056b3;
                            text-decoration: none;
                            margin: 0 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <img src="cid:logo" alt="Logo Assurance" class="logo"/>
                            <h2>Mise à jour de votre réclamation</h2>
                            <div style="font-size: 0.8em; opacity: 0.8;">
                                Référence #%s
                            </div>
                        </div>
                        
                        <div class="content">
                            <p>Cher(e) %s,</p>
                            
                            <p>Votre réclamation concernant <strong>"%s"</strong> a été traitée par notre conseiller.</p>
                            
                            <div class="info-box">
                                <h3>Détails de la réclamation</h3>
                                <p><strong>Type:</strong> %s</p>
                                <p><strong>Date de soumission:</strong> %s</p>
                            </div>
                            
                            <div class="response-box">
                                <h3>Réponse du conseiller</h3>
                                <p>%s</p>
                                <div class="metadata">
                                    <p><strong>Conseiller:</strong> %s %s</p>
                                    <p><strong>Date de réponse:</strong> %s</p>
                                </div>
                            </div>
                            
                            <p>État actuel: <span class="status">%s</span></p>
                        </div>
                        
                        <div class="footer">
                            <p>Ceci est un message automatique, merci de ne pas y répondre directement.</p>
                            <p>Pour toute question supplémentaire, veuillez créer une nouvelle réclamation.</p>
                            <div class="social-links">
                                <a href="#">Facebook</a> |
                                <a href="#">Twitter</a> |
                                <a href="#">LinkedIn</a>
                            </div>
                            <div class="system-info">
                                Généré le %s par %s
                            </div>
                        </div>
                    </div>
                </body>
            </html>
            """,
                complaint.getId(),
                complaint.getClient().getFirstName(),
                complaint.getTitle(),
                complaint.getType(),
                DateTimeUtils.formatDateTime(complaint.getCreationDate().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()),
                response.getContent(),
                response.getAdvisor().getFirstName(),
                response.getAdvisor().getLastName(),
                DateTimeUtils.formatDateTime(response.getDate()),
                complaint.getStatus(),
                DateTimeUtils.getCurrentFormattedDateTime(),
                DateTimeUtils.getCurrentUser()
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }
}
package com.assurance.assuranceback.Services.CarrieresServices;

import com.assurance.assuranceback.Enum.StatutCandidature;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends a notification email when job application status is updated.
     *
     * @param to The recipient email address
     * @param status The new status of the application
     * @param jobTitle The job title the applicant applied for
     * @param userName The name of the applicant
     */
    public void sendStatusUpdateNotification(String to, StatutCandidature status,
                                             String jobTitle, String userName) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@yourdomain.com");
        helper.setTo(to);

        String subject;
        String content;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        switch(status) {
            case EN_ATTENTE:
                subject = "Votre candidature est en cours d'examen";
                content = getHtmlTemplate(
                        "Candidature en cours d'examen",
                        "Cher/Chère " + userName + ",",
                        "Votre candidature pour le poste \"" + jobTitle + "\" est actuellement en cours d'examen.",
                        "Notre équipe de recrutement examine attentivement votre profil et vous contactera prochainement pour vous informer des prochaines étapes.",
                        formattedDateTime,
                        "YoussefHarrabi"
                );
                break;

            case PRESELECTIONNEE:
                subject = "Félicitations ! Votre candidature a été présélectionnée";
                content = getHtmlTemplate(
                        "Candidature Présélectionnée",
                        "Cher/Chère " + userName + ",",
                        "Nous avons le plaisir de vous informer que votre candidature pour le poste \"" + jobTitle + "\" a été présélectionnée.",
                        "Un membre de notre équipe vous contactera prochainement pour organiser un entretien.",
                        formattedDateTime,
                        "YoussefHarrabi"
                );
                break;

            case REFUSEE:
                subject = "Réponse concernant votre candidature";
                content = getHtmlTemplate(
                        "Réponse à votre candidature",
                        "Cher/Chère " + userName + ",",
                        "Nous vous remercions pour l'intérêt que vous portez à notre entreprise et pour le temps que vous avez consacré à votre candidature pour le poste \"" + jobTitle + "\".",
                        "Après un examen attentif, nous regrettons de vous informer que nous avons décidé de ne pas retenir votre candidature pour ce poste. Nous vous souhaitons beaucoup de succès dans vos recherches professionnelles.",
                        formattedDateTime,
                        "YoussefHarrabi"
                );
                break;

            default: // NOUVELLE
                subject = "Candidature bien reçue";
                content = getHtmlTemplate(
                        "Candidature enregistrée",
                        "Cher/Chère " + userName + ",",
                        "Nous confirmons la bonne réception de votre candidature pour le poste \"" + jobTitle + "\".",
                        "Votre candidature a été enregistrée dans notre système et sera examinée par notre équipe de recrutement dans les plus brefs délais.",
                        formattedDateTime,
                        "YoussefHarrabi"
                );
                break;
        }

        helper.setSubject(subject);
        helper.setText(content, true);

        emailSender.send(message);
    }

    private String getHtmlTemplate(String title, String greeting, String message1,
                                   String message2, String dateTime, String updatedBy) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; }\n" +
                "        .container { width: 100%; max-width: 600px; margin: 0 auto; }\n" +
                "        .header { background-color: #4a6da7; color: white; padding: 20px; text-align: center; }\n" +
                "        .content { padding: 20px; background-color: #f9f9f9; }\n" +
                "        .footer { background-color: #eeeeee; padding: 10px; text-align: center; font-size: 12px; }\n" +
                "        .timestamp { font-size: 11px; color: #888; margin-top: 10px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2>" + title + "</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>" + greeting + "</p>\n" +
                "            <p>" + message1 + "</p>\n" +
                "            <p>" + message2 + "</p>\n" +
                "            <p>Cordialement,<br>L'équipe de recrutement</p>\n" +
                "            <p class=\"timestamp\">Date: " + dateTime + "</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>\n" +
                "            <p>Candidature mise à jour par: " + updatedBy + "</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
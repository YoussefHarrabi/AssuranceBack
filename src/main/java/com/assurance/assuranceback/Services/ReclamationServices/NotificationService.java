package com.assurance.assuranceback.Services.ReclamationServices;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate, JavaMailSender mailSender) {
        this.messagingTemplate = messagingTemplate;
        this.mailSender = mailSender;
    }

    public void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    // Vous pouvez ajouter d'autres mécanismes de notification (SMS, notifications push, etc.)

    public void notifyAdmins(String subject, String message) {
        // Dans une implémentation réelle, récupérez les emails des administrateurs depuis la base de données
        String[] adminEmails = {"admin@maghrebia-assurance.tn"};

        for (String email : adminEmails) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        }
    }

    public void notifyUser(Long userId, String subject, String message) {
        // Dans une implémentation réelle, récupérez l'email de l'utilisateur depuis la base de données
        // Exemple simplifié:
        String userEmail = getUserEmailById(userId);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    private String getUserEmailById(Long userId) {
        // Implémentation simplifiée - à remplacer par la récupération réelle depuis la base de données
        return "client" + userId + "@example.com";
    }
}
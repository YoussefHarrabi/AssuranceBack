package com.assurance.assuranceback.Controller.ReclamationController;


import com.assurance.assuranceback.Services.ReclamationServices.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public String sendNotification(String message) {
        notificationService.sendNotification(message);
        return message;
    }
}
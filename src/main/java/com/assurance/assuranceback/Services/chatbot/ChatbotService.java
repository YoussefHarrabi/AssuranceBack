package com.assurance.assuranceback.Services.chatbot;

import com.assurance.assuranceback.Entity.chatbot.ChatResponse;
import com.assurance.assuranceback.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {
    private final Map<String, LocationDTO> locations;
    private final ContactDTO contactInfo;
    private final ScheduleDTO scheduleInfo;

    public ChatbotService() {
        // Initialiser les données statiques
        this.locations = initializeLocations();
        this.contactInfo = initializeContact();
        this.scheduleInfo = initializeSchedule();
    }

    public ChatResponseDTO processMessage(String message, String userId) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Convertir le message en minuscules pour faciliter la recherche
        String lowercaseMessage = message.toLowerCase();

        if (isLocationQuery(lowercaseMessage)) {
            return createLocationResponse();
        } else if (isContactQuery(lowercaseMessage)) {
            return createContactResponse();
        } else if (isScheduleQuery(lowercaseMessage)) {
            return createScheduleResponse();
        } else if (isInsuranceQuery(lowercaseMessage)) {
            return createInsuranceResponse(lowercaseMessage);
        }

        // Réponse par défaut
        return createDefaultResponse();
    }

    private Map<String, LocationDTO> initializeLocations() {
        Map<String, LocationDTO> locs = new HashMap<>();

        LocationDTO mainOffice = new LocationDTO();
        mainOffice.setAddress("123 Avenue des Assurances");
        mainOffice.setCity("Paris");
        mainOffice.setPostalCode("75008");
        mainOffice.setCountry("France");

        CoordinatesDTO coords = new CoordinatesDTO();
        coords.setLat(48.8716);
        coords.setLng(2.3015);
        mainOffice.setCoordinates(coords);

        mainOffice.setDirections("Métro ligne 1, station Charles de Gaulle-Étoile");

        locs.put("main", mainOffice);
        return locs;
    }

    private ContactDTO initializeContact() {
        ContactDTO contact = new ContactDTO();
        contact.setPhone("01 23 45 67 89");
        contact.setEmail("contact@assurance.fr");
        contact.setFax("01 23 45 67 90");

        SocialMediaDTO social = new SocialMediaDTO();
        social.setFacebook("https://facebook.com/assurance");
        social.setTwitter("https://twitter.com/assurance");
        social.setLinkedin("https://linkedin.com/company/assurance");

        contact.setSocialMedia(social);
        return contact;
    }

    private ScheduleDTO initializeSchedule() {
        ScheduleDTO schedule = new ScheduleDTO();

        WorkingHoursDTO weekdays = new WorkingHoursDTO();
        weekdays.setOpen("09:00");
        weekdays.setClose("18:00");
        schedule.setWeekdays(weekdays);

        WorkingHoursDTO weekend = new WorkingHoursDTO();
        weekend.setOpen("10:00");
        weekend.setClose("16:00");
        schedule.setWeekend(weekend);

        schedule.setHolidays(Arrays.asList("01/01", "01/05", "25/12"));
        return schedule;
    }

    private boolean isLocationQuery(String message) {
        List<String> locationKeywords = Arrays.asList("où", "adresse", "localisation", "situé", "trouve", "emplacement");
        return locationKeywords.stream().anyMatch(message::contains);
    }

    private boolean isContactQuery(String message) {
        List<String> contactKeywords = Arrays.asList("contact", "téléphone", "email", "appeler", "joindre", "numéro");
        return contactKeywords.stream().anyMatch(message::contains);
    }

    private boolean isScheduleQuery(String message) {
        List<String> scheduleKeywords = Arrays.asList("horaire", "ouvert", "fermé", "heure", "quand");
        return scheduleKeywords.stream().anyMatch(message::contains);
    }

    private boolean isInsuranceQuery(String message) {
        List<String> insuranceKeywords = Arrays.asList("assurance", "garantie", "couverture", "contrat", "devis");
        return insuranceKeywords.stream().anyMatch(message::contains);
    }

    private ChatResponseDTO createLocationResponse() {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setType(ChatResponseDTO.ResponseType.LOCATION);
        response.setMessage("Voici notre adresse principale :");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ResponseDataDTO data = new ResponseDataDTO();
        data.setLocation(locations.get("main"));
        response.setData(data);

        return response;
    }

    private ChatResponseDTO createContactResponse() {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setType(ChatResponseDTO.ResponseType.CONTACT);
        response.setMessage("Voici nos coordonnées :");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ResponseDataDTO data = new ResponseDataDTO();
        data.setContact(contactInfo);
        response.setData(data);

        return response;
    }

    private ChatResponseDTO createScheduleResponse() {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setType(ChatResponseDTO.ResponseType.SCHEDULE);
        response.setMessage("Voici nos horaires d'ouverture :");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ResponseDataDTO data = new ResponseDataDTO();
        data.setSchedule(scheduleInfo);
        response.setData(data);

        return response;
    }

    private ChatResponseDTO createInsuranceResponse(String message) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setType(ChatResponseDTO.ResponseType.TEXT);
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (message.contains("auto") || message.contains("voiture")) {
            response.setMessage("Notre assurance auto propose plusieurs formules :\n" +
                    "- Formule au tiers\n" +
                    "- Formule intermédiaire\n" +
                    "- Formule tous risques\n" +
                    "Quelle formule vous intéresse ?");
        } else if (message.contains("habitation") || message.contains("maison")) {
            response.setMessage("Notre assurance habitation couvre :\n" +
                    "- Dégâts des eaux\n" +
                    "- Incendie\n" +
                    "- Vol\n" +
                    "- Catastrophes naturelles\n" +
                    "Souhaitez-vous un devis personnalisé ?");
        } else if (message.contains("vie")) {
            response.setMessage("L'assurance vie est un excellent moyen d'épargner et de protéger vos proches. " +
                    "Nous proposons des contrats adaptés à vos besoins avec des rendements attractifs.");
        } else {
            response.setMessage("Nous proposons plusieurs types d'assurances :\n" +
                    "- Assurance auto\n" +
                    "- Assurance habitation\n" +
                    "- Assurance vie\n" +
                    "- Assurance santé\n" +
                    "Quelle assurance vous intéresse ?");
        }

        return response;
    }

    private ChatResponseDTO createDefaultResponse() {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setType(ChatResponseDTO.ResponseType.TEXT);
        response.setMessage("Je suis là pour vous renseigner sur nos assurances, " +
                "nos horaires d'ouverture, notre localisation ou nos coordonnées. " +
                "Comment puis-je vous aider ?");
        response.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return response;
    }
}
package com.assurance.assuranceback.Controller.ChatbotController;

import com.assurance.assuranceback.Config.GeminiConfig;
import com.assurance.assuranceback.Entity.chatbot.ChatFeedback;
import com.assurance.assuranceback.Entity.chatbot.ChatMessage;
import com.assurance.assuranceback.Repository.ChatBot.ChatFeedbackRepository;
import com.assurance.assuranceback.Repository.ChatBot.ChatMessageRepository;
import com.assurance.assuranceback.Services.chatbot.ChatbotIAService;
import com.assurance.assuranceback.dto.ChatFeedbackDTO;
import com.assurance.assuranceback.dto.ChatRequestDTO;
import com.assurance.assuranceback.dto.ChatResponseDTO;
import com.assurance.assuranceback.dto.ResponseDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // Correct import for Pageable
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/gemini")

@CrossOrigin(origins = "http://localhost:4200")  // Ajout explicite ici

@Slf4j
public class ChatbotIAController {

    @Autowired
    private ChatbotIAService chatbotIAService;

    @Autowired
    private GeminiConfig geminiConfig;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatFeedbackRepository chatFeedbackRepository;

    /**
     * Endpoint principal pour envoyer un message au chatbot et recevoir une réponse
     */
    @PostMapping("/send")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        try {
            // Initialiser le timestamp s'il est null
            if (request.getTimestamp() == null) {
                request.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            // Initialiser l'ID utilisateur s'il est null
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                request.setUserId(geminiConfig.getCurrentUser());
            }

            log.info("Received message: '{}' from user: {}", request.getMessage(), request.getUserId());

            ChatResponseDTO response = chatbotIAService.processMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in controller: {}", e.getMessage(), e);

            ChatResponseDTO errorResponse = new ChatResponseDTO();
            errorResponse.setType(ChatResponseDTO.ResponseType.ERROR);
            errorResponse.setMessage("Erreur serveur : " + e.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            errorResponse.setContent(e.getMessage());
            errorResponse.setMetadata("Server Error");

            ResponseDataDTO errorData = new ResponseDataDTO();
            errorData.setContent(e.getMessage());
            errorData.setMetadata("Server Error");
            errorResponse.setData(errorData);

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Endpoint pour vérifier l'état de santé du service chatbot
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Chatbot service is running");
        status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        status.put("version", "1.0.0");

        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint pour récupérer l'historique des conversations d'un utilisateur
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getChatHistory(@PathVariable String userId,
                                                                    @RequestParam(defaultValue = "20") int limit) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByTimestampDesc(
                    userId, PageRequest.of(0, limit));

            List<Map<String, Object>> formattedHistory = messages.stream()
                    .map(msg -> {
                        Map<String, Object> formattedMsg = new HashMap<>();
                        formattedMsg.put("id", msg.getId());
                        formattedMsg.put("content", msg.getContent());
                        formattedMsg.put("timestamp", msg.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        formattedMsg.put("type", msg.getType().toString());
                        return formattedMsg;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(formattedHistory);
        } catch (Exception e) {
            log.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint pour soumettre un feedback sur une réponse du chatbot
     */
    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> provideFeedback(@RequestBody ChatFeedbackDTO feedbackDTO) {
        try {
            Optional<ChatMessage> messageOpt = chatMessageRepository.findById(feedbackDTO.getMessageId());

            if (messageOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message not found"));
            }

            ChatMessage message = messageOpt.get();

            ChatFeedback feedback = new ChatFeedback();
            feedback.setMessage(message);
            feedback.setHelpful(feedbackDTO.isHelpful());
            feedback.setComment(feedbackDTO.getComment());
            feedback.setUserId(feedbackDTO.getUserId());
            feedback.setTimestamp(LocalDateTime.now());

            chatFeedbackRepository.save(feedback);

            return ResponseEntity.ok(Map.of("message", "Merci pour votre retour !"));
        } catch (Exception e) {
            log.error("Error saving feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Une erreur est survenue lors de l'enregistrement de votre retour"));
        }
    }

    /**
     * Endpoint pour effacer l'historique des conversations d'un utilisateur
     */
    @DeleteMapping("/history/{userId}")
    public ResponseEntity<Map<String, String>> clearChatHistory(@PathVariable String userId) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByTimestampDesc(
                    userId, PageRequest.of(0, 1000)); // Limite de sécurité

            chatMessageRepository.deleteAll(messages);

            return ResponseEntity.ok(Map.of("message", "Historique de conversation effacé avec succès"));
        } catch (Exception e) {
            log.error("Error clearing chat history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Une erreur est survenue lors de l'effacement de l'historique"));
        }
    }

    /**
     * Endpoint pour obtenir des informations sur le service chatbot
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getChatbotInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "AssuranceX ChatBot");
        info.put("version", "1.0.0");
        info.put("type", "Assurance Specialist AI");
        info.put("currentDateTime", geminiConfig.getCurrentDateTime());
        info.put("supportedTopics", List.of(
                "Assurance auto",
                "Assurance habitation",
                "Assurance santé",
                "Assurance vie",
                "Informations sur les agences",
                "Horaires d'ouverture",
                "Procédures de réclamation"
        ));

        return ResponseEntity.ok(info);
    }

    /**
     * Endpoint pour les FAQs prédéfinies
     */
    @GetMapping("/faq")
    public ResponseEntity<List<Map<String, Object>>> getFAQs() {
        List<Map<String, Object>> faqs = List.of(
                Map.of(
                        "question", "Quels types d'assurance proposez-vous ?",
                        "answer", "Maghrebia Assurances propose une gamme complète de produits :\n" +
                                "• Assurance Auto : Tiers et Tous Risques avec options\n" +
                                "• Assurance Multirisque Habitation\n" +
                                "• Maghrebia Santé : couverture médicale complète\n" +
                                "• Maghrebia Vie : épargne, retraite et prévoyance\n" +
                                "• Assurances Professionnelles et Entreprises"
                ),
                Map.of(
                        "question", "Quels sont vos horaires d'ouverture ?",
                        "answer", "Nos horaires d'ouverture sont :\n" +
                                "• Du Lundi au Vendredi : 8h00 à 17h00\n" +
                                "• Samedi : 8h00 à 13h00\n" +
                                "• Fermé le Dimanche\n" +
                                "• Horaires Ramadan : 8h00 à 15h00 (Lundi au Vendredi)"
                ),
                Map.of(
                        "question", "Comment déclarer un sinistre ?",
                        "answer", "Plusieurs options pour déclarer un sinistre :\n" +
                                "• Par téléphone : 31 330 330\n" +
                                "• Notre assistance 24/7 : 31 399 399\n" +
                                "• Par email : sinistres@maghrebia.com.tn\n" +
                                "• Dans l'une de nos agences\n" +
                                "Important : Déclarez votre sinistre dans les 5 jours ouvrés (2 jours en cas de vol)"
                ),
                Map.of(
                        "question", "Puis-je obtenir un devis en ligne ?",
                        "answer", "Oui, vous pouvez obtenir un devis de plusieurs façons :\n" +
                                "• Sur notre site web : www.maghrebia.com.tn\n" +
                                "• En appelant notre centre de relation client : 31 330 330\n" +
                                "• Via notre application mobile Maghrebia\n" +
                                "• Dans l'une de nos agences"
                ),
                Map.of(
                        "question", "Où se trouve votre siège social et vos agences principales ?",
                        "answer", "• Siège social : Angle 64, rue de Palestine et 22, rue du Royaume d'Arabie Saoudite, 1002 Tunis\n" +
                                "• Agence Sfax : Avenue 14 Janvier, Immeuble Mahfoudh, Route Gremda Km 0.5\n" +
                                "• Agence Sousse : Avenue Habib Bourguiba, Immeuble Maghrebia\n" +
                                "Pour trouver l'agence la plus proche, consultez notre site web ou appelez le 31 330 330"
                ),
                Map.of(
                        "question", "Comment puis-je contacter votre service client ?",
                        "answer", "Plusieurs canaux de contact sont disponibles :\n" +
                                "• Centre de Relations Clients : 31 330 330\n" +
                                "• Email : contact@maghrebia.com.tn\n" +
                                "• Assistance urgence 24/7 : 31 399 399\n" +
                                "• Facebook : Maghrebia Assurances\n" +
                                "• LinkedIn : Compagnie Maghrebia d'Assurances"
                ),
                Map.of(
                        "question", "Quels sont les documents nécessaires pour souscrire une assurance ?",
                        "answer", "Les documents requis varient selon le type d'assurance :\n" +
                                "• Auto : Carte grise, permis de conduire, CIN\n" +
                                "• Habitation : Contrat de location ou titre de propriété, CIN\n" +
                                "• Santé : CIN, composition familiale, justificatifs de revenus\n" +
                                "• Vie : CIN, justificatifs de revenus, questionnaire médical"
                ),
                Map.of(
                        "question", "Comment puis-je payer ma prime d'assurance ?",
                        "answer", "Plusieurs modes de paiement sont disponibles :\n" +
                                "• En agence : espèces, chèque ou carte bancaire\n" +
                                "• Par virement bancaire\n" +
                                "• Via notre application mobile\n" +
                                "• Par prélèvement automatique (mensualisation possible)"
                )
        );

        return ResponseEntity.ok(faqs);
    }
    /**
     * Endpoint pour suggérer des questions en fonction du contexte
     */
    @GetMapping("/suggestions/{context}")
    public ResponseEntity<List<String>> getSuggestions(@PathVariable String context) {
        Map<String, List<String>> contextualSuggestions = Map.of(
                "auto", List.of(
                        "Quelles garanties offre votre assurance auto ?",
                        "Comment fonctionne le bonus-malus ?",
                        "Quelle est la différence entre vos formules Essential, Comfort et Premium ?"
                ),
                "habitation", List.of(
                        "Comment estimer la valeur de mes biens ?",
                        "Les catastrophes naturelles sont-elles couvertes ?",
                        "Proposez-vous une garantie vol pour mon logement ?"
                ),
                "santé", List.of(
                        "Quels sont vos délais de remboursement ?",
                        "Comment ajouter un bénéficiaire à mon contrat ?",
                        "Les médecines douces sont-elles prises en charge ?"
                ),
                "agence", List.of(
                        "Où se trouve l'agence la plus proche de chez moi ?",
                        "Faut-il prendre rendez-vous pour rencontrer un conseiller ?",
                        "Quels sont vos horaires d'ouverture ?"
                )
        );

        List<String> suggestions = contextualSuggestions.getOrDefault(
                context.toLowerCase(),
                List.of(
                        "Quels types d'assurance proposez-vous ?",
                        "Comment vous contacter en cas d'urgence ?",
                        "Comment obtenir un devis personnalisé ?"
                )
        );

        return ResponseEntity.ok(suggestions);
    }
}
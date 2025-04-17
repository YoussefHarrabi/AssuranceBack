package com.assurance.assuranceback.Services.chatbot;

import com.assurance.assuranceback.Config.GeminiConfig;
import com.assurance.assuranceback.Entity.chatbot.ChatMessage;
import com.assurance.assuranceback.Entity.chatbot.InsuranceKnowledgeBase;
import com.assurance.assuranceback.Repository.ChatBot.ChatMessageRepository;
import com.assurance.assuranceback.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ChatbotIAService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebClient webClient;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private InsuranceKnowledgeBase knowledgeBase;

    @Autowired
    private GeminiConfig geminiConfig;

    // Liste de mots-clés liés à l'assurance pour filtrage
    private final List<String> insuranceKeywords = Arrays.asList(
            // Termes généraux d'assurance
            "assurance", "garantie", "couverture", "prime", "sinistre", "contrat", "police",
            "remboursement", "devis", "franchise", "indemnité", "clause", "risque", "cotisation",

            // Types d'assurance spécifiques à Maghrebia
            "auto", "automobile", "voiture", "véhicule", "habitation", "maison", "appartement",
            "santé", "vie", "décès", "invalidité", "incapacité", "responsabilité", "civile",
            "protection", "juridique", "multirisque",

            // Services Maghrebia
            "agence", "tunis", "sfax", "sousse", "localisation", "adresse", "horaire",
            "contact", "téléphone", "email", "fax", "assistance", "urgence",

            // Termes spécifiques Maghrebia
            "maghrebia", "31330330", "31399399"
    );

    private boolean isInsuranceRelated(String message, String userId) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        // 1. Vérification directe des mots-clés
        String lowerMessage = message.toLowerCase();
        if (insuranceKeywords.stream().anyMatch(keyword -> lowerMessage.contains(keyword.toLowerCase()))) {
            return true;
        }

        // 2. Vérification du contexte de la conversation
        List<ChatMessage> recentMessages = chatMessageRepository.findByUserIdOrderByTimestampDesc(
                userId, PageRequest.of(0, 3));

        // Si une conversation sur l'assurance est déjà en cours
        for (ChatMessage recentMessage : recentMessages) {
            String content = recentMessage.getContent().toLowerCase();
            if (insuranceKeywords.stream().anyMatch(keyword -> content.contains(keyword.toLowerCase()))) {
                return true;
            }
        }

        // 3. Messages courts ou questions
        if (message.length() < 50 && (message.endsWith("?") || !recentMessages.isEmpty())) {
            return true;
        }

        return false;
    }

    private String getSystemPrompt() {
        return "Vous êtes l'assistant virtuel officiel de Maghrebia Assurances, spécialisé uniquement dans le domaine de l'assurance.\n\n" +
                knowledgeBase.getFullKnowledgeBase() + "\n\n" +
                "Règles importantes à suivre :\n" +
                "1. Répondez uniquement aux questions liées à l'assurance, aux produits Maghrebia, aux horaires et localisations des agences, et aux contacts.\n" +
                "2. Pour les questions de localisation, mentionnez toujours les adresses complètes de nos agences :\n" +
                "   - Siège Tunis : Angle 64, rue de Palestine et 22, rue du Royaume d'Arabie Saoudite, 1002 Tunis\n" +
                "   - Agence Sfax : Avenue 14 Janvier, Immeuble Mahfoudh, Route Gremda Km 0.5, 3027 Sfax\n" +
                "   - Agence Sousse : Avenue Habib Bourguiba, Immeuble Maghrebia, 4000 Sousse\n" +
                "3. Utilisez toujours les bons numéros de contact :\n" +
                "   - Service client : 31 330 330\n" +
                "   - Urgence : 31 399 399\n" +
                "4. N'inventez pas d'informations qui ne sont pas dans la base de connaissances.\n" +
                "5. Pour les questions concernant les tarifs précis, invitez toujours l'utilisateur à contacter un conseiller.\n" +
                "6. Date actuelle pour référence: " + geminiConfig.getCurrentDateTime() + "\n" +
                "7. Utilisateur actuel: " + geminiConfig.getCurrentUser();
    }

    public ChatResponseDTO processMessage(ChatRequestDTO request) {
        try {
            boolean isInsuranceRelated = isInsuranceRelated(request.getMessage(), request.getUserId());

            if (!isInsuranceRelated && request.getMessage().length() > 15) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                ChatResponseDTO response = new ChatResponseDTO();
                response.setMessage("Je suis désolé, mais je suis spécialisé uniquement dans les services de Maghrebia Assurances. " +
                        "Je peux vous renseigner sur nos produits d'assurance, nos agences, nos horaires d'ouverture ou nos coordonnées.");
                response.setTimestamp(timestamp);
                response.setType(ChatResponseDTO.ResponseType.TEXT);
                response.setSuggestedQuestions(Arrays.asList(
                        "Quels sont vos produits d'assurance ?",
                        "Où se trouvent vos agences ?",
                        "Comment vous contacter ?"
                ));

                saveInteraction(request.getMessage(), response.getMessage(), request.getUserId());
                return response;
            }

            saveInteraction(request.getMessage(), null, request.getUserId());

            String conversationHistory = getConversationHistory(request.getUserId(), 5);

            String enhancedPrompt = "En tant qu'assistant d'assurance de Maghrebia Assurances, réponds à cette question en tenant compte de l'historique de conversation si pertinent. " +
                    "Sois précis et concis dans tes réponses. Utilise toujours les informations de la base de connaissances.\n\n" +
                    "Question actuelle: " + request.getMessage() + "\n\n" +
                    conversationHistory + "\n\n" +
                    "Base de connaissances :\n" +
                    knowledgeBase.getFullKnowledgeBase() + "\n\n" +
                    "Règles importantes :\n" +
                    "1. Utilise toujours les informations exactes de localisation des agences\n" +
                    "2. Utilise les vrais numéros de téléphone : 31 330 330 pour le service client et 31 399 399 pour les urgences\n" +
                    "3. Ne jamais inventer d'informations qui ne sont pas dans la base de connaissances\n" +
                    "4. Pour les questions de localisation, toujours mentionner les adresses complètes des agences\n" +
                    "5. Heure actuelle : " + geminiConfig.getCurrentDateTime() + "\n" +
                    "6. Utilisateur : " + geminiConfig.getCurrentUser();

            String requestBody = "{" +
                    "\"contents\": [" +
                    "{" +
                    "\"parts\": [" +
                    "{" +
                    "\"text\": \"" +
                    enhancedPrompt.replace("\"", "\\\"").replace("\n", "\\n") +
                    "\"" +
                    "}" +
                    "]" +
                    "}" +
                    "]" +
                    "}";

            log.info("Sending request to Gemini API: {}", requestBody);

            String response = webClient
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Received response from Gemini API: {}", response);

            JsonNode jsonResponse = objectMapper.readTree(response);
            String generatedText = jsonResponse
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            // Remplacer les numéros de téléphone incorrects
            generatedText = generatedText.replace("01.23.45.67.89", "31 330 330")
                    .replace("0800.12.34.56", "31 399 399")
                    .replace("AssuranceX", "Maghrebia Assurances");

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            saveInteraction(null, generatedText, request.getUserId());

            ChatResponseDTO responseDTO = new ChatResponseDTO();
            responseDTO.setMessage(generatedText);
            responseDTO.setTimestamp(timestamp);
            responseDTO.setType(ChatResponseDTO.ResponseType.TEXT);
            responseDTO.setSuggestedQuestions(generateSuggestedQuestions(request.getMessage(), generatedText));

            return responseDTO;

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ChatResponseDTO errorResponse = new ChatResponseDTO();
            errorResponse.setMessage("Désolé, une erreur est survenue. Veuillez nous contacter au 31 330 330 ou par email à contact@maghrebia.com.tn");
            errorResponse.setTimestamp(timestamp);
            errorResponse.setType(ChatResponseDTO.ResponseType.ERROR);
            errorResponse.setSuggestedQuestions(Arrays.asList(
                    "Comment vous contacter ?",
                    "Où se trouvent vos agences ?",
                    "Quels sont vos horaires d'ouverture ?"
            ));

            return errorResponse;
        }
    }

    private void saveInteraction(String userMessage, String botResponse, String userId) {
        try {
            if (userMessage != null) {
                ChatMessage msg = new ChatMessage();
                msg.setContent(userMessage);
                msg.setType(ChatMessage.MessageType.USER);
                msg.setTimestamp(LocalDateTime.now());
                msg.setSender(userId);
                msg.setUserId(userId);
                chatMessageRepository.save(msg);
            }

            if (botResponse != null) {
                ChatMessage msg = new ChatMessage();
                msg.setContent(botResponse);
                msg.setType(ChatMessage.MessageType.BOT);
                msg.setTimestamp(LocalDateTime.now());
                msg.setSender("BOT");
                msg.setUserId(userId);
                chatMessageRepository.save(msg);
            }
        } catch (Exception e) {
            log.error("Error saving chat interaction: {}", e.getMessage(), e);
        }
    }

    private String getConversationHistory(String userId, int maxMessages) {
        List<ChatMessage> conversationHistory = chatMessageRepository.findByUserIdOrderByTimestampDesc(
                userId, PageRequest.of(0, maxMessages));

        Collections.reverse(conversationHistory);

        StringBuilder historyBuilder = new StringBuilder("Historique de conversation récente:\n");
        for (ChatMessage msg : conversationHistory) {
            if (msg.getType() == ChatMessage.MessageType.USER) {
                historyBuilder.append("Utilisateur: ").append(msg.getContent()).append("\n");
            } else if (msg.getType() == ChatMessage.MessageType.BOT) {
                historyBuilder.append("Assistant: ").append(msg.getContent()).append("\n");
            }
        }

        return historyBuilder.toString();
    }

    private List<String> generateSuggestedQuestions(String userMessage, String botResponse) {
        List<String> suggestions = new ArrayList<>();
        String lowerUserMessage = userMessage.toLowerCase();
        String lowerBotResponse = botResponse.toLowerCase();

        // Suggestions pour les produits d'assurance
        if (lowerUserMessage.contains("assurance") || lowerBotResponse.contains("assurance") ||
                lowerUserMessage.contains("produit") || lowerBotResponse.contains("produit")) {
            suggestions.add("Quels sont les types d'assurance proposés ?");
            suggestions.add("Quelles sont les garanties incluses ?");
            suggestions.add("Comment souscrire à une assurance ?");
        }

        // Suggestions pour l'assurance auto
        if (lowerUserMessage.contains("auto") || lowerBotResponse.contains("auto") ||
                lowerUserMessage.contains("voiture") || lowerBotResponse.contains("voiture")) {
            suggestions.add("Quelles sont les garanties de l'assurance auto ?");
            suggestions.add("Comment déclarer un sinistre auto ?");
            suggestions.add("Quel est le coût de l'assurance auto ?");
        }

        // Suggestions pour la localisation
        if (lowerUserMessage.contains("agence") || lowerBotResponse.contains("agence") ||
                lowerUserMessage.contains("localisation") || lowerBotResponse.contains("localisation")) {
            suggestions.add("Quels sont vos horaires d'ouverture ?");
            suggestions.add("Comment prendre rendez-vous ?");
            suggestions.add("Où se trouve l'agence la plus proche ?");
        }

        // Suggestions pour les contacts
        if (lowerUserMessage.contains("contact") || lowerBotResponse.contains("contact") ||
                lowerUserMessage.contains("téléphone") || lowerBotResponse.contains("téléphone")) {
            suggestions.add("Quel est le numéro d'urgence ?");
            suggestions.add("Comment contacter le service client ?");
            suggestions.add("Quelle est l'adresse email de contact ?");
        }

        // Questions par défaut
        if (suggestions.isEmpty()) {
            suggestions.add("Quels sont vos produits d'assurance ?");
            suggestions.add("Où se trouvent vos agences ?");
            suggestions.add("Comment vous contacter ?");
        }

        // Limiter à 3 suggestions
        return suggestions.size() > 3 ? suggestions.subList(0, 3) : suggestions;
    }

    public boolean testGeminiApi() {
        try {
            log.info("Testing Gemini API...");
            ObjectNode requestJson = objectMapper.createObjectNode();
            ArrayNode contentsArray = requestJson.putArray("contents");

            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("role", "user");
            ArrayNode userPartsArray = userNode.putArray("parts");
            ObjectNode userPartNode = objectMapper.createObjectNode();
            userPartNode.put("text", "Bonjour");
            userPartsArray.add(userPartNode);
            contentsArray.add(userNode);

            String requestBody = objectMapper.writeValueAsString(requestJson);

            String response = webClient
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && response.contains("candidates");
        } catch (Exception e) {
            log.error("Error testing Gemini API: {}", e.getMessage());
            return false;
        }
    }
}
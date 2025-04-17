package com.assurance.assuranceback.Controller.ChatbotController;


import com.assurance.assuranceback.Entity.chatbot.ChatRequest;
import com.assurance.assuranceback.Entity.chatbot.ChatResponse;
import com.assurance.assuranceback.Services.chatbot.ChatbotService;
import com.assurance.assuranceback.dto.ChatRequestDTO;
import com.assurance.assuranceback.dto.ChatResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponseDTO> ask(@RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatbotService.processMessage(request.getMessage(), request.getUserId());
        return ResponseEntity.ok(response);
    }
}
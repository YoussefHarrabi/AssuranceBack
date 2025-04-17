package com.assurance.assuranceback.Repository.ChatBot;

import com.assurance.assuranceback.Entity.chatbot.ChatMessage;
import org.springframework.data.domain.Pageable; // Modifié ici
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
        List<ChatMessage> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
}
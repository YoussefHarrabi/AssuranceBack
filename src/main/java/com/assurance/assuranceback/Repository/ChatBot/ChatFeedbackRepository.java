package com.assurance.assuranceback.Repository.ChatBot;

import com.assurance.assuranceback.Entity.chatbot.ChatFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFeedbackRepository extends JpaRepository<ChatFeedback, Long> {
}

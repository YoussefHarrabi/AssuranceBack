package com.assurance.assuranceback.Entity.chatbot;

import com.assurance.assuranceback.Entity.UserEntity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private MessageType type;

    // Renommer la colonne pour éviter le conflit avec la relation user
    @Column(name = "user_identifier")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum MessageType {
        USER,
        BOT
    }
}
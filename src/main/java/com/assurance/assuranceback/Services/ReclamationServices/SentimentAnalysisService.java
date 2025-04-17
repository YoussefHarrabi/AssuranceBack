package com.assurance.assuranceback.Services.ReclamationServices;

import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {
    /**
     * Analyse le sentiment d'un texte et retourne le résultat
     * Vous pourriez utiliser Gemini API ou un autre service d'IA
     */
    public String analyzeSentiment(String text) {
        // Implémentation simplifiée - à remplacer par une vraie API de sentiment
        if (text == null || text.isEmpty()) {
            return "NEUTRAL";
        }

        text = text.toLowerCase();
        int positiveScore = countOccurrences(text, new String[]{"merci", "excellent", "bien", "satisfait", "rapide", "professionnel"});
        int negativeScore = countOccurrences(text, new String[]{"problème", "mauvais", "lent", "déçu", "insatisfait", "difficile"});

        if (positiveScore > negativeScore) {
            return "POSITIVE";
        } else if (negativeScore > positiveScore) {
            return "NEGATIVE";
        } else {
            return "NEUTRAL";
        }
    }

    private int countOccurrences(String text, String[] words) {
        int count = 0;
        for (String word : words) {
            // Compter les occurrences de chaque mot
            int index = 0;
            while ((index = text.indexOf(word, index)) != -1) {
                count++;
                index += word.length();
            }
        }
        return count;
    }
}
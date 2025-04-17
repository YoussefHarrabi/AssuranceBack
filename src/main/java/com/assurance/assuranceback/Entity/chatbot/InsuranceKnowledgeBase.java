package com.assurance.assuranceback.Entity.chatbot;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class InsuranceKnowledgeBase {
    // Structure pour stocker les informations d'assurance
    private final Map<String, String> productInfo = new HashMap<>();
    private final Map<String, String> locationInfo = new HashMap<>();
    private final Map<String, String> scheduleInfo = new HashMap<>();
    private final Map<String, String> contactInfo = new HashMap<>();

    @PostConstruct
    public void initialize() {
        // Produits d'assurance
        productInfo.put("auto", "L'assurance automobile Maghrebia propose plusieurs formules : \n"
                + "• Assurance au Tiers (responsabilité civile obligatoire)\n"
                + "• Assurance Tous Risques (dommages matériels, vol, incendie)\n"
                + "• Options complémentaires : assistance 24/7, bris de glace, protection juridique\n"
                + "Nous assurons tous types de véhicules : particuliers, utilitaires et poids lourds.");

        productInfo.put("habitation", "Notre assurance Multirisque Habitation protège votre logement et vos biens avec : \n"
                + "• Garantie incendie et risques annexes\n"
                + "• Protection contre le vol et le vandalisme\n"
                + "• Dégâts des eaux\n"
                + "• Responsabilité civile chef de famille\n"
                + "• Catastrophes naturelles");

        productInfo.put("santé", "Maghrebia Santé offre une couverture complète : \n"
                + "• Hospitalisation en Tunisie et à l'étranger\n"
                + "• Consultations et visites médicales\n"
                + "• Pharmacie et analyses\n"
                + "• Soins dentaires et optiques\n"
                + "• Services d'assistance et de tiers payant");

        productInfo.put("vie", "Maghrebia Vie propose des solutions d'épargne et de prévoyance : \n"
                + "• Assurance Temporaire Décès\n"
                + "• Assurance Mixte (épargne + protection)\n"
                + "• Retraite complémentaire\n"
                + "• Capitalisation\n"
                + "• Éducation");

        // Agences et localisation
        locationInfo.put("tunis", "Siège social: Angle 64, rue de Palestine et 22, rue du Royaume d'Arabie Saoudite, 1002 Tunis\n"
                + "• Parking disponible\n"
                + "• Accès facile depuis le centre-ville");

        locationInfo.put("sfax", "Agence Sfax: Avenue 14 Janvier, Immeuble Mahfoudh, Route Gremda Km 0.5, 3027 Sfax\n"
                + "• À proximité de la route principale\n"
                + "• Grand parking disponible");

        locationInfo.put("sousse", "Agence Sousse: Avenue Habib Bourguiba, Immeuble Maghrebia, 4000 Sousse\n"
                + "• En plein centre-ville\n"
                + "• Facilement accessible en transport public");

        // Horaires d'ouverture
        scheduleInfo.put("semaine", "Du Lundi au Vendredi de 8h00 à 17h00");
        scheduleInfo.put("weekend", "Samedi de 8h00 à 13h00. Fermé le Dimanche");
        scheduleInfo.put("ramadan", "Pendant le Ramadan : du Lundi au Vendredi de 8h00 à 15h00");

        // Contact
        contactInfo.put("telephone", "Centre de Relations Clients : 31 330 330");
        contactInfo.put("urgence", "Assistance 24/7 en cas d'urgence : 31 399 399");
        contactInfo.put("email", "contact@maghrebia.com.tn");
        contactInfo.put("reclamation", "reclamation@maghrebia.com.tn");
        contactInfo.put("devis", "Pour obtenir un devis, visitez www.maghrebia.com.tn ou appelez le 31 330 330");
        contactInfo.put("fax", "Fax : (+216) 71 784 307");
        contactInfo.put("social", "Suivez-nous sur :\n"
                + "• Facebook : Maghrebia Assurances\n"
                + "• LinkedIn : Compagnie Maghrebia d'Assurances\n"
                + "• Site web : www.maghrebia.com.tn");
    }

    public String getFullKnowledgeBase() {
        StringBuilder kb = new StringBuilder("Base de connaissances sur Maghrebia Assurances :\n\n");

        kb.append("PRODUITS D'ASSURANCE :\n");
        productInfo.forEach((k, v) -> kb.append("- ").append(k).append(" : ").append(v).append("\n"));

        kb.append("\nAGENCES ET LOCALISATION :\n");
        locationInfo.forEach((k, v) -> kb.append("- ").append(k).append(" : ").append(v).append("\n"));

        kb.append("\nHORAIRES D'OUVERTURE :\n");
        scheduleInfo.forEach((k, v) -> kb.append("- ").append(k).append(" : ").append(v).append("\n"));

        kb.append("\nCONTACT :\n");
        contactInfo.forEach((k, v) -> kb.append("- ").append(k).append(" : ").append(v).append("\n"));

        return kb.toString();
    }

}

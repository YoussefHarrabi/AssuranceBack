package com.example.gharbipi.services;

import com.example.gharbipi.entities.InsurancePro;
import com.example.gharbipi.entities.InsuranceProType;
import com.example.gharbipi.entities.InsuranceStatus;
import com.example.gharbipi.repos.InsuranceProRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceProService {

    @Autowired
    private InsuranceProRepository insuranceProRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    private static final double BASE_PREMIUM = 1000.0;

    // Send HTML Email Method
    public void sendEmail(String to, String subject, String text) {
        try {
            // Create MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set email parameters
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildHtmlEmailContent(text), true);  // Set the content to be HTML

            // Send the email
            javaMailSender.send(message);
            System.out.println("✅ Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Build HTML content for the email
    private String buildHtmlEmailContent(String text) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body {font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4;}" +
                ".email-container {max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);}" +
                ".email-header {background-color: #007BFF; color: white; padding: 10px; text-align: center; border-radius: 8px 8px 0 0;}" +
                ".email-body {padding: 20px;}" +
                ".email-footer {background-color: #f1f1f1; text-align: center; padding: 10px; border-radius: 0 0 8px 8px;}" +
                "h2 {color: #333;}" +
                "p {font-size: 16px; color: #666;}" +
                ".button {background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;}" +
                ".button:hover {background-color: #218838;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='email-header'>" +
                "<h2>Important Update on InsurancePro</h2>" +
                "</div>" +
                "<div class='email-body'>" +
                "<p>" + text + "</p>" +
                "<p>Best Regards,<br/>Your Company Team</p>" +
                "</div>" +
                "<div class='email-footer'>" +
                "<p>Company Name | Address | Contact Info</p>" +
                "<p><a href='#' class='button'>Visit Our Website</a></p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    // Calculate premium amount based on risk level
    public double calculatePremiumAmount(double risk) {
        if (risk < 1 || risk > 10) {
            throw new IllegalArgumentException("Risk must be between 1 and 10.");
        }

        double riskMultiplier = 1.0 + (risk - 1) * 0.2;
        double premium = BASE_PREMIUM * riskMultiplier;

        if (riskMultiplier > 2.0) {
            premium += 500.0;
        }

        if (riskMultiplier >= 1.5 && riskMultiplier <= 2.0) {
            premium *= 0.9;
        }

        premium += risk * 10;
        return Math.round(premium * 100.0) / 100.0;
    }

    public List<InsurancePro> getAllInsurancePros() {
        return insuranceProRepository.findAll();
    }

    public Optional<InsurancePro> getInsuranceProById(Long id) {
        return insuranceProRepository.findById(id);
    }

    // Create InsurancePro and send email with details (with HTML email)
    public InsurancePro createInsurancePro(InsurancePro insurancePro) {
        InitializeAmountInsurancePro(insurancePro);

        if (insurancePro.getStatus() == null) {
            insurancePro.setStatus(InsuranceStatus.EN_ATTENTE);
        }

        // Save InsurancePro
        InsurancePro saved = insuranceProRepository.save(insurancePro);

        // Prepare email content for creation
        String emailContent = "<strong>A new InsurancePro has been created:</strong><br/>" +
                "<b>Insurance Type:</b> " + saved.getInsuranceProType().getName() + "<br/>" +
                "<b>Premium Amount:</b> " + saved.getPremiumAmount() + "<br/>" +
                "<b>Status:</b> " + saved.getStatus().toString();

        // Send email to kaledgadh0@gmail.com (updated email)
        sendEmail("kaledgadh0@gmail.com", "New InsurancePro Created", emailContent);

        return saved;
    }

    // Update InsurancePro and send email with details (with HTML email)
    public InsurancePro updateInsurancePro(InsurancePro insurancePro) {
        InitializeAmountInsurancePro(insurancePro);

        // Save the updated InsurancePro
        InsurancePro updated = insuranceProRepository.save(insurancePro);

        // Prepare email content for update
        String emailContent = "<strong>An existing InsurancePro has been updated:</strong><br/>" +
                "<b>Insurance Type:</b> " + updated.getInsuranceProType().getName() + "<br/>" +
                "<b>Premium Amount:</b> " + updated.getPremiumAmount() + "<br/>" +
                "<b>Status:</b> " + updated.getStatus().toString();

        // Send email to kaledgadh0@gmail.com (updated email)
        sendEmail("kaledgadh0@gmail.com", "InsurancePro Updated", emailContent);

        return updated;
    }
    public void deleteInsurancePro(Long id) {
        insuranceProRepository.deleteById(id);
    }

    // Check if more than 40% of policies are of the same type
    private boolean exceedsTypeLimit(InsurancePro insurancePro) {
        long total = insuranceProRepository.count();
        long sameTypeCount = insuranceProRepository.countByInsuranceProType(insurancePro.getInsuranceProType());

        return sameTypeCount > total * 0.4;
    }

    // Sets the premium amount considering type proportion and risk
    public InsurancePro InitializeAmountInsurancePro(InsurancePro insurancePro) {
        double risk = Double.parseDouble(insurancePro.getInsuranceProType().getRisk());
        double premium = calculatePremiumAmount(risk);

        if (exceedsTypeLimit(insurancePro)) {
            premium *= 1.5;
        }

        insurancePro.setPremiumAmount(premium);
        return insurancePro;
    }
}

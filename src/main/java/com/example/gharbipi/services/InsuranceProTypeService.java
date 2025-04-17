package com.example.gharbipi.services;

import com.example.gharbipi.entities.InsuranceProType;
import com.example.gharbipi.repos.InsuranceProTypeRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InsuranceProTypeService {
    @Autowired
    private InsuranceProTypeRepository insuranceProTypeRepository;

    private final RestTemplate restTemplate;


    public InsuranceProTypeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Value("${huggingface.api.key}")
    private String apiKey;



    public String getRisk(String field) {
        // Step 1: Set up the API URL for the RoBERTa model
        String apiUrl = "https://api-inference.huggingface.co/models/deepset/roberta-base-squad2";

        // Step 2: Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); // Use your Hugging Face API key
        headers.set("Content-Type", "application/json");

        // Step 3: Provide a context for the model
        String context = "Insurance risk for factories is 8. Insurance risk for machines is 5. Insurance risk for vehicles is 7.";

        // Step 4: Construct the input prompt
        String requestBody = "{ \"inputs\": { \"question\": \"What is the insurance risk on a scale of 1 to 10 for " + field + "?\", \"context\": \"" + context + "\" } }";

        // Step 5: Create the HTTP entity
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Step 6: Send the POST request to the Hugging Face API
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        // Step 7: Log the raw API response for debugging
        String rawResponse = response.getBody();
        System.out.println("Raw API Response: " + rawResponse);

        // Step 8: Parse the response to extract the risk value
        try {
            JSONObject jsonResponse = new JSONObject(rawResponse);
            String answer = jsonResponse.getString("answer"); // Extract the answer
            return answer;
        } catch (org.json.JSONException e) {
            // Handle errors or unexpected responses
            return "Error: Unable to parse response";
        }
    }

    public List<InsuranceProType> getAllInsuranceProTypes() {
        return insuranceProTypeRepository.findAll();
    }

    public Optional<InsuranceProType> getInsuranceProTypeById(Long id) {
        return insuranceProTypeRepository.findById(id);
    }

    public InsuranceProType createInsuranceProType(InsuranceProType insuranceProType) {
        String risk = getRisk(insuranceProType.getField());
        System.out.println("Risk: " + risk);
        insuranceProType.setRisk(risk);
        return insuranceProTypeRepository.save(insuranceProType);    }

    public void deleteInsuranceProType(Long id) {
        insuranceProTypeRepository.deleteById(id);
    }

    public InsuranceProType updateInsuranceProType(InsuranceProType insuranceProType) {
        return insuranceProTypeRepository.save(insuranceProType);
    }

    //*******

    @Value("${huggingface.api.key}")
    private String huggingFaceApiKey;




    public InsuranceProType saveInsurance(InsuranceProType insurance) {
        String risk = getRisk(insurance.getField());
        System.out.println("Risk: " + risk);
        insurance.setRisk(risk);
        return insuranceRepository.save(insurance);
    }

    @Autowired
    private InsuranceProTypeRepository insuranceRepository;

    public InsuranceProType findById(Long insuranceProTypeId) {
        return insuranceRepository.findById(insuranceProTypeId)
                .orElseThrow(() -> new RuntimeException("InsuranceProType not found with id: " + insuranceProTypeId));
    }
}

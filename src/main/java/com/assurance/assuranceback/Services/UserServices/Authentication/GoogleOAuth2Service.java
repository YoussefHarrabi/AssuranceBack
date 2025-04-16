package com.assurance.assuranceback.Services.UserServices.Authentication;

import com.assurance.assuranceback.DTOs.GoogleAuthRequest;
import com.assurance.assuranceback.DTOs.OAuth2AuthResponse;
import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.IdentityType;
import com.assurance.assuranceback.Enum.Role;
import com.assurance.assuranceback.Repository.UserRepositories.UserRepository;
import com.assurance.assuranceback.Services.UserServices.MfaService;
import com.assurance.assuranceback.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

@Service
public class GoogleOAuth2Service {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final JwtUtils jwtService;
    private final MfaService mfaService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public GoogleOAuth2Service(UserRepository userRepository, JwtUtils jwtService, MfaService mfaService) {
        this.webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.mfaService = mfaService;
    }
    public OAuth2AuthResponse authenticateUser(GoogleAuthRequest authRequest) {
        // Exchange code for access token
        Map<String, Object> tokenResponse = exchangeCodeForToken(authRequest.getCode(), authRequest.getRedirectUri());
        String accessToken = (String) tokenResponse.get("access_token");

        // Get user info from Google
        Map<String, Object> userInfo = getUserInfo(accessToken);

        // Find or create user
        User user = processUserInfo(userInfo);

        // Check if MFA is enabled
        boolean mfaEnabled = mfaService.isMfaEnabled(user);

        if (mfaEnabled) {
            // Return response indicating MFA is required
            return OAuth2AuthResponse.builder()
                    .requiresMfa(true)
                    .email(user.getEmail())
                    .userId(user.getId())
                    .build();
        } else {
            // Generate JWT token
            String token = jwtService.generateToken(user.getEmail(), user.getRoles(), user.getId());

            // Return response with token
            return OAuth2AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .requiresMfa(false)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        return webClient.post()
                .uri("/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserInfo(String accessToken) {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com")
                .build()
                .get()
                .uri("/oauth2/v3/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    private User processUserInfo(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            // Create new user
            String firstName = (String) userInfo.get("given_name");
            String lastName = (String) userInfo.get("family_name");
            String sub = (String) userInfo.get("sub"); // Google's unique user identifier

            User newUser = User.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .birthday(LocalDate.now()) // Default value, should be updated by user
                    .identityType(IdentityType.PASSPORT) // Default value, should be updated by user
                    .numberOfIdentity(sub) // Using Google's sub as a placeholder
                    .phoneNumber("") // Default value, should be updated by user
                    .address("") // Default value, should be updated by user
                    .password(UUID.randomUUID().toString()) // Random password since we're using OAuth
                    .roles(Set.of(Role.CLIENT)) // Default role
                    .build();

            return userRepository.save(newUser);
        }
    }
}
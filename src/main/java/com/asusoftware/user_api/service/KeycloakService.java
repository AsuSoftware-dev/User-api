package com.asusoftware.user_api.service;


import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import com.asusoftware.user_api.model.dto.UserDto;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;


    private Keycloak keycloak;

    // Inițializează Keycloak după ce valorile din application.yml sunt injectate
    @PostConstruct
    public void init() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)  // Folosește valoarea injectată din application.yml
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
    }

    // Creează un utilizator în Keycloak cu rolul specific
    public void createUserInKeycloak(UserDto userDto) {
        UserRepresentation user = getUserRepresentation(userDto);

        // Atribuie rolul corect utilizatorului pe baza Role-ului
        String keycloakRole = switch (userDto.getRole()) {
            case USER -> "USER";
            case PLACE -> "PLACE";
            case ADMIN -> "ADMIN";
        };

        // Adaugă rolul în atributele utilizatorului
        user.singleAttribute("role", keycloakRole);

        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() != 201) {  // 201 Created este statusul de succes pentru crearea utilizatorului
                throw new RuntimeException("Failed to create user in Keycloak, status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }

    private static UserRepresentation getUserRepresentation(UserDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.isEnabled());

        // Setează parola utilizatorului
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(userDto.getPassword());
        user.setCredentials(Collections.singletonList(passwordCred));
        return user;
    }
}

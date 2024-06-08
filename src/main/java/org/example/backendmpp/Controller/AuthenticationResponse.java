package org.example.backendmpp.Controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String role;
}


/*
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    private boolean authenticated;
    private String token;

    public AuthenticationResponse(boolean authenticated, String token) {
        this.authenticated = authenticated;
        this.token = token;
    }
}


 */
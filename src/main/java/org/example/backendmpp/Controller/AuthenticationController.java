package org.example.backendmpp.Controller;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backendmpp.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        //return ResponseEntity.ok(service.autheticate(request));
        var response = service.autheticate(request);
        if(response == null){
            System.out.println("Response is null");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Response is not null");
        System.out.println("Response: " + response);
        System.out.println("Token: " + response.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        ///return ResponseEntity.ok(service.register(request));
        var response = service.register(request);
        if(response == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }
}


/*
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/{username}/{password}")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@PathVariable String username, @PathVariable String password) {
        AuthenticationResponse response = authenticationService.authenticateUser(username, password);
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}

 */

package org.example.backendmpp.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backendmpp.JwtService;
import org.example.backendmpp.Model.User.Role;
import org.example.backendmpp.Model.User.User;
import org.example.backendmpp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse autheticate(AuthenticationRequest request) {

        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            System.out.println("Password does not match");
            return null;
        }
        var jwtToken = jwtService.generateToken(user);
        System.out.println("JWT Token: " + jwtToken);
        System.out.println("Role: " + user.getRole());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
        .build();
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
        .build();
    }

    public AuthenticationResponse authenticateAdmin(AuthenticationRequest request) {

        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            System.out.println("Password does not match");
            return null;
        }
        var jwtToken = jwtService.generateToken(user);
        System.out.println("JWT Token: " + jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}



/*
@Service
public class AuthenticationService {

    @Autowired
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticationResponse authenticateUser(String username, String password) {
        boolean exists = userRepository.existsByUsernameAndPassword(username, password);
        if (exists) {
            String token = generateToken(username);
            return new AuthenticationResponse(true, token);
        } else {
            return new AuthenticationResponse(false, null);
        }
    }

    private String generateToken(String username) {
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
        var user = userRepository.findByUsername(username);
        int userId = user.get().getId();
        return "{" +
                "\"id\": \"" + userId + "\"," +
                "\"expirationDate\": \"" + expirationDate + "\"" +
                "}";
    }
}
*/
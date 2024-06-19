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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private String username;

    public AuthenticationResponse autheticate(AuthenticationRequest request) {
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            System.out.println("Password does not match");
            return null;
        }
        var jwtToken = jwtService.generateToken(user);
        System.out.println("JWT Token: " + jwtToken);
        System.out.println("Role: " + user.getRole());
        this.username = user.getUsername();
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
                .locations("1,2")
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
        .build();
    }

    public AuthenticationResponse registerManager(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
        .build();
    }

    public List<Integer> getUserLocations() {
        User user = repository.findByUsername(username).orElseThrow();
        String[] locations = user.getLocations().split(",");
        List<Integer> locationList = new ArrayList<>();
        for (String location : locations) {
            locationList.add(Integer.parseInt(location));
        }
        return locationList;
    }

    public String getUsernameRole(String username) {
        User user = repository.findByUsername(username).orElseThrow();
        return user.getRole().toString();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void deleteUser(Integer id) {
        User user = repository.findById(id).orElseThrow();
        repository.delete(user);
    }

    public Boolean checkUserIsAdmin(){
        User user = repository.findByUsername(username).orElseThrow();
        return user.getRole().equals(Role.ADMIN);
    }

    public void editUser(User usernew, Integer id){
        User user = repository.findById(id).orElseThrow();
        user.setFirstname(usernew.getFirstname());
        user.setLastname(usernew.getLastname());
        user.setUsername(usernew.getUsername());
        System.out.println("Password: " + usernew.getPassword());
        //if password is not "" then encode it
        if(!usernew.getPassword().equals("")){
            user.setPassword(passwordEncoder.encode(usernew.getPassword()));
        }
        ///else keep the old password
        user.setRole(usernew.getRole());
        user.setLocations(usernew.getLocations());

        repository.save(user);
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
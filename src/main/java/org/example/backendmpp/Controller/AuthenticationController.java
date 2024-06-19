package org.example.backendmpp.Controller;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backendmpp.JwtService;
import org.example.backendmpp.Model.User.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;

import java.util.List;

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

    @PostMapping("/registerAdmin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        var response = service.registerAdmin(request);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerManager")
    public ResponseEntity<AuthenticationResponse> registerManager(
            @RequestBody RegisterRequest request
    ) {
        var response = service.registerManager(request);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    //get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(service.getAllUsers());
    }


    //edit user
    @PutMapping("/edit/{id}")
    public ResponseEntity<User> editUser(
            @PathVariable("id") Integer id,
            @RequestBody User user
    ){
        service.editUser(user, id);
        return ResponseEntity.ok(user);
    }

    //delete user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Integer id
    ){
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
package com.example.JuniorInterview.Controller;

import com.example.JuniorInterview.Service.UserService;
import com.example.JuniorInterview.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Validated @RequestBody SignUpRequest request) {
        userService.signup(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User created");
    }
}

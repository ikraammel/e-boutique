package com.ecommerce.demo.auth;

import com.ecommerce.demo.auth.request.LoginRequest;
import com.ecommerce.demo.auth.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrate")
    public ResponseEntity<AuthResponse> Register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> Authenticate(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.authenticate(request));
    }
}

package com.ecommerce.demo.config;

import com.ecommerce.demo.models.Role;
import com.ecommerce.demo.models.User;
import com.ecommerce.demo.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Vérifie si l'utilisateur existe déjà
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(name);
            newUser.setRole(Role.USER);
            return userRepository.save(newUser);
        });
        if (user.getRole() == null) {
            user.setRole(Role.USER);
            userRepository.save(user);
        }
        // Génère un JWT
        String jwtToken = jwtService.generateToken(user);

        // Redirige vers le front avec le token
        String redirectUrl = "http://localhost:5173/login/success?token=" +
                URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}

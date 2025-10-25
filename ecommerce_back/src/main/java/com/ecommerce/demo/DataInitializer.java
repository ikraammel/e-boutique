//package com.ecommerce.demo;
//
//import com.ecommerce.demo.models.Role;
//import com.ecommerce.demo.models.User;
//import com.ecommerce.demo.repositories.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (!userRepository.existsByEmail("ikramadmin@gmail.com")) {
//            User admin = new User();
//            admin.setFirstName("Admin");
//            admin.setEmail("ikramadmin@gmail.com");
//            admin.setPassword(passwordEncoder.encode("ikram123")); // mot de passe crypté
//            admin.setRole(Role.ADMIN);
//            userRepository.save(admin);
//            System.out.println("Admin créé !");
//        }
//    }
//}
//

package com.mesara.app.config;

import com.mesara.app.domain.Role;
import com.mesara.app.domain.User;
import com.mesara.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Value("${app.admin.username}")
    private String adminUser;

    @Value("${app.admin.password}")
    private String adminPass;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            Optional<User> existingAdmin = userRepository.findByUsername(adminUser);

            if (existingAdmin.isEmpty()) {
                User admin = User.builder()
                        .username(adminUser)
                        .password(passwordEncoder.encode(adminPass))
                        .role(Role.ROLE_ADMIN)
                        .active(true)
                        .build();

                userRepository.save(admin);
                System.out.println("INFO: Admin nalog (" + adminUser + ") je uspešno kreiran!");
            } else {
                System.out.println("INFO: Admin nalog (" + adminUser + ") već postoji u bazi.");
            }
        };
    }
}
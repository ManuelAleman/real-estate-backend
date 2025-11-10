package com.realestate.realestate.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.realestate.realestate.entity.Role;
import com.realestate.realestate.enums.RoleName;
import com.realestate.realestate.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with default data...");
        
        createRoleIfNotExists(RoleName.USER);
        createRoleIfNotExists(RoleName.SELLER);
        createRoleIfNotExists(RoleName.ADMIN);
        
        log.info("Database initialization completed.");
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        } else {
            log.info("Role already exists: {}", roleName);
        }
    }
}

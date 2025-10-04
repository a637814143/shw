package com.example.housekeeping.service;

import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.CurrentUserResponse;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void userRegisterShouldPersistNormalizedDataAndAllowLogin() {
        Map<String, Object> profile = authService.userRegister("  alice  ", "password123", "13800000001");

        assertEquals("alice", profile.get("username"));
        assertEquals("13800000001", profile.get("phone"));
        Long userId = ((Number) profile.get("id")).longValue();

        User saved = userRepository.findById(userId).orElseThrow();
        assertEquals("alice", saved.getUsername());
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));

        AuthResponse login = authService.userLogin("alice", "password123");
        assertNotNull(login.token());
        assertEquals("USER", login.userType());
        assertEquals(userId, ((Number) login.profile().get("id")).longValue());

        assertThrows(IllegalArgumentException.class,
                () -> authService.userRegister("alice", "another123", "13800000002"));
        assertThrows(IllegalArgumentException.class,
                () -> authService.userRegister("alice2", "another123", "13800000001"));
    }

    @Test
    void providerRegisterShouldInitializeStatusAndSupportLogin() {
        Map<String, Object> profile = authService.providerRegister(" ProviderOne ", "secure123", "13900000001");

        assertEquals("ProviderOne", profile.get("username"));
        Long providerId = ((Number) profile.get("id")).longValue();
        assertEquals(0, ((Number) profile.get("certificationStatus")).intValue());
        assertEquals(1, ((Number) profile.get("status")).intValue());

        ServiceProvider provider = serviceProviderRepository.findById(providerId).orElseThrow();
        assertEquals(0, provider.getCertificationStatus());
        assertEquals(1, provider.getStatus());
        assertTrue(passwordEncoder.matches("secure123", provider.getPassword()));

        AuthResponse login = authService.providerLogin("ProviderOne", "secure123");
        assertEquals("PROVIDER", login.userType());
        assertEquals(providerId, ((Number) login.profile().get("id")).longValue());

        assertThrows(IllegalArgumentException.class,
                () -> authService.providerRegister("ProviderOne", "another123", "13900000002"));
        assertThrows(IllegalArgumentException.class,
                () -> authService.providerRegister("ProviderTwo", "another123", "13900000001"));
    }

    @Test
    void adminRegisterShouldNormalizeAndPreventDuplicates() {
        Map<String, Object> profile = authService.adminRegister("  manager  ", "AdminPass1", "13700000001");

        assertEquals("manager", profile.get("username"));
        Long adminId = ((Number) profile.get("id")).longValue();

        Admin admin = adminRepository.findById(adminId).orElseThrow();
        assertEquals(1, admin.getStatus());
        assertTrue(passwordEncoder.matches("AdminPass1", admin.getPassword()));

        AuthResponse login = authService.adminLogin("manager", "AdminPass1");
        assertEquals("ADMIN", login.userType());
        assertEquals(adminId, ((Number) login.profile().get("id")).longValue());

        assertThrows(IllegalArgumentException.class,
                () -> authService.adminRegister("manager", "another123", "13700000002"));
        assertThrows(IllegalArgumentException.class,
                () -> authService.adminRegister("manager2", "another123", "13700000001"));
    }

    @Test
    void changePasswordShouldValidateCredentialsAndUpdateSecret() {
        Map<String, Object> profile = authService.userRegister("bob", "password123", "13600000001");
        Long userId = ((Number) profile.get("id")).longValue();

        authService.changePassword(userId, "USER", "password123", "newPass456");
        User updated = userRepository.findById(userId).orElseThrow();
        assertTrue(passwordEncoder.matches("newPass456", updated.getPassword()));

        assertThrows(IllegalArgumentException.class, () -> authService.userLogin("bob", "password123"));
        AuthResponse login = authService.userLogin("bob", "newPass456");
        assertEquals("USER", login.userType());

        assertThrows(IllegalArgumentException.class,
                () -> authService.changePassword(userId, "USER", "wrongOld", "anotherPass123"));

        CurrentUserResponse current = authService.loadCurrentUser(userId, "user");
        assertEquals(userId, current.userId());
        assertEquals("USER", current.userType());
        assertEquals("bob", current.username());
    }
}

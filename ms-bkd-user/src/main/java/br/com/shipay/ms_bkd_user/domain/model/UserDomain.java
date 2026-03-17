package br.com.shipay.ms_bkd_user.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserDomain {

    private Long id;

    private String name;

    private String email;

    private String password;

    private RoleDomain role;

    private Set<ClaimDomain> claims = new HashSet<>();

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private UserDomain(Long id, String name, String email, String password, RoleDomain role, Set<ClaimDomain> claims,
                       LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.claims = claims;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserDomain create(String name, String email, String password, RoleDomain role) {

        validateName(name);
        validateEmail(email);
        validateRole(role);

        final String finalPassword = (password == null || password.isBlank()) ? generateRandomPassword() : password;

        return new UserDomain(null, name, email, finalPassword, role, null, LocalDate.now(), null);
    }

    public static UserDomain restore(Long id, String name, String email, String password, RoleDomain role,
                                     Set<ClaimDomain> claims, LocalDate createdAt, LocalDate updatedAt) {
        return new UserDomain(id, name, email, password, role, claims, createdAt, updatedAt);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
    }

    private static void validateRole(RoleDomain role) {
        if (role == null) {
            throw new IllegalArgumentException("Role é obrigatório");
        }
    }

    private static String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8) + "@Shipay2026";
    }

}

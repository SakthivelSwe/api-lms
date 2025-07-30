package com.tvm.security;

import com.tvm.client.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class TokenValidator {

    @Autowired
    private AuthClient authClient;

    public Map<String, String> validate(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new RuntimeException("❌ Missing or invalid Authorization header");
        }

        try {
            ResponseEntity<Map<String, String>> response = authClient.validateToken(tokenHeader);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, String> body = response.getBody();
                System.out.println("✅ Token validated. Response: " + body);
                return body;
            } else {
                throw new RuntimeException("❌ Token validation failed with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Error during token validation: " + e.getMessage(), e);
        }
    }

    public void validateWithRoles(String tokenHeader, Set<String> allowedRoles) {
        Map<String, String> claims = validate(tokenHeader);

        if (claims == null || !claims.containsKey("role")) {
            throw new RuntimeException("❌ Role not found in token claims");
        }

        String role = claims.get("role");
        System.out.println("🔍 Role from token: " + role);
        System.out.println("✅ Allowed roles: " + allowedRoles);

        if (role != null && !allowedRoles.contains(role) && !allowedRoles.contains("ROLE_" + role)) {
            throw new RuntimeException("❌ Access denied for role: " + role);
        }
    }

}
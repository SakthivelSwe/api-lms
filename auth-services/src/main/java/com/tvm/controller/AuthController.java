package com.tvm.controller;

import com.tvm.model.UserEntity;
import com.tvm.repository.UserRepository;
import com.tvm.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserEntity user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password must not be null"));
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", userDetails.getUsername(),
                    "role", role
            ));

        } catch (Exception e) {
            e.printStackTrace(); // ✅ Show the exact cause of the 500 error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Username or Password"));
        }
    }
    @GetMapping("validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String tokenHeader)
    {
        try {
            // Check if token is present and starts with "Beare
            if (tokenHeader==null||!tokenHeader.startsWith("Bearer "))
            {
                return ResponseEntity.badRequest().body(Map.of("error","Missing or invalid Authorization header"));
            }
            String token=tokenHeader.substring(7);
            // Validate and extract username
            String username=jwtUtil.extractUsername(token);
            if (username==null||!jwtUtil.validateToken(token))
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid or expired token"));
            }
            return ResponseEntity.ok(Map.of("username",username,"message","token valid"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Token validation failed"));
        }
    }
//    @GetMapping("/validate/{id}")
//    public ResponseEntity<?> validateUserById(@PathVariable Long id)
//    {
//        if (id==null||id<=0)
//        {
//            return ResponseEntity.badRequest().body(Map.of("error","Invalid ID"));
//        }
//        //Get the currently logged-in username from JWT
//        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername=auth.getName();
//        //Find the user by id
//        return userRepository.findById(id)
//                .map(user->{
//                    if (!user.getUsername().equals(currentUsername))
//                    {
//                        return ResponseEntity.badRequest().body(Map.of("error","Unauthorized to access this user's data"));
//                    }
//                    return ResponseEntity.ok(Map.of(
//                            "id",user.getId(),
//                            "username",user.getUsername(),
//                            "role",user.getRole()
//                    ));
//                })
//                .orElseGet(()->ResponseEntity.badRequest().body(Map.of("error", "User not found with ID: " + id)));
//    }
//    @GetMapping("validate/{id}")
//    public ResponseEntity<?> validateByUserId(@PathVariable Long id)
//    {
//        if (id==null||id <= 0) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID"));
//        }
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = auth.getName();
//        boolean isAdmin = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(role -> role.equals("ROLE_ADMIN"));
//        return userRepository.findById(id)
//                .map(user -> {
//                    if (!isAdmin && !user.getUsername().equals(currentUsername)) {
//                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                                Map.of("error", "Access denied: Cannot validate other user's data")
//                        );
//                    }
//                    return ResponseEntity.ok(Map.of(
//                            "id", user.getId(),
//                            "username", user.getUsername(),
//                            "role", user.getRole()
//                    ));
//                })
//                .orElseGet(() -> ResponseEntity.badRequest()
//                        .body(Map.of("error", "User not found with ID: " + id)));
//    }
//@GetMapping("/validate/{id}")
//public ResponseEntity<?> validateUserById(@PathVariable Long id) {
//    if (id == null || id <= 0) {
//        return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID"));
//    }
//
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//    String currentUsername = auth.getName();
//    var roles = auth.getAuthorities().stream()
//            .map(GrantedAuthority::getAuthority)
//            .toList();
//
//    boolean isAdmin = roles.contains("ROLE_ADMIN");
//    boolean isInstructor = roles.contains("ROLE_INSTRUCTOR");
//    boolean isStudent = roles.contains("ROLE_STUDENT");
//
//    return userRepository.findById(id)
//            .map(targetUser -> {
//                String targetUsername = targetUser.getUsername();
//                String targetRole = String.valueOf(targetUser.getRole()); // assuming getRole() returns something like ROLE_STUDENT
//
//                // ✅ Allow ADMIN to access all
//                if (isAdmin) {
//                    return ResponseEntity.ok(Map.of(
//                            "id", targetUser.getId(),
//                            "username", targetUser.getUsername(),
//                            "role", targetUser.getRole()
//                    ));
//                }
//
//
//
//
//                // ✅ Allow INSTRUCTOR only if target user is STUDENT
//                if (isInstructor || targetRole.equals("ROLE_STUDENT")) {
//                    return ResponseEntity.ok(Map.of(
//                            "id", targetUser.getId(),
//                            "username", targetUser.getUsername(),
//                            "role", targetUser.getRole()
//                    ));
//                } else if (isInstructor) {
//                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                            .body(Map.of("error", "Instructors can only validate students"));
//                }
//
//
//                // ✅ Allow STUDENT to validate only their own ID
//                if (isStudent && targetUsername.equals(currentUsername)) {
//                    return ResponseEntity.ok(Map.of(
//                            "id", targetUser.getId(),
//                            "username", targetUser.getUsername(),
//                            "role", targetUser.getRole()
//                    ));
//                }
//
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Access denied"));
//            })
//            .orElseGet(() -> ResponseEntity.badRequest()
//                    .body(Map.of("error", "User not found with ID: " + id)));
//}
@GetMapping("/validate/{id}")
public ResponseEntity<?> validateUserById(@PathVariable Long id) {
    if (id == null || id <= 0) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID"));
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = auth.getName();
    var roles = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    boolean isAdmin = roles.contains("ROLE_ADMIN");
    boolean isInstructor = roles.contains("ROLE_INSTRUCTOR");
    boolean isStudent = roles.contains("ROLE_STUDENT");

    return userRepository.findById(id)
            .map(targetUser -> {
                String targetUsername = targetUser.getUsername();
                String targetRole = String.valueOf(targetUser.getRole());

                // ✅ ADMIN can view anyone
                if (isAdmin) {
                    return ResponseEntity.ok(Map.of(
                            "id", targetUser.getId(),
                            "username", targetUser.getUsername(),
                            "role", targetUser.getRole()
                    ));
                }

                // ✅ INSTRUCTOR logic
                if (isInstructor) {
                    // Own details
                    if (targetUsername.equals(currentUsername)) {
                        return ResponseEntity.ok(Map.of(
                                "id", targetUser.getId(),
                                "username", targetUser.getUsername(),
                                "role", targetUser.getRole()
                        ));
                    }

                    // Only allow viewing STUDENT users
                    if ("STUDENT".equals(targetRole)) {
                        return ResponseEntity.ok(Map.of(
                                "id", targetUser.getId(),
                                "username", targetUser.getUsername(),
                                "role", targetUser.getRole()
                        ));
                    }

                    // Not allowed to view other instructors or admins
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Instructors can only view student details and their own."));
                }

                // ✅ STUDENT can view only their own details
                if (isStudent && targetUsername.equals(currentUsername)) {
                    return ResponseEntity.ok(Map.of(
                            "id", targetUser.getId(),
                            "username", targetUser.getUsername(),
                            "role", targetUser.getRole()
                    ));
                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            })
            .orElseGet(() -> ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found with ID: " + id)));
}


}
package com.monkdevs.elearning.Controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.monkdevs.elearning.Controllers.AuthRequest.LoginRequest;
import com.monkdevs.elearning.Controllers.AuthRequest.SignupRequest;
import com.monkdevs.elearning.Controllers.AuthResponse.JwtResponse;
import com.monkdevs.elearning.Controllers.AuthResponse.ResponseMessage;
import com.monkdevs.elearning.Models.Role;
import com.monkdevs.elearning.Models.RoleName;
import com.monkdevs.elearning.Models.User;
import com.monkdevs.elearning.Repositories.RoleRepository;
import com.monkdevs.elearning.Repositories.UserRepository;
import com.monkdevs.elearning.Security.Jwt.JwtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken."),
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in exist"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        LocalDateTime createdDate = LocalDateTime.now();

        user.setCreatedDate(createdDate);
        user.setIsActive(true);

        Set<Role> roles = new HashSet<>();
        if (signupRequest.getRole() != null) {
            Set<String> strRoles = signupRequest.getRole();

            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Fail! Cause: User Role not found"));
                        roles.add(adminRole);
                        break;
                    case "headOfDepartment":
                        Role headOfDepartment = roleRepository.findByName(RoleName.ROLE_HOD)
                                .orElseThrow(() -> new RuntimeException("Fail! Cause: User Role not found"));
                        roles.add(headOfDepartment);
                        break;
                    case "student":
                        Role student = roleRepository.findByName(RoleName.ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Fail! Cause: User Role not found"));
                        roles.add(student);
                        break;
                    case "teacher":
                        Role teacher = roleRepository.findByName(RoleName.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Fail! Cause: User Role not found"));
                        roles.add(teacher);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
                        roles.add(userRole);
                }
            });
        } else {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found"));
            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(
                new ResponseMessage("User " + signupRequest.getUsername() + " is registered successfully"),
                HttpStatus.OK);
    }

}

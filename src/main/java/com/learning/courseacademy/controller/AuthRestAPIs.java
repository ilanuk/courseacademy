package com.learning.courseacademy.controller;

import com.learning.courseacademy.message.request.LoginForm;
import com.learning.courseacademy.message.request.SignUpForm;
import com.learning.courseacademy.message.response.JwtResponse;
import com.learning.courseacademy.model.Role;
import com.learning.courseacademy.model.RoleName;
import com.learning.courseacademy.model.User;
import com.learning.courseacademy.repository.RoleRepository;
import com.learning.courseacademy.repository.UserRepository;
import com.learning.courseacademy.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthRestAPIs {
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername(),
                  loginRequest.getPassword()
          )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt =jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if(userRepository.existsByUserName(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!",
                    HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByUserEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }
        User user = new User(signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole =roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                            .orElseThrow(()-> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(adminRole);
                    break;
                case "pm":
                    Role pmRole =roleRepository.findByRoleName(RoleName.ROLE_PM)
                            .orElseThrow(()-> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(pmRole);
                    break;
                default:
                    Role userRole =roleRepository.findByRoleName(RoleName.ROLE_USER)
                            .orElseThrow(()-> new RuntimeException("Fail! -> Cause: User Role not find."));
                    roles.add(userRole);
                    break;
            }
        });
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok().body("User registered successfully!");
    }
}

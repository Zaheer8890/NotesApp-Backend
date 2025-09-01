package com.notesapi.NoteApp.controller;

import com.notesapi.NoteApp.Security.JwtUtil;
import com.notesapi.NoteApp.dto.AuthResponseDto;
import com.notesapi.NoteApp.dto.UserDto;
import com.notesapi.NoteApp.dto.UserLoginDto;
import com.notesapi.NoteApp.dto.UserRegistrationDto;
import com.notesapi.NoteApp.entity.User;
import com.notesapi.NoteApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = userService.createUser(
                registrationDto.getEmail(),
                registrationDto.getName(),
                registrationDto.getPassword()
        );

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());

        return ResponseEntity.ok(new AuthResponseDto(token, "bearer", userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginDto) {
        Optional<User> userOptional = userService.findByEmail(loginDto.getEmail());

        if (userOptional.isEmpty() ||
                !userService.validatePassword(loginDto.getPassword(), userOptional.get().getPasswordHash())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        User user = userOptional.get();
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName());

        return ResponseEntity.ok(new AuthResponseDto(token, "bearer", userDto));
    }
}

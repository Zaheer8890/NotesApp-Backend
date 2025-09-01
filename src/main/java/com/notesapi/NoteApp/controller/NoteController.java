package com.notesapi.NoteApp.controller;

import com.notesapi.NoteApp.Security.JwtUtil;
import com.notesapi.NoteApp.dto.NoteDto;
import com.notesapi.NoteApp.dto.ShareResponseDto;
import com.notesapi.NoteApp.entity.Note;
import com.notesapi.NoteApp.entity.User;
import com.notesapi.NoteApp.service.NoteService;
import com.notesapi.NoteApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notes")
@CrossOrigin(origins = "http://localhost:3000")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getCurrentUserId(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        return jwtUtil.getUserIdFromToken(jwt);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> listNotes(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<NoteDto> notes = noteService.findByUserId(userId)
                .stream()
                .map(note -> {
                    NoteDto dto = new NoteDto();
                    dto.setId(note.getId());
                    dto.setTitle(note.getTitle());
                    dto.setContent(note.getContent());
                    dto.setIsShared(note.getIsShared());
                    dto.setShareToken(note.getShareToken());
                    dto.setCreatedAt(note.getCreatedAt());
                    dto.setUpdatedAt(note.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<?> createNote(HttpServletRequest request, @Valid @RequestBody NoteDto noteDto) {
        Long userId = getCurrentUserId(request);
        User user = userService.findByEmail(jwtUtil.getEmailFromToken(getJwtFromRequest(request)))
                .orElseThrow(() -> new RuntimeException("User not found"));
        Note note = noteService.createNote(user, noteDto.getTitle(), noteDto.getContent());
        noteDto.setId(note.getId());
        noteDto.setCreatedAt(note.getCreatedAt());
        noteDto.setUpdatedAt(note.getUpdatedAt());
        return ResponseEntity.ok(noteDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Optional<Note> noteOpt = noteService.findByIdAndUserId(id, userId);
        if (noteOpt.isEmpty()) return ResponseEntity.notFound().build();
        Note note = noteOpt.get();

        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setIsShared(note.getIsShared());
        dto.setShareToken(note.getShareToken());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, HttpServletRequest request, @Valid @RequestBody NoteDto noteDto) {
        Long userId = getCurrentUserId(request);
        Optional<Note> noteOpt = noteService.findByIdAndUserId(id, userId);
        if (noteOpt.isEmpty()) return ResponseEntity.notFound().build();

        Note updated = noteService.updateNote(noteOpt.get(), noteDto.getTitle(), noteDto.getContent());
        noteDto.setId(updated.getId());
        noteDto.setCreatedAt(updated.getCreatedAt());
        noteDto.setUpdatedAt(updated.getUpdatedAt());
        return ResponseEntity.ok(noteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Optional<Note> noteOpt = noteService.findByIdAndUserId(id, userId);
        if (noteOpt.isEmpty()) return ResponseEntity.notFound().build();
        noteService.deleteNote(noteOpt.get());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<?> shareNote(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Optional<Note> noteOpt = noteService.findByIdAndUserId(id, userId);
        if (noteOpt.isEmpty()) return ResponseEntity.notFound().build();

        String token = noteService.generateShareToken(noteOpt.get());
        String url = "http://localhost:8080/shared/" + token;
        return ResponseEntity.ok(new ShareResponseDto(url, token));
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<?> revokeShare(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Optional<Note> noteOpt = noteService.findByIdAndUserId(id, userId);
        if (noteOpt.isEmpty()) return ResponseEntity.notFound().build();
        noteService.revokeShare(noteOpt.get());
        return ResponseEntity.ok("Share revoked");
    }
}
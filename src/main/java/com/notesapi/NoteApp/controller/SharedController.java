package com.notesapi.NoteApp.controller;



import com.notesapi.NoteApp.dto.NoteDto;
import com.notesapi.NoteApp.entity.Note;
import com.notesapi.NoteApp.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/shared")
@CrossOrigin(origins = "http://localhost:3000")
public class SharedController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/{token}")
    public ResponseEntity<?> getSharedNote(@PathVariable String token) {
        Optional<Note> noteOpt = noteService.findByShareToken(token);
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
}


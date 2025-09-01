package com.notesapi.NoteApp.service;


import com.notesapi.NoteApp.entity.Note;
import com.notesapi.NoteApp.entity.User;
import com.notesapi.NoteApp.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public List<Note> findByUserId(Long userId) {
        return noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Note createNote(User user, String title, String content) {
        Note note = new Note(user, title, content);
        return noteRepository.save(note);
    }

    public Optional<Note> findByIdAndUserId(Long id, Long userId) {
        return noteRepository.findByIdAndUserId(id, userId);
    }

    public Note updateNote(Note note, String title, String content) {
        if (title != null) note.setTitle(title);
        if (content != null) note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNote(Note note) {
        noteRepository.delete(note);
    }

    public String generateShareToken(Note note) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String shareToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        note.setShareToken(shareToken);
        note.setIsShared(true);
        noteRepository.save(note);

        return shareToken;
    }

    public Optional<Note> findByShareToken(String shareToken) {
        return noteRepository.findByShareTokenAndIsSharedTrue(shareToken);
    }

    public void revokeShare(Note note) {
        note.setShareToken(null);
        note.setIsShared(false);
        noteRepository.save(note);
    }
}

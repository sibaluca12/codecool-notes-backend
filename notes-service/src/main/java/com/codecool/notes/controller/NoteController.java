package com.codecool.notes.controller;

import com.codecool.notes.entity.Modules;
import com.codecool.notes.entity.Note;
import com.codecool.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping("")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/{noteId}")
    public Note getNoteById(@PathVariable Long noteId) {
        return noteRepository.findNoteByNoteId(noteId);
    }



    @PostMapping("")
    public Note addNote(@RequestBody Note note) {
        return noteRepository.save(note);
    }

    @DeleteMapping("/{id}")
    public Note deleteNote(@PathVariable Long id) {
        return noteRepository.deleteByNoteId(id);
    }

   /* @GetMapping("/search/findByName")
    public List<Note> searchNoteByKeyWord(@RequestParam String kexWord) {
        return noteRepository.fuzzysearch??(String keyword);
    } */

    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable Long noteId, @RequestBody Note note) {
        return noteRepository.updateNote(noteId, note.getNoteTitle(), note.getNoteUrl(), note.getModule(), note.getWeek());
    }

    @GetMapping("/user/{userId}")
    public List<Note> getAllByUser(@PathVariable Long userId){
        return noteRepository.findAllByUserId(userId);
    }

    @GetMapping("/module/{module}")
    public List<Note> getAllByModule(@PathVariable Modules module){
        return noteRepository.findAllByModule(module);
    }

}

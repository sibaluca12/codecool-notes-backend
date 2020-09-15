package com.codecool.notes.repository;

import com.codecool.notes.entity.Modules;
import com.codecool.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAll();

    Note findNoteByNoteId(Long noteId);

    Note deleteByNoteId(Long noteId);

    @Query("update Note SET noteTitle = :title, noteUrl = :url, module =:module, week =:week WHERE noteId = :noteId")
    Note updateNote(@Param("noteId") Long noteId, @Param("title") String title, @Param("url") String url, @Param("module") Modules module, @Param("week") int week);

    List<Note> findAllByUserId(Long userId);

    List<Note> findAllByModule(Modules module);
}

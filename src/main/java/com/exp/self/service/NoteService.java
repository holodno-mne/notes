package com.exp.self.service;

import com.exp.self.model.Note;

import java.util.List;

public interface NoteService {
    void saveNote(String text);

    List<Note> getAllNotes();

    void updateNote(int index, String newText);

    void deleteNote(int index);
}

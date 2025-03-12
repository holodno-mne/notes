package com.exp.self.dao;

import com.exp.self.model.Note;

import java.util.List;

public interface NoteDao {
    void save(Note note);

    List<Note> findAll();

    void update(int index, String newText);

    void delete(int index);
}
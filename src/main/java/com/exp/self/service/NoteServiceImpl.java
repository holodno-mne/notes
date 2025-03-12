package com.exp.self.service;

import com.exp.self.annotation.Component;
import com.exp.self.annotation.Inject;
import com.exp.self.dao.NoteDao;
import com.exp.self.model.Note;

import java.util.List;

@Component
public class NoteServiceImpl implements NoteService {

    @Inject
    private NoteDao noteDao;

    @Override
    public void saveNote(String text) {
        Note note = new Note(text);
        noteDao.save(note);
    }

    @Override
    public List<Note> getAllNotes() {
        return noteDao.findAll();
    }

    @Override
    public void updateNote(int index, String newText) {
        noteDao.update(index, newText);
    }

    @Override
    public void deleteNote(int index) {
        noteDao.delete(index);
    }

}

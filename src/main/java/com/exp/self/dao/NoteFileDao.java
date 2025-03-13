package com.exp.self.dao;

import com.exp.self.annotation.Component;
import com.exp.self.annotation.Primary;
import com.exp.self.model.Note;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class NoteFileDao implements NoteDao {
    private static final String FILE_NAME = "note.txt";
    private List<Note> notes = new ArrayList<>();

    public NoteFileDao() {

        loadNotesFromFile();

        if (notes.isEmpty()) {
            notes.add(new Note("Это моя первая заметка в файле"));
            saveNotesToFile();
        }
    }

    @Override
    public void save(Note note) {
        notes.add(note);
        saveNotesToFile();
    }

    @Override
    public List<Note> findAll() {
        return notes;
    }

    @Override
    public void update(int index, String newText) {
        if (index >= 0 && index < notes.size()) {
            notes.get(index).setText(newText);
            saveNotesToFile();
        } else {
            throw new IllegalArgumentException("Неверный индекс");
        }
    }

    @Override
    public void delete(int index) {
        if (index >= 0 && index < notes.size()) {
            notes.remove(index);
            saveNotesToFile();
        } else {
            throw new IllegalArgumentException("Неверный индекс");
        }
    }

    private void loadNotesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notes.add(new Note(line));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла" + e.getMessage());
        }
    }

    private void saveNotesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Note note : notes) {
                writer.write(note.getText());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла" + e.getMessage());
        }
    }

}

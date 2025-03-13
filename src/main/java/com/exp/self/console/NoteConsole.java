package com.exp.self.console;

import com.exp.self.annotation.Component;
import com.exp.self.annotation.Inject;
import com.exp.self.model.Note;
import com.exp.self.service.NoteService;

import java.util.List;
import java.util.Scanner;

@Component
public class NoteConsole {

    @Inject
    private NoteService noteService;

    private final Scanner scanner;


    public NoteConsole() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Создать заметку");
            System.out.println("2. Просмотреть все заметки");
            System.out.println("3. Редактировать заметку");
            System.out.println("4. Удалить заметку");
            System.out.println("5. Выйти");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createNote();
                    break;
                case 2:
                    showAllNotes();
                    break;
                case 3:
                    editNote();
                    break;
                case 4:
                    deleteNote();
                    break;
                case 5:
                    System.out.println("Выход из приложения.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createNote() {
        System.out.println("\nВведите текст заметки: ");
        String text = scanner.nextLine();
        noteService.saveNote(text);
        System.out.println("\nЗаметка сохранена.");
    }

    private void showAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        if (notes.isEmpty()) {
            System.out.println("\nЗаметок нет");
        } else {
            System.out.println("\nЗаметки: ");
            for (int i = 0; i < notes.size(); i++) {
                System.out.println((i + 1) + ". " + notes.get(i));
            }
        }
    }

    private void editNote() {
        System.out.println("\nВведите номер заметки для редактирования: ");
        int editIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        System.out.println("\nВведите новый тест: ");
        String newText = scanner.nextLine();
        try {
            noteService.updateNote(editIndex, newText);
            System.out.println("\nЗаметка отредактирована.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nОшибка. Изменения не сохранены. " + e.getMessage());
        }
    }

    private void deleteNote() {
        System.out.println("\nВведите номер заметки для удаления");
        int deleteIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        try {
            noteService.deleteNote(deleteIndex);
            System.out.println("\nЗаметка удалена.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("\nОшибка. Неверный номер заметки. " + e.getMessage());
        }
    }
}

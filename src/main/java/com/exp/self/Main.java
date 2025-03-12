package com.exp.self;

import com.exp.self.console.NoteConsole;
import com.exp.self.context.ApplicationContext;
import com.exp.self.service.NoteService;
import com.exp.self.service.NoteServiceImpl;

public class Main {
    public static void main(String[] args) throws Exception {

        ApplicationContext context = ApplicationContext.getInstance();

        NoteService noteService = context.getBean(NoteServiceImpl.class);

        NoteConsole console = new NoteConsole(noteService);
        console.start();
    }
}
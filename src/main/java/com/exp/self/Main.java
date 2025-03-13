package com.exp.self;

import com.exp.self.console.NoteConsole;
import com.exp.self.context.ApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {

        ApplicationContext context = ApplicationContext.getInstance();

        NoteConsole console = context.getBean(NoteConsole.class);

        console.start();
    }
}
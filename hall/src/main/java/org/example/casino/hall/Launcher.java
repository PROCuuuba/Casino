package org.example.casino.hall;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--list")) {
            GameCatalog.discover().machines().forEach(machine ->
                    System.out.println(machine.id() + "\t" + machine.title()));
            return;
        }
        Application.launch(HelloApplication.class, args);
    }
}

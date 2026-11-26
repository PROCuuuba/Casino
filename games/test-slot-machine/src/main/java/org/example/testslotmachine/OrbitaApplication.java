package org.example.testslotmachine;

import javafx.application.Application;
import javafx.stage.Stage;

public final class OrbitaApplication extends Application {
    @Override
    public void start(Stage stage) {
        OrbitaMachine machine = new OrbitaMachine();
        machine.prepare(stage);
        stage.setTitle(machine.title());
        stage.show();
    }
}

package com.example.chain;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;

final class ClickSound {
    private static final AudioClip click = new AudioClip(ClickSound.class.getResource("click.mp3").toExternalForm());

    private ClickSound() {
    }

    static void install(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (isButton(event.getTarget())) click.play(0.3);
        });
    }

    private static boolean isButton(Object target) {
        if (!(target instanceof Node)) return false;
        for (Node node = (Node) target; node != null; node = node.getParent()) {
            if (node instanceof ButtonBase) return true;
        }
        return false;
    }
}

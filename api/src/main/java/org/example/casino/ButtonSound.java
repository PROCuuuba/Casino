package org.example.casino;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class ButtonSound {
    private static final AudioClip click = createClick();
    private static final Set<Scene> installedScenes = Collections.newSetFromMap(new WeakHashMap<>());

    private ButtonSound() {
    }

    public static void install(Scene scene) {
        if (!installedScenes.add(scene)) return;
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (isButton(event.getTarget())) play();
        });
    }

    private static boolean isButton(Object target) {
        if (!(target instanceof Node)) return false;
        for (Node node = (Node) target; node != null; node = node.getParent()) {
            if (node instanceof ButtonBase) return true;
        }
        return false;
    }

    private static void play() {
        if (click != null) click.play(0.3);
    }

    private static AudioClip createClick() {
        URL source = ButtonSound.class.getResource("click.mp3");
        return source == null ? null : new AudioClip(source.toExternalForm());
    }
}

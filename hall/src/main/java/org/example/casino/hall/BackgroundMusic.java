package org.example.casino.hall;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

final class BackgroundMusic {
    private MediaPlayer player;
    private boolean playing;

    void start() {
        if (player != null) {
            player.play();
            playing = true;
            return;
        }
        URL source = BackgroundMusic.class.getResource("/music/menu.mp3");
        if (source == null) return;
        try {
            player = new MediaPlayer(new Media(source.toExternalForm()));
            player.setVolume(0.14);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
            playing = true;
        } catch (RuntimeException ignored) {
            playing = false;
        }
    }

    boolean toggle() {
        if (player == null) {
            start();
            return playing;
        }
        if (playing) {
            player.pause();
            playing = false;
        } else {
            player.play();
            playing = true;
        }
        return playing;
    }
}

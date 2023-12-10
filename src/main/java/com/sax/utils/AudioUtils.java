package com.sax.utils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Objects;

public class AudioUtils {

    public static void playAudio(String filePath) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Objects.requireNonNull(AudioUtils.class.getResourceAsStream("/audios/" + filePath))));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

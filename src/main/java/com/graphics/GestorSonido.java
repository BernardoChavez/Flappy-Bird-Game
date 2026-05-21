package com.graphics;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Clase encargada de la reproducción de efectos de sonido.
 */
public class GestorSonido {
    public static void reproducirSonido(String nombreArchivo) {
        new Thread(() -> {
            try {
                // Forma más compatible de cargar recursos en Maven
                InputStream is = GestorSonido.class.getResourceAsStream("/sounds/" + nombreArchivo);
                if (is == null) {
                    System.err.println("No se encontró el sonido: " + nombreArchivo);
                    return;
                }
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error al reproducir sonido " + nombreArchivo + ": " + e.getMessage());
            }
        }).start();
    }
}

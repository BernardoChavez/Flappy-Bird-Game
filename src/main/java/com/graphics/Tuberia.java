package com.graphics;

/**
 * Clase que representa un obstáculo (tubería) en el juego.
 */
public class Tuberia {
    public float x;
    public float centroHuecoY;
    public boolean puntuadaP1;
    public boolean puntuadaP2;
    public boolean puntuadaP3; // Nuevo check para el tercer jugador

    // Configuración estática de las tuberías
    public static float ANCHO = 0.20f;
    public static float TAMANO_HUECO = 0.50f;

    public Tuberia(float x, float centroHuecoY) {
        this.x = x;
        this.centroHuecoY = centroHuecoY;
        this.puntuadaP1 = false;
        this.puntuadaP2 = false;
        this.puntuadaP3 = false;
    }

    public float obtenerIzquierda() {
        return x - ANCHO / 2;
    }

    public float obtenerDerecha() {
        return x + ANCHO / 2;
    }

    public float obtenerArribaHueco() {
        return centroHuecoY + TAMANO_HUECO / 2;
    }

    public float obtenerAbajoHueco() {
        return centroHuecoY - TAMANO_HUECO / 2;
    }
}

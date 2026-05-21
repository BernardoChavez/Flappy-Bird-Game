package com.graphics;

/**
 * Clase que representa al jugador (el pájaro).
 */
public class Pajaro {
    public float x, y;
    public float velocidadY;
    public int puntaje;
    public boolean estaMuerto;
    private float temporizadorAlas = 0;

    public float ancho = 0.12f;
    public float alto = 0.09f;

    public Pajaro(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocidadY = 0;
        this.puntaje = 0;
        this.estaMuerto = false;
    }

    public void saltar(float fuerza) {
        if (!estaMuerto) {
            velocidadY = fuerza;
        }
    }

    public void actualizar(float dt, float gravedad, float velMaxima) {
        if (!estaMuerto) {
            velocidadY += gravedad * dt;
            if (velocidadY < velMaxima)
                velocidadY = velMaxima;
            y += velocidadY * dt;
            temporizadorAlas += dt * 15; // Velocidad del aleteo
        }
    }

    public void renderizar(Renderizador renderizador, Shader shader, float r, float g, float b) {
        // La rotación ahora es 0 para que el cuerpo se mantenga estático (derechito)
        float rotacionEstatica = 0.0f;

        // 1. Cuerpo (Cuadrado estilo Retro)
        renderizador.dibujarCuadrado(shader, x, y, ancho, alto, rotacionEstatica, r, g, b, 1.0f);

        // 2. Cola
        renderizador.dibujarTriangulo(shader, x - ancho * 0.3f, y, 0.04f, 0.04f, rotacionEstatica, r * 0.8f, g * 0.8f,
                b * 0.8f, 1.0f);

        // 3. Ojo (Cuadrado)
        renderizador.dibujarCuadrado(shader, x + ancho * 0.2f, y + alto * 0.2f, 0.03f, 0.03f, rotacionEstatica, 1.0f,
                1.0f, 1.0f, 1.0f);
        renderizador.dibujarCuadrado(shader, x + ancho * 0.25f, y + alto * 0.2f, 0.015f, 0.015f, rotacionEstatica, 0.0f,
                0.0f, 0.0f, 1.0f);

        // 4. Pico
        renderizador.dibujarTriangulo(shader, x + ancho * 0.6f, y, 0.04f, 0.05f, -1.57f, 1.0f, 0.5f, 0.0f, 1.0f);

        // 5. Ala (Esta es la única que tiene "física" de aleteo)
        float oscilacionAla = (float) Math.sin(temporizadorAlas) * 0.3f;
        renderizador.dibujarCuadrado(shader, x - 0.02f, y - 0.01f, 0.06f, 0.04f, oscilacionAla, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float obtenerArriba() {
        return y + alto / 2;
    }

    public float obtenerAbajo() {
        return y - alto / 2;
    }

    public float obtenerIzquierda() {
        return x - ancho / 2;
    }

    public float obtenerDerecha() {
        return x + ancho / 2;
    }
}

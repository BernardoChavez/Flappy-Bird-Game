package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Clase principal que controla el flujo del juego.
 */
public class Juego {
    private static final int ANCHO = 900;
    private static final int ALTO = 700;
    private static int META_PUNTAJE = 10; // Puntaje para ganar (configurable)

    private long ventana;
    private Shader shader;
    private Renderizador renderizador;

    private Pajaro jugador1;
    private Pajaro jugador2;
    private Pajaro jugador3;
    private List<Tuberia> tuberias = new ArrayList<>();
    private Random aleatorio = new Random();

    private int estado = 0; // 0: Menú, 1: Jugando, 2: Game Over
    private int modoJugadores = 2;
    private boolean modoVictoria = false;

    private float temporizadorTuberias = 0;
    private float velocidadActualTuberias = 0.60f;
    private float intervaloAparicionActual = 1.8f;

    private boolean j1SaltoPrev = false;
    private boolean j2SaltoPrev = false;
    private boolean j3SaltoPrev = false;
    private boolean tecla1Prev = false;
    private boolean tecla2Prev = false;
    private boolean tecla3Prev = false;
    private boolean reiniciarPrev = false;

    public void ejecutar() {
        inicializar();
        reiniciarJuego();
        buclePrincipal();
        limpiar();
    }

    private void inicializar() {
        if (!GLFW.glfwInit())
            throw new IllegalStateException("No se pudo inicializar GLFW");

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        ventana = GLFW.glfwCreateWindow(ANCHO, ALTO, "Flappy Bird OpenGL - 3 Jugadores", 0, 0);
        if (ventana == 0)
            throw new RuntimeException("Error al crear la ventana");

        GLFW.glfwMakeContextCurrent(ventana);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(ventana);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        String vertexSrc = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                uniform vec2 uDesplazamiento;
                uniform vec2 uEscala;
                uniform float uRotacion;
                void main() {
                    float c = cos(uRotacion);
                    float s = sin(uRotacion);
                    vec2 posRotada = vec2(aPos.x * c - aPos.y * s, aPos.x * s + aPos.y * c);
                    vec2 posFinal = posRotada * uEscala + uDesplazamiento;
                    gl_Position = vec4(posFinal, aPos.z, 1.0);
                }
                """;

        String fragmentSrc = """
                #version 330 core
                uniform vec4 uColor;
                out vec4 FragColor;
                void main() {
                    FragColor = uColor;
                }
                """;

        shader = new Shader(vertexSrc, fragmentSrc);
        renderizador = new Renderizador();
    }

    private void reiniciarJuego() {
        jugador1 = new Pajaro(-0.6f, 0.0f);
        jugador2 = new Pajaro(-0.5f, 0.0f);
        jugador3 = new Pajaro(-0.4f, 0.0f);

        if (modoJugadores < 3)
            jugador3.estaMuerto = true;
        if (modoJugadores < 2)
            jugador2.estaMuerto = true;

        tuberias.clear();
        temporizadorTuberias = 0;
        velocidadActualTuberias = 0.60f;
        intervaloAparicionActual = 1.8f;
        modoVictoria = false;
        actualizarTitulo();
    }

    private void buclePrincipal() {
        float ultimoTiempo = (float) GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(ventana)) {
            float tiempoActual = (float) GLFW.glfwGetTime();
            float dt = tiempoActual - ultimoTiempo;
            ultimoTiempo = tiempoActual;
            if (dt > 0.033f)
                dt = 0.033f;

            procesarEntrada();
            actualizar(dt);
            renderizar();

            GLFW.glfwSwapBuffers(ventana);
            GLFW.glfwPollEvents();
        }
    }

    private void procesarEntrada() {
        if (GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(ventana, true);
        }

        if (estado == 0) {
            boolean t1 = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_1) == GLFW.GLFW_PRESS;
            if (t1 && !tecla1Prev) {
                modoJugadores = 1;
                reiniciarJuego();
                estado = 1;
            }
            tecla1Prev = t1;

            boolean t2 = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_2) == GLFW.GLFW_PRESS;
            if (t2 && !tecla2Prev) {
                modoJugadores = 2;
                reiniciarJuego();
                estado = 1;
            }
            tecla2Prev = t2;

            boolean t3 = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_3) == GLFW.GLFW_PRESS;
            if (t3 && !tecla3Prev) {
                modoJugadores = 3;
                reiniciarJuego();
                estado = 1;
            }
            tecla3Prev = t3;
        }

        if (estado == 1 && !modoVictoria) {
            boolean j1Salto = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS;
            if (j1Salto && !j1SaltoPrev) {
                jugador1.saltar(0.85f);
                GestorSonido.reproducirSonido("salto.wav");
            }
            j1SaltoPrev = j1Salto;

            boolean j2Salto = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
            if (j2Salto && !j2SaltoPrev) {
                jugador2.saltar(0.85f);
                GestorSonido.reproducirSonido("salto.wav");
            }
            j2SaltoPrev = j2Salto;

            boolean j3Salto = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS;
            if (j3Salto && !j3SaltoPrev) {
                jugador3.saltar(0.85f);
                GestorSonido.reproducirSonido("salto.wav");
            }
            j3SaltoPrev = j3Salto;
        }

        boolean reiniciar = GLFW.glfwGetKey(ventana, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
        if (reiniciar && !reiniciarPrev && estado == 2) {
            estado = 0;
            reiniciarJuego();
        }
        reiniciarPrev = reiniciar;
    }

    private void actualizar(float dt) {
        if (estado != 1)
            return;

        if (modoVictoria) {
            jugador1.velocidadY = 2.0f;
            jugador1.y += jugador1.velocidadY * dt;
            jugador2.velocidadY = 2.0f;
            jugador2.y += jugador2.velocidadY * dt;
            jugador3.velocidadY = 2.0f;
            jugador3.y += jugador3.velocidadY * dt;

            if (jugador1.y > 1.2f && jugador2.y > 1.2f && jugador3.y > 1.2f) {
                estado = 2;
            }
            return;
        }

        float gravedad = -2.2f;
        float velMaximaCaida = -2.0f;
        jugador1.actualizar(dt, gravedad, velMaximaCaida);
        if (modoJugadores >= 2)
            jugador2.actualizar(dt, gravedad, velMaximaCaida);
        if (modoJugadores >= 3)
            jugador3.actualizar(dt, gravedad, velMaximaCaida);

        verificarLimitesPantalla(jugador1);
        if (modoJugadores >= 2)
            verificarLimitesPantalla(jugador2);
        if (modoJugadores >= 3)
            verificarLimitesPantalla(jugador3);

        int puntajeMax = Math.max(jugador1.puntaje, Math.max(jugador2.puntaje, jugador3.puntaje));

        if (puntajeMax >= META_PUNTAJE) {
            modoVictoria = true;
            GestorSonido.reproducirSonido("punto.wav");
            return;
        }

        velocidadActualTuberias = 0.60f + (puntajeMax * 0.10f);
        intervaloAparicionActual = 1.8f - (puntajeMax * 0.08f);

        temporizadorTuberias += dt;
        if (temporizadorTuberias >= intervaloAparicionActual) {
            temporizadorTuberias = 0;
            generarTuberia();
        }

        Iterator<Tuberia> it = tuberias.iterator();
        while (it.hasNext()) {
            Tuberia t = it.next();
            t.x -= velocidadActualTuberias * dt;

            verificarColisionConTuberia(jugador1, t);
            if (modoJugadores >= 2)
                verificarColisionConTuberia(jugador2, t);
            if (modoJugadores >= 3)
                verificarColisionConTuberia(jugador3, t);

            if (!jugador1.estaMuerto && !t.puntuadaP1 && t.obtenerDerecha() < jugador1.x) {
                jugador1.puntaje++;
                t.puntuadaP1 = true;
                GestorSonido.reproducirSonido("punto.wav");
                actualizarTitulo();
            }
            if (modoJugadores >= 2 && !jugador2.estaMuerto && !t.puntuadaP2 && t.obtenerDerecha() < jugador2.x) {
                jugador2.puntaje++;
                t.puntuadaP2 = true;
                GestorSonido.reproducirSonido("punto.wav");
                actualizarTitulo();
            }
            if (modoJugadores >= 3 && !jugador3.estaMuerto && !t.puntuadaP3 && t.obtenerDerecha() < jugador3.x) {
                jugador3.puntaje++;
                t.puntuadaP3 = true;
                GestorSonido.reproducirSonido("punto.wav");
                actualizarTitulo();
            }

            if (t.obtenerDerecha() < -1.2f)
                it.remove();
        }

        boolean todosMuertos = jugador1.estaMuerto && (modoJugadores < 2 || jugador2.estaMuerto)
                && (modoJugadores < 3 || jugador3.estaMuerto);
        if (todosMuertos) {
            GestorSonido.reproducirSonido("muerte.wav");
            estado = 2;
            actualizarTitulo();
        }
    }

    private void verificarLimitesPantalla(Pajaro p) {
        if (p.estaMuerto)
            return;
        if (p.obtenerArriba() > 1.0f || p.obtenerAbajo() < -1.0f) {
            p.estaMuerto = true;
            p.velocidadY = 0;
        }
    }

    private void verificarColisionConTuberia(Pajaro p, Tuberia t) {
        if (p.estaMuerto)
            return;
        boolean solapamientoX = p.obtenerDerecha() > t.obtenerIzquierda() && p.obtenerIzquierda() < t.obtenerDerecha();
        if (solapamientoX) {
            if (p.obtenerArriba() > t.obtenerArribaHueco() || p.obtenerAbajo() < t.obtenerAbajoHueco()) {
                p.estaMuerto = true;
                p.velocidadY = 0;
            }
        }
    }

    private void generarTuberia() {
        float centroHueco = -0.4f + aleatorio.nextFloat() * 0.8f;
        tuberias.add(new Tuberia(1.2f, centroHueco));
    }

    private void renderizar() {
        GL11.glClearColor(0.2f, 0.6f, 0.9f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        shader.usar();
        dibujarFondo();

        for (Tuberia t : tuberias) {
            float altoSup = 1.0f - t.obtenerArribaHueco();
            float yCentroSup = t.obtenerArribaHueco() + altoSup / 2;
            renderizador.dibujarCuadrado(shader, t.x, yCentroSup, Tuberia.ANCHO, altoSup, 0, 0.2f, 0.8f, 0.2f, 1.0f);
            float altoInf = t.obtenerAbajoHueco() + 1.0f;
            float yCentroInf = -1.0f + altoInf / 2;
            renderizador.dibujarCuadrado(shader, t.x, yCentroInf, Tuberia.ANCHO, altoInf, 0, 0.2f, 0.8f, 0.2f, 1.0f);
            renderizador.dibujarCuadrado(shader, t.x, t.obtenerArribaHueco() + 0.02f, Tuberia.ANCHO + 0.04f, 0.05f, 0,
                    0.1f, 0.6f, 0.1f, 1.0f);
            renderizador.dibujarCuadrado(shader, t.x, t.obtenerAbajoHueco() - 0.02f, Tuberia.ANCHO + 0.04f, 0.05f, 0,
                    0.1f, 0.6f, 0.1f, 1.0f);
        }

        jugador1.renderizar(renderizador, shader, 1.0f, 0.9f, 0.0f);
        if (modoJugadores >= 2)
            jugador2.renderizar(renderizador, shader, 1.0f, 0.2f, 0.2f);
        if (modoJugadores >= 3)
            jugador3.renderizar(renderizador, shader, 0.2f, 0.8f, 0.2f);

        if (estado == 0) {
            renderizador.dibujarCuadrado(shader, 0, 0, 2.0f, 2.0f, 0, 0.0f, 0.0f, 0.0f, 0.7f);
            dibujarBotonMenu(-0.6f, 1, 1.0f, 0.9f, 0.0f);
            dibujarBotonMenu(0.0f, 2, 1.0f, 0.2f, 0.2f);
            dibujarBotonMenu(0.6f, 3, 0.2f, 0.8f, 0.2f);
        }

        if (estado == 2) {
            renderizador.dibujarCuadrado(shader, 0, 0, 2.0f, 2.0f, 0, modoVictoria ? 0.2f : 0.5f,
                    modoVictoria ? 0.6f : 0.0f, 0.0f, 0.6f);
            renderizador.dibujarCuadrado(shader, 0, 0, 1.0f, 0.5f, 0, 1.0f, 1.0f, 1.0f, 1.0f);
            renderizador.dibujarCuadrado(shader, 0, 0, 0.95f, 0.45f, 0, 0.1f, 0.1f, 0.15f, 1.0f);

            float s = 0.04f;
            if (modoVictoria) {
                // Dibujar "META"
                dibujarLetra('M', -0.15f, 0.1f, s);
                dibujarLetra('E', -0.05f, 0.1f, s);
                dibujarLetra('T', 0.05f, 0.1f, s);
                dibujarLetra('A', 0.15f, 0.1f, s);
            } else {
                // Dibujar "GAME OVER"
                dibujarLetra('G', -0.35f, 0.1f, s);
                dibujarLetra('A', -0.25f, 0.1f, s);
                dibujarLetra('M', -0.15f, 0.1f, s);
                dibujarLetra('E', -0.05f, 0.1f, s);
                dibujarLetra('O', 0.10f, 0.1f, s);
                dibujarLetra('V', 0.20f, 0.1f, s);
                dibujarLetra('E', 0.30f, 0.1f, s);
                dibujarLetra('R', 0.40f, 0.1f, s);
            }
        }
    }

    private void dibujarBotonMenu(float x, int num, float r, float g, float b) {
        renderizador.dibujarCuadrado(shader, x, 0, 0.35f, 0.35f, 0, 1.0f, 1.0f, 1.0f, 1.0f);
        renderizador.dibujarCuadrado(shader, x, 0, 0.32f, 0.32f, 0, r, g, b, 1.0f);
        dibujarNumero(num, x, 0, 0.2f, 0, 0, 0, 1);
    }

    private void dibujarLetra(char letra, float x, float y, float s) {
        float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
        float grosor = s * 0.25f;
        switch (letra) {
            case 'T':
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a); // Techo
                renderizador.dibujarCuadrado(shader, x, y, grosor, s * 2, 0, r, g, b, a); // Palo
                break;
            case 'G':
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y - s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y - s / 2, grosor, s, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 4, y, s / 2, grosor, 0, r, g, b, a);
                break;
            case 'A':
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y, s, grosor, 0, r, g, b, a);
                break;
            case 'M':
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y + s / 2, grosor, s, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x - s / 4, y + s, s / 2, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 4, y + s, s / 2, grosor, 0, r, g, b, a);
                break;
            case 'E':
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y - s, s, grosor, 0, r, g, b, a);
                break;
            case 'O':
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y - s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y, grosor, s * 2, 0, r, g, b, a);
                break;
            case 'V':
                renderizador.dibujarCuadrado(shader, x - s / 2, y + s / 2, grosor, s * 1.5f, 0.2f, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y + s / 2, grosor, s * 1.5f, -0.2f, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y - s, s / 2, grosor, 0, r, g, b, a);
                break;
            case 'R':
                renderizador.dibujarCuadrado(shader, x - s / 2, y, grosor, s * 2, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y + s, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y + s / 2, grosor, s, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x, y, s, grosor, 0, r, g, b, a);
                renderizador.dibujarCuadrado(shader, x + s / 2, y - s / 2, grosor, s, 0.3f, r, g, b, a);
                break;
        }
    }

    private void dibujarNumero(int num, float x, float y, float s, float r, float g, float b, float a) {
        if (num == 1) {
            renderizador.dibujarCuadrado(shader, x, y, s * 0.2f, s, 0, r, g, b, a);
        } else if (num == 2) {
            renderizador.dibujarCuadrado(shader, x, y + s * 0.4f, s, s * 0.2f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x + s * 0.4f, y + s * 0.2f, s * 0.2f, s * 0.4f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x, y, s, s * 0.2f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x - s * 0.4f, y - s * 0.2f, s * 0.2f, s * 0.4f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x, y - s * 0.4f, s, s * 0.2f, 0, r, g, b, a);
        } else if (num == 3) {
            renderizador.dibujarCuadrado(shader, x, y + s * 0.4f, s, s * 0.2f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x + s * 0.4f, y, s * 0.2f, s, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x, y, s, s * 0.2f, 0, r, g, b, a);
            renderizador.dibujarCuadrado(shader, x, y - s * 0.4f, s, s * 0.2f, 0, r, g, b, a);
        }
    }

    private void dibujarFondo() {
        renderizador.dibujarCuadrado(shader, 0, -0.95f, 2.0f, 0.1f, 0, 0.7f, 0.4f, 0.2f, 1.0f);
        dibujarMontana(-0.7f, -0.9f, 0.5f, 0.4f);
        dibujarMontana(0.0f, -0.9f, 0.8f, 0.6f);
        dibujarMontana(0.7f, -0.9f, 0.6f, 0.5f);
        dibujarNube(-0.5f, 0.7f, 0.3f);
        dibujarNube(0.4f, 0.5f, 0.25f);
        dibujarNube(-0.8f, 0.4f, 0.2f);
    }

    private void dibujarMontana(float x, float y, float w, float h) {
        renderizador.dibujarTriangulo(shader, x, y + h / 2, w, h, 0, 0.4f, 0.5f, 0.4f, 1.0f);
    }

    private void dibujarNube(float x, float y, float tamano) {
        renderizador.dibujarCuadrado(shader, x, y, tamano, tamano * 0.6f, 0, 1.0f, 1.0f, 1.0f, 0.8f);
    }

    private void actualizarTitulo() {
        String msg = "";
        if (estado == 0)
            msg = ">>> SELECCIONA MODO: [1] [2] o [3] Jugadores <<<";
        else {
            msg = String.format("HUD -> P1: %d | P2: %d | P3: %d | META: %d | VEL: %.2f | INT: %.2f",
                    jugador1.puntaje, jugador2.puntaje, jugador3.puntaje, META_PUNTAJE,
                    velocidadActualTuberias, intervaloAparicionActual);
            if (modoVictoria)
                msg = "!!! META ALCANZADA !!! - " + msg + " - [Saliendo...]";
            else if (estado == 2)
                msg = "!!! GAME OVER !!! - " + msg + " - [Presiona R para volver]";
            else
                msg = "JUGANDO... " + msg + " - [ESPACIO / W / FLECHA ARRIBA]";
        }
        GLFW.glfwSetWindowTitle(ventana, msg);
    }

    private void limpiar() {
        shader.limpiar();
        renderizador.limpiar();
        GLFW.glfwDestroyWindow(ventana);
        GLFW.glfwTerminate();
    }
}

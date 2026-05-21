package com.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

/**
 * Clase encargada de gestionar los buffers de OpenGL y dibujar figuras básicas.
 */
public class Renderizador {
    private int vaoCuadrado, vboCuadrado;
    private int vaoTriangulo, vboTriangulo;
    private int vaoCirculo, vboCirculo;
    private int segmentosCirculo = 32;

    public Renderizador() {
        inicializarCuadrado();
        inicializarTriangulo();
        inicializarCirculo();
    }

    private void inicializarCuadrado() {
        float[] vertices = {
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f,
             0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
             0.5f,  0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f
        };
        vaoCuadrado = GL30.glGenVertexArrays();
        vboCuadrado = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoCuadrado);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboCuadrado);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
    }

    private void inicializarTriangulo() {
        float[] vertices = {
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f,
             0.0f,  0.5f, 0.0f
        };
        vaoTriangulo = GL30.glGenVertexArrays();
        vboTriangulo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoTriangulo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTriangulo);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
    }

    private void inicializarCirculo() {
        float[] vertices = new float[(segmentosCirculo + 2) * 3];
        vertices[0] = 0; vertices[1] = 0; vertices[2] = 0;
        for (int i = 0; i <= segmentosCirculo; i++) {
            double angulo = Math.PI * 2 * i / segmentosCirculo;
            vertices[(i + 1) * 3] = (float) Math.cos(angulo) * 0.5f;
            vertices[(i + 1) * 3 + 1] = (float) Math.sin(angulo) * 0.5f;
            vertices[(i + 1) * 3 + 2] = 0;
        }
        vaoCirculo = GL30.glGenVertexArrays();
        vboCirculo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoCirculo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboCirculo);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);
    }

    public void dibujarCuadrado(Shader shader, float x, float y, float ancho, float alto, float rotacion, float r, float g, float b, float a) {
        shader.establecerUniformeVec2("uDesplazamiento", x, y);
        shader.establecerUniformeVec2("uEscala", ancho, alto);
        shader.establecerUniformeFloat("uRotacion", rotacion);
        shader.establecerUniformeColor("uColor", r, g, b, a);
        GL30.glBindVertexArray(vaoCuadrado);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
    }

    public void dibujarTriangulo(Shader shader, float x, float y, float ancho, float alto, float rotacion, float r, float g, float b, float a) {
        shader.establecerUniformeVec2("uDesplazamiento", x, y);
        shader.establecerUniformeVec2("uEscala", ancho, alto);
        shader.establecerUniformeFloat("uRotacion", rotacion);
        shader.establecerUniformeColor("uColor", r, g, b, a);
        GL30.glBindVertexArray(vaoTriangulo);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
    }

    public void dibujarCirculo(Shader shader, float x, float y, float ancho, float alto, float rotacion, float r, float g, float b, float a) {
        shader.establecerUniformeVec2("uDesplazamiento", x, y);
        shader.establecerUniformeVec2("uEscala", ancho, alto);
        shader.establecerUniformeFloat("uRotacion", rotacion);
        shader.establecerUniformeColor("uColor", r, g, b, a);
        GL30.glBindVertexArray(vaoCirculo);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, segmentosCirculo + 2);
    }

    public void limpiar() {
        GL30.glDeleteVertexArrays(vaoCuadrado);
        GL15.glDeleteBuffers(vboCuadrado);
        GL30.glDeleteVertexArrays(vaoTriangulo);
        GL15.glDeleteBuffers(vboTriangulo);
        GL30.glDeleteVertexArrays(vaoCirculo);
        GL15.glDeleteBuffers(vboCirculo);
    }
}

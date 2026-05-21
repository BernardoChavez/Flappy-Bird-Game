package com.graphics;

import org.lwjgl.opengl.GL20;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

/**
 * Clase que gestiona la carga, compilación y uso de programas de sombreado (shaders).
 */
public class Shader {
    private int programa;

    public Shader(String codigoVertex, String codigoFragment) {
        int vertex = compilar(GL20.GL_VERTEX_SHADER, codigoVertex);
        int fragment = compilar(GL20.GL_FRAGMENT_SHADER, codigoFragment);

        programa = GL20.glCreateProgram();
        GL20.glAttachShader(programa, vertex);
        GL20.glAttachShader(programa, fragment);
        GL20.glLinkProgram(programa);

        if (GL20.glGetProgrami(programa, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error enlazando shader: " + GL20.glGetProgramInfoLog(programa));
        }

        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
    }

    private int compilar(int tipo, String codigo) {
        int shader = GL20.glCreateShader(tipo);
        GL20.glShaderSource(shader, codigo);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compilando shader (" + tipo + "): " + GL20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public void usar() {
        GL20.glUseProgram(programa);
    }

    public void establecerUniformeVec2(String nombre, float x, float y) {
        int loc = GL20.glGetUniformLocation(programa, nombre);
        GL20.glUniform2f(loc, x, y);
    }

    public void establecerUniformeFloat(String nombre, float valor) {
        int loc = GL20.glGetUniformLocation(programa, nombre);
        GL20.glUniform1f(loc, valor);
    }

    public void establecerUniformeColor(String nombre, float r, float g, float b, float a) {
        int loc = GL20.glGetUniformLocation(programa, nombre);
        GL20.glUniform4f(loc, r, g, b, a);
    }
    
    public void limpiar() {
        GL20.glDeleteProgram(programa);
    }
}

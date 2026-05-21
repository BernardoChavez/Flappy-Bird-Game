# Flappy Bird OpenGL - Proyecto Primer Parcial

## Integrantes
- [Tu Nombre Aquí]

## Controles
### Jugador 1 (Amarillo)
- **Espacio**: Saltar / Empezar juego.

### Jugador 2 (Rojo)
- **W** o **Flecha Arriba**: Saltar / Empezar juego.

### General
- **R**: Reiniciar partida (cuando ambos mueren).
- **ESC**: Salir del juego.

## Instrucciones de Compilación y Ejecución
Este proyecto utiliza Maven. Asegúrate de tener instalado Java 17+ y Maven.

1. Abre una terminal en la carpeta del proyecto.
2. Compila el proyecto:
   ```bash
   mvn compile
   ```
3. Ejecuta el juego:
   ```bash
   mvn exec:exec
   ```

## Descripción de los Cambios Realizados (Defensa del Código)

### 1. Pájaro Geométrico Complejo (`Pajaro.java`)
Se reemplazó el rectángulo básico por un personaje compuesto por múltiples primitivas:
- **Cuerpo**: Círculo/elipse amarilla (`renderizador.dibujarCirculo`).
- **Pico**: Triángulo naranja (`renderizador.dibujarTriangulo`).
- **Ala**: Cuadrilátero blanco con animación de aleteo basada en `Math.sin(temporizadorAlas)`.
- **Cola**: Triángulo decorativo.
- **Ojo**: Círculo blanco con pupila negra.
- **Dinámica**: Se implementó rotación basada en la velocidad vertical (`velocidadY`), permitiendo que el pájaro se incline al subir o bajar.

### 2. Modo Dos Jugadores (`Juego.java`)
El sistema soporta dos jugadores independientes simultáneos:
- Cada uno tiene su propio objeto de la clase `Pajaro`.
- Controles independientes: Espacio (P1) y W (P2).
- El estado de la partida se mantiene mientras al menos un jugador siga vivo.
- El juego termina solo cuando ambos han colisionado.

### 3. Incremento Progresivo de Dificultad
La dificultad escala dinámicamente en función del puntaje máximo alcanzado:
- Se modifica la variable `velocidadActualTuberias` y `intervaloAparicionActual`.
- Esto aumenta la velocidad de desplazamiento y la frecuencia de las tuberías.
- Los cambios se reflejan en tiempo real en la interfaz (título de la ventana).

### 4. Mejora de la Interfaz y Estética
- **Fondo**: Dibujado con primitivas para incluir montañas, nubes y suelo.
- **Gráficos**: Uso de OpenGL 3.3 Core Profile con shaders personalizados y matrices de transformación (JOML).
- **HUD**: Visualización de puntajes y estados en el título.
- **Sonido**: Integración de `GestorSonido` para efectos de salto, puntos y muerte.

## Estructura del Código (Nombres en Español)
- `Principal.java`: Punto de entrada que lanza la aplicación.
- `Juego.java`: Controlador principal de la lógica, física y bucle de juego.
- `Pajaro.java`: Lógica de movimiento, colisión y dibujo del personaje.
- `Tuberia.java`: Modelo de los obstáculos.
- `Renderizador.java`: Gestiona los VAO/VBO de OpenGL para dibujar figuras.
- `Shader.java`: Clase de utilidad para cargar y compilar programas GLSL.
- `GestorSonido.java`: Sistema simple para reproducir archivos .wav.

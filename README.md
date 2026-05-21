# Flappy Bird OpenGL - Proyecto de Videojuego Multijugador

Un juego de Flappy Bird en 3D/2D construido sobre **OpenGL 3.3 Core Profile** usando **LWJGL** (Lightweight Java Game Library) y **Java**. El juego ha sido expandido para soportar partidas de hasta 3 jugadores simultáneos en una misma pantalla, un sistema dinámico de dificultad, una interfaz HUD en el título y un objetivo/meta de puntaje configurable.

---

## 🎮 Controles del Juego

### 🖥️ Menú Principal (Selección de Modo)
Al iniciar o después de un Game Over, selecciona la cantidad de jugadores activos:
* Presiona **`1`**: Modo 1 Jugador
* Presiona **`2`**: Modo 2 Jugadores
* Presiona **`3`**: Modo 3 Jugadores

### 🕹️ Controles de Vuelo
Cada jugador tiene asignada una tecla exclusiva para aletear/saltar:

* 🐤 **Jugador 1 (Amarillo)**: Tecla **`W`**
* 🔴 **Jugador 2 (Rojo)**: Tecla **`Espacio`** (Spacebar)
* 🟢 **Jugador 3 (Verde)**: Tecla **`Flecha Arriba`** (Up Arrow)

### ⚙️ Teclas Generales
* **`R`**: Regresa al menú principal (solo disponible en pantalla de Game Over / Victoria).
* **`ESC`**: Cierra y sale del juego.

---

## 🏁 Sistema de Meta y Puntaje

* **Meta Configurable**: El juego cuenta con una meta de puntos para ganar la partida. Por defecto está configurada en **10 puntos**.
* **Cómo editarla**: Puedes cambiar el límite modificando la variable `META_PUNTAJE` en el archivo `Juego.java`:
  ```java
  private static int META_PUNTAJE = 10; // Cambia este valor al límite deseado
  ```
* **Pantalla de Victoria**: Si algún jugador alcanza la meta de puntaje, el juego entra en modo Victoria: los pájaros celebran volando hacia arriba y aparece el mensaje **"META ALCANZADA"** en la pantalla.

---

## 🛠️ Instrucciones de Compilación y Ejecución

Este proyecto está gestionado por **Maven**. Asegúrate de tener instalado Java 17 o superior y Maven en tu sistema.

1. Abre una terminal en la carpeta raíz del proyecto.
2. Compila el código:
   ```bash
   mvn compile
   ```
3. Ejecuta la aplicación:
   ```bash
   mvn exec:exec
   ```

---

## 🚀 Características y Defensa del Código

### 1. Renderizado Geométrico Avanzado (`Pajaro.java`)
Se diseñó un personaje articulado con múltiples formas geométricas en lugar de un sprite estático:
* **Cuerpo principal**: Elipse/círculo con variación cromática por jugador.
* **Pico**: Triángulo naranja.
* **Ala**: Cuadrilátero que realiza una animación física de aleteo sincronizada con un temporizador `Math.sin(temporizadorAlas)`.
* **Cola**: Triángulo decorativo posterior.
* **Ojo y pupila**: Círculos concéntricos para mayor expresividad.
* **Rotación Dinámica**: El personaje se inclina físicamente hacia arriba al saltar y hacia abajo al caer, calculado dinámicamente según la velocidad vertical (`velocidadY`).

### 2. Modo Multijugador Cooperativo/Competitivo (`Juego.java`)
* Soporta 1, 2 o 3 jugadores independientes en tiempo real.
* Cada jugador posee físicas, colisiones y puntajes independientes.
* La partida se mantiene en curso mientras quede **al menos un jugador con vida**. El juego solo termina en Game Over si todos los jugadores activos colisionan.

### 3. Incremento Progresivo de Dificultad
* A medida que sube el puntaje máximo del grupo, la velocidad de avance de las tuberías aumenta (`velocidadActualTuberias`) y la frecuencia con la que aparecen se acelera (`intervaloAparicionActual`).
* Los valores de velocidad e intervalo se muestran dinámicamente en el HUD.

### 4. Estética y HUD Integrado
* **Fondo dinámico**: Renderizado de montañas en perspectiva, nubes translúcidas y suelo.
* **HUD en Ventana**: Muestra los puntajes individuales, la meta y el estado de la partida directamente en la barra de título de la ventana.
* **Efectos de Sonido (`GestorSonido.java`)**: Carga y reproducción nativa de sonidos `.wav` para saltos, anotación de puntos, choques y victorias.

---

## 📁 Estructura del Proyecto

* `Principal.java`: Clase de entrada (`main`) que inicializa y arranca el ciclo del juego.
* `Juego.java`: Motor principal. Controla el gameloop, el estado del teclado, actualización física de las entidades y renderizado.
* `Pajaro.java`: Define las propiedades físicas, colisiones y composición geométrica de cada pájaro.
* `Tuberia.java`: Define las dimensiones y el comportamiento de las tuberías/obstáculos.
* `Renderizador.java`: Abstracción de OpenGL 3.3. Dibuja primitivas (cuadrados, triángulos, elipses) enviando datos a la GPU (VAO, VBO).
* `Shader.java`: Compilador y cargador de programas Shader en GLSL.
* `GestorSonido.java`: Sistema simplificado de reproducción de audio.

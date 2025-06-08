package triviaucab.tui.models;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Clase que representa un turno en el juego.
 * Cada turno se asocia a un jugador específico, y maneja la lógica del avance del jugador
 * y la interacción con las preguntas del juego.
 */
public class Turno implements Callable<Void> {
    /**
     * Lista de jugadores que participan en el juego.
     */
    ArrayList<Ficha> jugadores;

    /**
     * Objeto Scanner para capturar las entradas del usuario durante el turno.
     */
    Scanner scanner;

    /**
     * Objeto que contiene las preguntas del juego.
     */
    Questions questions;

    /**
     * Índice del jugador cuyo turno se está procesando.
     */
    int jugadorActual;

    /**
     * Ejecuta el turno del jugador actual.
     * Este método maneja la lógica de avanzar al siguiente paso del jugador
     * y muestra su nueva posición. Si el hilo se interrumpe, se informa que el turno fue cancelado.
     *
     * @return null (se usa Void como tipo de retorno en Callable)
     * @throws Exception Si ocurre algún error durante el avance del jugador.
     */
    @Override
    public Void call() throws Exception {
        // Limpiar pantalla
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // Mostrar el nombre del jugador y su turno
        System.out.println("Turno del jugador: " + jugadores.get(jugadorActual).nickName);

        // Verificar si el hilo ha sido interrumpido
        if (Thread.currentThread().isInterrupted()) {
            System.out.println("Turno cancelado antes de completarse.");
            return null;
        }

        // El jugador avanza en su turno
        jugadores.get(jugadorActual).avanzar(scanner, questions);

        // Mostrar la posición actual del jugador
        System.out.println("Posición actual:\n" + jugadores.get(jugadorActual).posicion.paint());

        return null;
    }

    /**
     * Constructor de la clase Turno.
     * Inicializa el objeto Turno con la lista de jugadores, el objeto Scanner,
     * el conjunto de preguntas y el índice del jugador que está tomando el turno.
     *
     * @param jugadores Lista de jugadores del juego.
     * @param scanner Objeto Scanner para capturar las respuestas del jugador.
     * @param questions Conjunto de preguntas que se usan durante el juego.
     * @param jugadorActual Índice del jugador que tiene el turno actual.
     */
    public Turno(ArrayList<Ficha> jugadores, Scanner scanner, Questions questions, int jugadorActual) {
        this.jugadores = jugadores;
        this.scanner = scanner;
        this.questions = questions;
        this.jugadorActual = jugadorActual;
    }
}

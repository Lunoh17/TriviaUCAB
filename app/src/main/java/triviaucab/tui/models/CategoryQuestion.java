package triviaucab.tui.models;

import java.util.Scanner;

/**
 * La interfaz {@code CategoryQuestion} define el comportamiento esperado para las casillas
 * de categoría que contienen preguntas dentro del tablero del juego.
 * Implementaciones de esta interfaz deben proporcionar la lógica para reaccionar a una
 * pregunta y verificar si la respuesta del jugador es correcta.
 */
public interface CategoryQuestion {

    /**
     * Ejecuta la lógica de reacción cuando un jugador cae en una casilla de categoría.
     *
     * @param scanner   Scanner para capturar la entrada del usuario.
     * @param jugador   La ficha del jugador actual.
     * @param questions El conjunto de preguntas disponibles para la categoría.
     * @return La {@link Square} a la que el jugador se moverá después de responder.
     */
    public Square reaction(Scanner scanner, Ficha jugador, Questions questions);

    /**
     * Verifica si la respuesta del jugador a una pregunta es correcta.
     *
     * @param scanner  Scanner para capturar la respuesta del usuario.
     * @param question La pregunta que se le presenta al jugador.
     * @return {@code true} si la respuesta es correcta, de lo contrario {@code false}.
     */
    public boolean revisarRespuesta(Scanner scanner, Question question);
}

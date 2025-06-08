package triviaucab.tui.models;

/**
 * Interfaz que representa el comportamiento de movimiento bidireccional
 * para una ficha en el tablero de juego.
 */
public interface movimientoBidireccional {

    /**
     * Obtiene la siguiente casilla en la secuencia del tablero.
     *
     * @return la siguiente casilla {@link Square}.
     */
    public Square getNext();

    /**
     * Obtiene la casilla anterior en la secuencia del tablero.
     *
     * @return la casilla anterior {@link Square}.
     */
    public Square getPrevious();

    /**
     * Realiza el movimiento de la ficha en el tablero.
     *
     * @param move número de espacios que se debe mover.
     * @param exit posición de salida o dirección del movimiento.
     * @param jugador la ficha que se está moviendo.
     * @return la nueva casilla en la que se encuentra la ficha.
     */
    public Square movimiento(int move, int exit, Ficha jugador);
}

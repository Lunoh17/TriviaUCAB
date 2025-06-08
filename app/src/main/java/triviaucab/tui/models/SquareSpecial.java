package triviaucab.tui.models;

import java.util.Scanner;

/**
 * Representa una casilla especial del tablero que permite movimiento bidireccional.
 * Puede redirigir al jugador hacia adelante o atrás dependiendo de su decisión.
 */
public class SquareSpecial extends Square implements movimientoBidireccional {

    protected SquareCategory next;
    protected SquareCategory previous;

    /**
     * Constructor de la clase SquareSpecial.
     *
     * @param next     Casilla siguiente.
     * @param previous Casilla anterior.
     * @param position Posición de esta casilla en el tablero.
     */
    public SquareSpecial(SquareCategory next, SquareCategory previous, int position) {
        this.position = position;
        this.next = next;
        this.previous = previous;
    }

    /**
     * Establece la casilla siguiente.
     *
     * @param next Nueva casilla siguiente.
     */
    public void setNext(SquareCategory next) {
        this.next = next;
    }

    /**
     * Devuelve la casilla siguiente.
     *
     * @return Casilla siguiente.
     */
    @Override
    public SquareCategory getNext() {
        return next;
    }

    /**
     * Devuelve la casilla anterior.
     *
     * @return Casilla anterior.
     */
    @Override
    public SquareCategory getPrevious() {
        return previous;
    }

    /**
     * Movimiento del jugador desde esta casilla, considerando dirección y cantidad de pasos.
     *
     * @param move    Cantidad de pasos a mover.
     * @param exit    Dirección del movimiento (0: atrás, 1: adelante).
     * @param jugador Ficha del jugador.
     * @return Casilla destino tras el movimiento.
     */
    public Square movimiento(int move, int exit, Ficha jugador) {
        this.cantidadFichas--;
        Square iter = this;
        for (int i = 0; i < move; i++) {
            if (exit == 1 && iter instanceof movimientoBidireccional next)
                iter = next.getNext();
            else if (exit == 0 && iter instanceof movimientoBidireccional prev)
                iter = prev.getPrevious();
        }
        return iter;
    }

    /**
     * Devuelve la siguiente casilla como avance directo (sin lógica adicional).
     *
     * @return Casilla siguiente.
     */
    public SquareCategory advance() {
        return next;
    }

    /**
     * Dibuja visualmente la casilla.
     *
     * @return Representación visual de la casilla.
     */
    @Override
    public String paint() {
        if (cantidadFichas > 0) {
            return "┌────┐\n" +
                    "│S " + cantidadFichas + " │\n" +
                    "└────┘";
        }
        return "┌────┐\n" +
                "│ S  │\n" +
                "└────┘";
    }

    /**
     * Solicita al jugador que seleccione una dirección de movimiento.
     *
     * @param scanner Scanner para entrada del jugador.
     * @param jugador Ficha del jugador.
     * @return Dirección elegida (0: atrás, 1: adelante).
     */
    @Override
    public int action(Scanner scanner, Ficha jugador) {
        int a;
        do {
            a = Validator.validarInt(
                    "Tienes 2 posibles rutas, ¿a dónde te quieres mover?\n" +
                            "0. Atrás\n1. Adelante", scanner);
            if (a < 0 || a > 1) {
                System.out.println("ERROR: Vuelve a intentarlo.");
            }
        } while (a < 0 || a > 1);
        return a;
    }

    /**
     * Reacción del jugador al caer en esta casilla:
     * lanza un dado y se mueve según la dirección seleccionada.
     *
     * @param scanner Scanner para entrada del jugador.
     * @param jugador Ficha del jugador.
     * @return Nueva casilla luego del movimiento.
     */
    @Override
    public Square reaction(Scanner scanner, Ficha jugador) {
        int dado = (int) (Math.random() * 6) + 1; // Número aleatorio entre 1 y 6
        System.out.println("Tirando el dado... " + dado);
        int direction = action(scanner, jugador);
        return movimiento(dado, direction, jugador);
    }
}

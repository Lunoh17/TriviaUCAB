package triviaucab.tui.models;

import java.util.Scanner;

/**
 * La interfaz {@code brazo} representa una estructura que define la lógica de entrada y salida
 * de un jugador en un "brazo" del tablero del juego.
 * Implementaciones de esta interfaz deben definir cómo un jugador entra y sale del brazo
 * según su movimiento.
 */
public interface brazo {

    /**
     * Lógica para salir del brazo hacia una posición del tablero.
     *
     * @param move    Número de casillas que debe moverse el jugador.
     * @param exit    Número o índice de salida correspondiente (usualmente para decidir hacia qué brazo o casilla se dirige).
     * @param jugador Instancia del jugador que realiza el movimiento.
     * @param scanner Scanner para capturar interacción del usuario si es necesaria.
     * @return La {@link Square} a la que el jugador se moverá tras salir del brazo.
     */
    public Square salir(int move, int exit, Ficha jugador, Scanner scanner);

    /**
     * Lógica para entrar al brazo desde otra parte del tablero.
     *
     * @param move    Número de casillas que debe moverse el jugador.
     * @param exit    Número o índice de entrada correspondiente (usualmente para decidir desde dónde llega).
     * @param jugador Instancia del jugador que realiza el movimiento.
     * @param scanner Scanner para capturar interacción del usuario si es necesaria.
     * @return La {@link Square} a la que el jugador se moverá tras entrar al brazo.
     */
    public Square entrar(int move, int exit, Ficha jugador, Scanner scanner);
}


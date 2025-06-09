package triviaucab.tui.models;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Clase que representa una casilla de categoría en el tablero.
 * Implementa las interfaces {@link brazo}, {@link movimientoBidireccional} y {@link CategoryQuestion}.
 */
public class SquareCategory extends Square implements brazo, movimientoBidireccional, CategoryQuestion {

    /**
     * Categoría asociada a esta casilla.
     */
    protected Category categoria;

    /**
     * Casilla siguiente en el recorrido.
     */
    protected Square next;

    /**
     * Casilla anterior en el recorrido.
     */
    protected Square previous;

    /**
     * Constructor para la casilla de categoría.
     *
     * @param categoria categoría asociada a la casilla.
     * @param next      siguiente casilla.
     * @param previous  casilla anterior.
     * @param position  posición en el tablero.
     */
    public SquareCategory(Category categoria, Square next, Square previous, int position) {
        this.position = position;
        this.categoria = categoria;
        this.next = next;
        this.previous = previous;
    }

    /**
     * Dibuja la casilla en consola, mostrando si hay fichas presentes.
     *
     * @return representación en texto de la casilla.
     */
    @Override
    public String paint() {
        if (cantidadFichas > 0) {
            return "┌────┐\n" +
                    "│C " + cantidadFichas + " │\n" +
                    "└────┘";
        }
        return "┌────┐\n" +
                "│ C  │\n" +
                "└────┘";
    }

    /**
     * Permite al jugador escoger entre dos rutas posibles.
     *
     * @param scanner objeto {@link Scanner} para entrada del usuario.
     * @param jugador ficha del jugador.
     * @return la dirección seleccionada (0 o 1).
     */
    @Override
    public int action(Scanner scanner, Ficha jugador) {
        int a;
        do {
            a = Validator.validarInt(
                    "Tienes 2 posibles rutas, ¿a dónde te quieres mover?\n" +
                            "0. Atrás\n1. Adelante", scanner);
            if (a < 0 || a > 1) {
                System.out.println("ERROR, vuelva a intentarlo.");
            }
        } while (a < 0 || a > 1);
        return a;
    }

    /**
     * Lógica de salida desde esta casilla hacia otro recorrido.
     *
     * @param move     cantidad de movimientos.
     * @param exit     dirección de salida.
     * @param jugador  ficha del jugador.
     * @param scanner  entrada del usuario.
     * @return casilla destino luego del movimiento.
     */
    @Override
    public Square salir(int move, int exit, Ficha jugador, Scanner scanner) {
        Square iter = this;
        this.cantidadFichas--;
        for (int i = 0; i < move; i++) {
            if (iter instanceof SquareRayo ray) {
                int salir = ray.action(scanner, jugador);
                jugador.salido = true;
                iter = ray.salir(move - i, salir, jugador);
                i = move;
            } else if (iter instanceof SquareCategory sc)
                iter = sc.getNext();
        }
        if (iter instanceof SquareRayo) {
            jugador.salido = true;
        }
        return iter;
    }

    /**
     * Movimiento dentro del recorrido en ambas direcciones.
     *
     * @param move     número de pasos a mover.
     * @param exit     dirección de movimiento (0: previo, 1: siguiente).
     * @param jugador  ficha del jugador.
     * @return casilla destino.
     */
    @Override
    public Square movimiento(int move, int exit, Ficha jugador) {
        Square iter = this;
        this.cantidadFichas--;
        for (int i = 0; i < move; i++) {
            if (exit == 1 && iter instanceof movimientoBidireccional next)
                iter = next.getNext();
            else if (exit == 0 && iter instanceof movimientoBidireccional prev)
                iter = prev.getPrevious();
        }
        return iter;
    }

    /**
     * Reacción de la casilla al caer una ficha y plantear una pregunta al jugador.
     *
     * @param scanner   entrada del usuario.
     * @param jugador   ficha del jugador.
     * @param questions banco de preguntas.
     * @return casilla siguiente si la respuesta fue correcta, o la misma si fue incorrecta.
     */
    @Override
    public boolean reaction(Scanner scanner, Ficha jugador, Questions questions) {
        Question question = questions.getRandomQuestion(categoria);

        if (question == null) {
            System.out.println("No hay preguntas disponibles para esta categoría.");
            return false;
        }

        System.out.println("Pregunta: " + question.getQuestion());
        boolean respuestaCorrecta = false;
        try (ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor()) {

            RevisarRespuesta revisor = new RevisarRespuesta(question);
            Future<Boolean> future = null;
            try {
                future = executorService.submit(revisor);
                respuestaCorrecta = future.get(questions.getTime(), TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                future.cancel(true); // Cancelar la ejecución de la tarea
                System.out.println("Se ha acabado el tiempo de responder la pregunta, presione enter para continuar. ");
            }
        }

        if (respuestaCorrecta) {
            System.out.println("¡Respuesta correcta!");
            jugador.incrementarPuntos(categoria);
            return true;
        } else {
            System.out.println("Respuesta incorrecta.");
            return false;
        }
    }

    /**
     * Reacción neutra al llegar a la casilla (sin pregunta).
     *
     * @param scanner entrada del usuario.
     * @param jugador ficha del jugador.
     * @return esta misma casilla.
     */
    @Override
    public Square reaction(Scanner scanner, Ficha jugador) {
        return this;
    }

    /**
     * Revisa la respuesta ingresada por el jugador contra la respuesta correcta.
     *
     * @param scanner  entrada del usuario.
     * @param question pregunta a responder.
     * @return true si la respuesta es correcta, false en caso contrario.
     */
    @Override
    public boolean revisarRespuesta(Scanner scanner, Question question) {
        System.out.print("Ingrese su respuesta: ");
        String respuesta = scanner.nextLine();
        return respuesta.equalsIgnoreCase(question.getAnswer()) ||
                question.getAnswer().toLowerCase().contains(respuesta.toLowerCase()) ||
                respuesta.toLowerCase().contains(question.getAnswer().toLowerCase());
    }

    /**
     * Método para entrar desde esta casilla hacia el centro.
     *
     * @param move     número de pasos.
     * @param exit     dirección (no utilizada aquí).
     * @param jugador  ficha del jugador.
     * @param scanner  entrada del usuario.
     * @return casilla destino (centro) si llega, si no esta misma.
     */
    @Override
    public Square entrar(int move, int exit, Ficha jugador, Scanner scanner) {
        SquareCategory iter = this;
        this.cantidadFichas--;
        for (int i = 1; i <= move; i++) {
            if (iter.next instanceof SquareCenter sC) {
                if (i == move) return sC;
            }
        }
        return this;
    }

    /**
     * Obtiene la categoría asociada a la casilla.
     *
     * @return categoría de esta casilla.
     */
    public Category getCategoria() {
        return categoria;
    }

    /**
     * Obtiene la casilla anterior.
     *
     * @return casilla anterior.
     */
    public Square getPrevious() {
        return previous;
    }

    /**
     * Obtiene la casilla siguiente.
     *
     * @return casilla siguiente.
     */
    public Square getNext() {
        return next;
    }
}

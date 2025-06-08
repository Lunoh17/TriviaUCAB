package triviaucab.tui.models;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Clase que representa la casilla central del tablero en el juego.
 * Desde esta casilla se puede salir hacia cualquiera de los seis brazos (rayos) que
 * conforman el tablero.
 * <p>
 * Implementa las interfaces {@link brazo} y {@link CategoryQuestion}.
 */
public class SquareCenter extends Square implements brazo, CategoryQuestion {

    /**
     * Arreglo que contiene los 6 brazos del tablero, cada uno compuesto por varias casillas de categoría.
     */
    protected SquareCategory rayos[] = new SquareCategory[6];

    /**
     * Dibuja la casilla central en la consola.
     *
     * @return Una representación textual de la casilla central.
     */
    @Override
    public String paint() {
        if (cantidadFichas > 0) {
            return "/────\\\n" +
                    "│  " + cantidadFichas + " │\n" +
                    "\\────/";
        }
        return "/────\\\n" +
                "│    │\n" +
                "\\────/";
    }

    /**
     * Permite al jugador seleccionar una de las seis rutas posibles para salir del centro.
     *
     * @param scanner Scanner para entrada del usuario.
     * @param jugador Ficha del jugador.
     * @return Índice del rayo seleccionado (0 a 5).
     */
    @Override
    public int action(Scanner scanner, Ficha jugador) {
        int a;
        do {
            a = Validator.validarInt(
                    "Tienes 6 posibles rutas, ¿a dónde te quieres mover?\n" +
                            "0. Derecha\n" +
                            "1. Abajo a la Derecha\n" +
                            "2. Abajo a la Izquierda\n" +
                            "3. Izquierda\n" +
                            "4. Arriba a la Izquierda\n" +
                            "5. Arriba a la Derecha",
                    scanner);
            if (a < 0 || a > 5) {
                System.out.println("ERROR, vuelva a intentarlo.");
            }
        } while (a < 0 || a > 5);
        return a;
    }

    /**
     * Lógica para salir del centro hacia un brazo específico del tablero.
     *
     * @param move    Número de pasos a mover.
     * @param exit    Índice del brazo por el cual salir (0 a 5).
     * @param jugador Ficha del jugador.
     * @param scanner Scanner para entrada del usuario.
     * @return Casilla destino después del movimiento.
     */
    public Square salir(int move, int exit, Ficha jugador, Scanner scanner) {
        if (move < 1 || move > 6) {
            throw new IllegalArgumentException("El movimiento debe estar entre 1 y 6");
        }
        SquareCategory ciclar = this.rayos[exit];
        Square salida = ciclar;
        if (this.cantidadFichas > 0) {
            this.cantidadFichas--;
            for (int i = 1; i < move; i++) {
                if (ciclar.next instanceof SquareCategory sig) {
                    ciclar = sig;
                    salida = ciclar;
                } else if (ciclar.next instanceof SquareRayo sig) {
                    salida = sig;
                    jugador.salido = true;
                }
            }
        } else {
            throw new IllegalStateException("No hay cantidadFichas en el centro para salir.");
        }
        return salida;
    }

    /**
     * Constructor de la clase. Inicializa todos los rayos del tablero, conectando
     * las casillas en un patrón predeterminado.
     *
     * @param jugadores Número de jugadores para determinar cantidad inicial de fichas.
     */
    public SquareCenter(int jugadores) {
        int position = 0;
        this.position = position++;
        int currentCategory = 0;
        this.cantidadFichas = jugadores;
        var categorias = Category.values();
        SquareCategory lastSquare = null;

        for (int i = 0; i < this.rayos.length; i++) {
            this.rayos[i] = new SquareCategory(categorias[i], null, this, position++);
            SquareCategory squareActual = this.rayos[i];
            currentCategory = i + 1;
            if (categorias.length == currentCategory) currentCategory = 0;

            for (int j = 1; j < 5; j++) {
                squareActual.next = new SquareCategory(categorias[currentCategory++], null, squareActual, position++);
                if (categorias.length == currentCategory) currentCategory = 0;
                squareActual = (SquareCategory) squareActual.next;
            }

            squareActual.next = new SquareRayo(categorias[currentCategory++], null, lastSquare, squareActual, position++);
            if (lastSquare != null) lastSquare.next = squareActual.next;
            SquareRayo rayoActual = (SquareRayo) squareActual.next;

            if (categorias.length == currentCategory) currentCategory = 0;
            squareActual = rayoActual.next = new SquareCategory(categorias[currentCategory++], null, rayoActual, position++);
            if (categorias.length == currentCategory) currentCategory = 0;
            for (int j = 1; j < 6; j++) {
                if (j == 1 || j == 4) {
                    squareActual.next = new SquareSpecial(null, squareActual, position++);
                    ((SquareSpecial) squareActual.next).next = new SquareCategory(categorias[currentCategory++], null, squareActual.next, position++);
                    j++;
                    if (categorias.length == currentCategory) currentCategory = 0;
                    squareActual = ((SquareSpecial) squareActual.next).next;
                } else {
                    squareActual.next = new SquareCategory(categorias[currentCategory++], null, squareActual, position++);
                    if (categorias.length == currentCategory) currentCategory = 0;
                    squareActual = (SquareCategory) squareActual.next;
                }
            }

            lastSquare = squareActual;
        }

        // Conecta el último SquareRayo al final del recorrido
        SquareCategory iter = this.rayos[0];
        for (int i = 1; i < 5; i++) {
            iter = (SquareCategory) iter.next;
        }
        if (iter.next instanceof SquareRayo a) {
            a.previous = lastSquare;
            lastSquare.next = a;
        }
    }

    /**
     * Reacción al caer en la casilla central: permite seleccionar una categoría
     * y responder una pregunta para intentar ganar.
     *
     * @param scanner  Scanner para entrada del usuario.
     * @param jugador  Ficha del jugador.
     * @param questions Banco de preguntas.
     * @return Casilla resultante después de la interacción (esta misma).
     */
    @Override
    public Square reaction(Scanner scanner, Ficha jugador, Questions questions) {
        Category[] categorias = Category.values();
        int seleccion;
        do {
            System.out.println("Seleccione una categoría:");
            for (int i = 0; i < categorias.length; i++) {
                System.out.println(i + ": " + categorias[i]);
            }
            seleccion = Validator.validarInt("", scanner);
        } while (seleccion < 0 || seleccion > categorias.length - 1);

        Category categoria = categorias[seleccion];
        Question question = questions.getRandomQuestion(categoria);

        if (question == null) {
            System.out.println("No hay preguntas disponibles para esta categoría.");
            return this;
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
            jugador.gano = true;
            return this;
        } else {
            System.out.println("Respuesta incorrecta.");
            return this;
        }
    }

    /**
     * Reacción alternativa sin preguntas (por compatibilidad con la interfaz).
     *
     * @param scanner Scanner para entrada del usuario.
     * @param jugador Ficha del jugador.
     * @return Esta misma casilla.
     */
    @Override
    public Square reaction(Scanner scanner, Ficha jugador) {
        return this;
    }

    /**
     * Revisa si la respuesta ingresada por el jugador es correcta.
     *
     * @param scanner  Scanner para entrada del usuario.
     * @param question Pregunta a evaluar.
     * @return true si la respuesta es válida; false si es incorrecta.
     */
    @Override
    public boolean revisarRespuesta(Scanner scanner, Question question) {
        System.out.print("Ingrese su respuesta: ");
        String respuesta = scanner.nextLine();
        return respuesta.equalsIgnoreCase(question.getAnswer())
                || question.getAnswer().toLowerCase().contains(respuesta.toLowerCase())
                || respuesta.toLowerCase().contains(question.getAnswer().toLowerCase());
    }

    /**
     * Acción al entrar a la casilla central. No hay movimiento adicional desde aquí.
     *
     * @param move    Número de pasos a mover.
     * @param exit    Dirección de entrada.
     * @param jugador Ficha del jugador.
     * @param scanner Scanner para entrada del usuario.
     * @return Esta misma casilla.
     */
    @Override
    public Square entrar(int move, int exit, Ficha jugador, Scanner scanner) {
        return this;
    }
}

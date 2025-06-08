package triviaucab.tui.models;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

/**
 * Clase que administra las preguntas de trivia, clasificadas como en espera de aprobación,
 * aprobadas o rechazadas. Permite modificar, visualizar, aprobar o eliminar preguntas.
 */
public class Questions {

    /**
     * Lista de preguntas en espera de aprobación.
     */
    public ArrayList<Question> waitApproved = new ArrayList<>();

    /**
     * Lista de preguntas aprobadas.
     */
    private ArrayList<Question> approved = new ArrayList<>();

    /**
     * Lista de preguntas rechazadas.
     */
    private ArrayList<Question> rejected = new ArrayList<>();

    /**
     * Tiempo límite permitido para responder preguntas (máximo 2 minutos).
     */
    private int time;

    public ArrayList<Question> getApproved() {
        return approved;
    }

    /**
     * Solicita al usuario que establezca el tiempo límite para responder las preguntas.
     * El tiempo debe estar entre 0 y 2 minutos.
     */
    public void setTime(Scanner scanner) {
        while (time < 0 || time > 180) {
            this.time = Validator.validarInt("Ingrese el tiempo límite de las preguntas que no exceda de dos minutos 180 segundos",scanner);
            if (time < 0 || time > 180) {
                System.out.print("El tiempo que introdujo se excede del límite: ");
            }
        }
    }

    public int getTime() {
        return time;
    }

    /**
     * Agrega una pregunta a la lista de espera por aprobación.
     *
     * @param question Pregunta a agregar.
     */
    public void addWaitApproved(Question question) {
        waitApproved.add(question);
    }

    /**
     * Modifica el texto de una pregunta según su lista y posición.
     *
     * @param listNumber Número de la lista (1: espera, 2: rechazadas, 3: aprobadas).
     * @param position   Posición de la pregunta en la lista.
     * @param question   Nuevo texto de la pregunta.
     */
    public void modifyQuestion(int listNumber, int position, String question) {
        switch (listNumber) {
            case 1 -> waitApproved.get(position).setQuestion(question);
            case 3 -> approved.get(position).setQuestion(question);
            case 2 -> rejected.get(position).setQuestion(question);
        }
    }

    /**
     * Modifica la respuesta de una pregunta según su lista y posición.
     *
     * @param listNumber Número de la lista (1: espera, 2: rechazadas, 3: aprobadas).
     * @param position   Posición de la pregunta en la lista.
     * @param answer     Nueva respuesta de la pregunta.
     */
    public void modifyAnswer(int listNumber, int position, String answer) {
        switch (listNumber) {
            case 1 -> waitApproved.get(position).setAnswer(answer);
            case 3 -> approved.get(position).setAnswer(answer);
            case 2 -> rejected.get(position).setAnswer(answer);
        }
    }

    /**
     * Modifica la categoría de una pregunta según su lista y posición.
     *
     * @param listNumber Número de la lista (1: espera, 2: rechazadas, 3: aprobadas).
     * @param position   Posición de la pregunta en la lista.
     * @param category   Nueva categoría.
     */
    public void modifyCategory(int listNumber, int position, Category category) {
        switch (listNumber) {
            case 1 -> waitApproved.get(position).setCategory(category);
            case 3 -> approved.get(position).setCategory(category);
            case 2 -> rejected.get(position).setCategory(category);
        }
    }

    /**
     * Rechaza una pregunta de la lista de espera, moviéndola a la lista de rechazadas.
     *
     * @param position Posición de la pregunta a rechazar.
     */
    public void addRejeter(int position) {
        rejected.add(waitApproved.get(position));
        waitApproved.remove(position);
    }

    /**
     * Elimina una pregunta de una lista específica.
     *
     * @param position Posición de la pregunta a eliminar.
     * @param lista    Número de la lista (1: espera, 2: aprobadas, 3: rechazadas).
     */
    public void delete(int position, int lista) {
        switch (lista) {
            case 1 -> waitApproved.remove(position);
            case 2 -> approved.remove(position);
            case 3 -> rejected.remove(position);
        }
    }

    /**
     * Aprueba una pregunta si no fue creada por el usuario actual.
     *
     * @param position    Posición de la pregunta en espera.
     * @param currentUser Usuario que intenta aprobar la pregunta.
     */
    public void addApproved(int position, Usuario currentUser) {
        if (position == -1) {
            System.out.println("Hasta luego, vuelva pronto a jugar.");
        } else if (!currentUser.userName.equals(waitApproved.get(position).getCreator())) {
            waitApproved.get(position).aprovedBy = currentUser.userName;
            approved.add(waitApproved.get(position));
            waitApproved.remove(position);
        } else {
            System.out.println("No puedes aprobar tu propia pregunta.");
        }
    }

    /**
     * Devuelve la cantidad de preguntas en una lista específica.
     *
     * @param lista Número de la lista (1: espera, 2: aprobadas, 3: rechazadas).
     * @return Tamaño de la lista.
     */
    public int getSize(int lista) {
        return switch (lista) {
            case 1, 4 -> waitApproved.size();
            case 2 -> approved.size();
            case 3 -> rejected.size();
            default -> 0;
        };
    }

    /**
     * Muestra todas las preguntas en espera de aprobación que no fueron creadas por el usuario actual.
     *
     * @param usuarioActual Usuario autenticado.
     */
    public void visualAproved(Usuario usuarioActual) {
        int counter = 1;
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
        System.out.println("| #   | Pregunta                                                     | Respuesta                      | Categoria         |    Creador           |");
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
        for (Question question : waitApproved) {
            if (!question.getCreator().equals(usuarioActual.getUserName())) {
                System.out.println(question.tableFormat(counter));
            }
            counter++;
        }
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
    }

    /**
     * Muestra las preguntas de una lista específica que el usuario puede ver.
     *
     * @param numberList  Número de la lista (1: espera, 2: aprobadas, 3: rechazadas).
     * @param currentUser Usuario autenticado.
     */
    public void visualQuestions(int numberList, Usuario currentUser) {
        ArrayList<Question> questions = switch (numberList) {
            case 1 -> waitApproved;
            case 2 -> approved;
            case 3 -> rejected;
            default -> waitApproved;
        };
        int counter = 1;
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
        System.out.println("| #   | Pregunta                                                     | Respuesta                      | Categoria         |    Creador           |");
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
        for (Question question : questions) {
            if (numberList != 2) {
                System.out.println(question.tableFormat(counter));
            } else if (currentUser.getUserName().equals(question.aprovedBy) ||
                    currentUser.getUserName().equals(question.getCreator())) {
                System.out.println(question.tableFormat(counter));
            }
            counter++;
        }
        System.out.println("+-----+--------------------------------------------------------------+--------------------------------+-------------------+----------------------+");
    }

    /**
     * Devuelve una pregunta aleatoria de una categoría específica entre las preguntas aprobadas.
     *
     * @param category La categoría de la pregunta.
     * @return Una pregunta aleatoria de esa categoría o null si no hay ninguna disponible.
     */
    // NUEVO MÉTODO: Obtener una pregunta aleatoria por categoría
    public Question getRandomQuestion(Category category) {
        ArrayList<Question> filtradas = new ArrayList<>();
        for (Question q : approved) {
            if (q.getCategory() == category) {
                filtradas.add(q);
            }
        }
        if (filtradas.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return filtradas.get(rand.nextInt(filtradas.size()));
    }


}

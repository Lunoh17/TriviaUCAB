package triviaucab.tui.models;

/**
 * Clase que representa una pregunta de trivia, con su respuesta, categoría,
 * creador y usuario que la aprobó.
 */
public class Question {

    /** Texto de la pregunta. */
    private String question;

    /** Respuesta correcta a la pregunta. */
    private String answer;

    /** Nombre del usuario que creó la pregunta (no modificable). */
    final String creator;

    /** Nombre del usuario que aprobó la pregunta. */
    public String aprovedBy;

    /** Categoría de la pregunta. */
    private Category category;

    /**
     * Constructor para inicializar una pregunta.
     *
     * @param question Texto de la pregunta.
     * @param answer Respuesta correcta.
     * @param creator Nombre del usuario creador.
     * @param category Categoría asignada.
     */
    public Question(String question, String answer, String creator, Category category) {
        this.question = question;
        this.answer = answer;
        this.creator = creator;
        this.category = category;
    }

    /**
     * Obtiene el texto de la pregunta.
     *
     * @return Pregunta como cadena de texto.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Establece el texto de la pregunta (solo para preguntas en lista de espera).
     *
     * @param question Nueva pregunta.
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Obtiene la respuesta de la pregunta.
     *
     * @return Respuesta como cadena de texto.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Establece la respuesta de la pregunta (solo para preguntas en lista de espera).
     *
     * @param answer Nueva respuesta.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Obtiene la categoría asignada a la pregunta.
     *
     * @return Categoría de la pregunta.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Establece la categoría de la pregunta.
     *
     * @param category Nueva categoría.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Obtiene el nombre del usuario creador de la pregunta.
     *
     * @return Nombre del creador.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Representación textual del objeto `Question`.
     *
     * @return Cadena descriptiva de la pregunta.
     */
    @Override
    public String toString() {
        return "Question{" +
                "creator='" + creator + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", category=" + category +
                '}';
    }

    /**
     * Devuelve la representación en formato de tabla de la pregunta.
     *
     * @param row Número de fila para mostrar en la tabla.
     * @return Cadena con formato de tabla.
     */
    public String tableFormat(int row) {
        if (question.length() > 60) {
            return String.format("| %-3d | %-60s | %-30s | %-18s| %-20s | \n" +
                            "|     | %-60s | " + " ".repeat(30) + " | " + " ".repeat(18) + " | " + " ".repeat(20) + "|",
                    row, this.question.substring(0,60), this.answer, this.category, this.creator, this.question.substring(question.length() - 60));
        }
        return String.format("| %-3d | %-60s | %-30s | %-18s| %-20s |", row, this.question, this.answer, this.category, this.creator);
    }
}

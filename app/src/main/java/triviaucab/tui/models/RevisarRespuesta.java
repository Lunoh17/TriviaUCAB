package triviaucab.tui.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class RevisarRespuesta implements Callable<Boolean> {
    Question question;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    @Override
    public Boolean call() throws Exception {
        System.out.print("Ingrese su respuesta: ");
        String respuesta = reader.readLine();
        return respuesta.equalsIgnoreCase(question.getAnswer()) ||
                question.getAnswer().toLowerCase().contains(respuesta.toLowerCase()) ||
                respuesta.toLowerCase().contains(question.getAnswer().toLowerCase());
    }

    public RevisarRespuesta(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
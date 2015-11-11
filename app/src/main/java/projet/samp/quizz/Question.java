package projet.samp.quizz;

import java.util.ArrayList;

public class Question {
    int indiceReponce;
    ArrayList<Proposition> propositionsList;
    String question;

    public Question(String question) {
        this.question = null;
        this.indiceReponce = 0;
        this.propositionsList = new ArrayList<>();
        this.question = question;
    }

    public String getQuestion() {
        return this.question;
    }

    public int getIndiceReponce() {
        return this.indiceReponce;
    }

    public void setIndiceReponce(int indiceReponce) {
        this.indiceReponce = indiceReponce;
    }

}

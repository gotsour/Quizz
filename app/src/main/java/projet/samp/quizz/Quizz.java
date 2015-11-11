package projet.samp.quizz;

import java.util.ArrayList;

public class Quizz {
    public ArrayList<Question> questionList;
    String quizzName;

    public String getQuizzName() {
        return this.quizzName;
    }

    public Quizz(String quizzName) {
        questionList = new ArrayList<>();
        this.quizzName = quizzName;
    }

}

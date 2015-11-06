package projet.samp.quizz;

import java.util.ArrayList;

public class Quizz {
    ArrayList<Question> questionList;
    String quizzName;

    public String getQuizzName() {
        return this.quizzName;
    }

    public Quizz(String quizzName) {
        this.quizzName = null;
        this.quizzName = quizzName;
    }

    public void setQuizzName(String quizzName) {
    }

    public ArrayList<Question> getQuestionList() {
        return this.questionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }
}

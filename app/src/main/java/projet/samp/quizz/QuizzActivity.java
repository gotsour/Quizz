package projet.samp.quizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twesterm on 08/10/15.
 */
public class QuizzActivity extends MainActivity {

    List<String> mesQuestions = new ArrayList<String>();
    List<String> mesReponses = new ArrayList<String>();
    QuestionDataBase questionDB ;
    int indiceReponse;

    TextView question;
    TextView score;
    int indiceQuestion;
    boolean estAlleVoirReponse;
    LinearLayout layoutProposition;
    LinearLayout layoutQuestion;
    LinearLayout layoutButton;
    Button buttonNext;
    Button buttonVoirReponse;
    int quizzNumber;


    private int scoreJeu = 0;

    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        quizzNumber = intent.getIntExtra("quizzNumber", 0);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
        indiceQuestion = settings.getInt("myIndice", 0);
        setContentView(R.layout.quizz);

        /* On sélectionne les questions et les réponses qui correspondent au quizz passé en paramètre (quizzNumber) */
        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuestions(mesQuestions, quizzNumber);

        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonVoirReponse = (Button) findViewById(R.id.buttonVoirReponse);
        layoutProposition = (LinearLayout) findViewById(R.id.linearLayoutReponse);
        layoutButton = (LinearLayout) findViewById(R.id.linearLayoutButton);
        layoutQuestion = (LinearLayout) findViewById(R.id.linearLayoutQuestion);

        score = (TextView) findViewById(R.id.textViewScore);
        score.setText(String.valueOf(scoreJeu));

        /**
         * LIGNE JUSTE EN DESSOUS A ENLEVER PLUS TARD
         */
        indiceQuestion = 1;
        joue(indiceQuestion - 1);

        buttonNext.setOnClickListener(myhandlerButtonNext);
        buttonVoirReponse.setOnClickListener(myhandlerButtonVoirReponse);

    }

    /* Méthode qui affiche les question et charge les élements */
    public void joue(int indice) {

        mesReponses = new ArrayList<String>();

        questionDB.chargerLesReponses(mesReponses, Integer.parseInt(mesQuestions.get(indice)));

        indiceReponse = questionDB.getIndiceReponse(Integer.parseInt(mesQuestions.get(indice)));
        layoutProposition.removeAllViews();

        question = (TextView) findViewById(R.id.textViewQuestion);
        question.setText(mesQuestions.get(indice+1));

        for (int i = 0 ; i < mesReponses.size() ; i++) {
            final Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setId(i);
            btnTag.setText(mesReponses.get(i));
            layoutProposition.addView(btnTag);
            btnTag.setOnClickListener(myhandler1);
        }

    }

    View.OnClickListener myhandlerButtonNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indiceQuestion+=2;
            if (indiceQuestion > mesQuestions.size()) {

                finQuizz();

            } else {
                joue(indiceQuestion-1);
            }
        }
    };

    View.OnClickListener myhandlerButtonVoirReponse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(QuizzActivity.this, ShowAnswerActivity.class);
            intent.putExtra("reponse", mesReponses.get(indiceReponse - 1));
            startActivity(intent);
            estAlleVoirReponse = true;
        }
    };

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (verifieReponse(v.getId() + 1, indiceReponse) == true) {
                if (estAlleVoirReponse) {
                    Toast.makeText(QuizzActivity.this, "VOUS AVEZ TRICHÉ !", Toast.LENGTH_SHORT).show();
                    scoreJeu--;
                    score.setText(String.valueOf(scoreJeu));
                    estAlleVoirReponse = false;
                } else {
                    Toast.makeText(QuizzActivity.this, "CORRECT !", Toast.LENGTH_SHORT).show();
                    scoreJeu+=2;
                    score.setText(String.valueOf(scoreJeu));
                }
            } else {
                Toast.makeText(QuizzActivity.this, "MAUVAISE REPONSE !", Toast.LENGTH_SHORT).show();
                scoreJeu-=2;
                score.setText(String.valueOf(scoreJeu));
            }
            indiceQuestion+=2;
            if (indiceQuestion > mesQuestions.size()) {

                finQuizz();

            } else {
                joue(indiceQuestion-1);
            }
        }
    };

    public void finQuizz() {

        layoutQuestion.removeAllViews();
        layoutButton.removeAllViews();
        layoutProposition.removeAllViews();

        final Button btnRejouer = new Button(this);
        final Button btnRetourQuizz = new Button(this);
        final TextView scoreFinQuizz = new TextView(this);

        btnRejouer.setText("Rejouer");
        btnRetourQuizz.setText("Retour aux quizzs");
        scoreFinQuizz.setText("Vous avez un score de : " + scoreJeu);

        btnRejouer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnRetourQuizz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scoreFinQuizz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        layoutProposition.addView(btnRejouer);
        layoutProposition.addView(btnRetourQuizz);
        layoutProposition.addView(scoreFinQuizz);

        btnRejouer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                intent.putExtra("quizzNumber", quizzNumber);
                startActivity(intent);
            }
        });

        btnRetourQuizz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                intent = new Intent(QuizzActivity.this, SelectQuizzActivity.class);
                intent.putExtra("STATE", "play");
                startActivity(intent);
            }
        });

    }

    private boolean verifieReponse(int idButton, int indiceReponse) {
        boolean result;
        if ( idButton == indiceReponse) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }


    @Override
    protected void onStop(){
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("myIndice", indiceQuestion);
        // Commit the edits!
        editor.commit();
    }

}

package projet.samp.quizz;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class QuizzActivity extends MainActivity {

    List<String> mesQuestions = new ArrayList<>();
    List<String> mesReponses = new ArrayList<>();
    QuestionDataBase questionDB ;
    int indiceReponse;

    TextView question;
    TextView score;
    int indiceQuestion = 0;
    boolean estAlleVoirReponse;
    TableLayout layoutProposition;
    LinearLayout layoutQuestion;
    LinearLayout layoutButton;
    LinearLayout layoutScore;
    Button buttonNext;
    Button buttonVoirReponse;
    int quizzNumber;
    boolean imageQuestion = false;


    private int scoreJeu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        quizzNumber = intent.getIntExtra("quizzNumber", 0);

        // Restore preferences
        setContentView(R.layout.quizz);

        /* On sélectionne les questions et les réponses qui correspondent au quizz passé en paramètre (quizzNumber) */
        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuestions(mesQuestions, quizzNumber);

        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonVoirReponse = (Button) findViewById(R.id.buttonVoirReponse);
        layoutProposition = (TableLayout) findViewById(R.id.layoutProposition);
        layoutButton = (LinearLayout) findViewById(R.id.linearLayoutButton);
        layoutQuestion = (LinearLayout) findViewById(R.id.linearLayoutQuestion);
        layoutScore = (LinearLayout) findViewById(R.id.linearLayoutScore);

        score = (TextView) findViewById(R.id.textViewScore);
        score.setText(String.valueOf(scoreJeu));

        joue(indiceQuestion);

        buttonNext.setOnClickListener(myhandlerButtonNext);
        buttonVoirReponse.setOnClickListener(myhandlerButtonVoirReponse);

    }

    /* Méthode qui affiche les question et charge les élements */
    public void joue(int indice) {

        TableRow tableRow1 = new TableRow(this);
        TableRow tableRow2 = new TableRow(this);
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParamsImg = new TableRow.LayoutParams(300, 300);
        rowParams.setMargins(10,10,10,10);
        rowParamsImg.setMargins(10,10,10,10);

        tableRow1.setLayoutParams(tableParams);
        tableRow2.setLayoutParams(tableParams);

        mesReponses = new ArrayList<>();

        // Le try permet de lever une exception si jamais il n'y a pas de questions dans le quizz
        try {
            questionDB.chargerLesReponses(mesReponses, Integer.parseInt(mesQuestions.get(indice)));

            indiceReponse = questionDB.getIndiceReponse(Integer.parseInt(mesQuestions.get(indice)));
            layoutProposition.removeAllViews();

            question = (TextView) findViewById(R.id.textViewQuestion);
            question.setText(mesQuestions.get(indice + 1));

            for (int i = 0; i < mesReponses.size(); i++) {

                // Permet de détecter si on a une image ou pas
                if (mesReponses.get(i).startsWith("/")) {

                    imageQuestion = true;

                    final ImageButton imgBtnTag = new ImageButton(this);
                    imgBtnTag.setLayoutParams(rowParamsImg);
                    imgBtnTag.setId(i);
                    Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(mesReponses.get(i)));
                    imgBtnTag.setBackground(d);

                    if (i%2 == 0) {
                        tableRow1.addView(imgBtnTag);
                    } else {
                        tableRow2.addView(imgBtnTag);
                    }

                    imgBtnTag.setOnClickListener(myhandler1);

                } else {

                    imageQuestion = false;

                    final Button btnTag = new Button(this);
                    btnTag.setLayoutParams(rowParams);
                    btnTag.setId(i);
                    btnTag.setBackgroundColor(Color.parseColor("#0288D1"));
                    btnTag.setTextColor(Color.parseColor("#ffffff"));
                    btnTag.setText(mesReponses.get(i));

                    if (i%2 == 0) {
                        tableRow1.addView(btnTag);
                    } else {
                        tableRow2.addView(btnTag);
                    }
                    btnTag.setOnClickListener(myhandler1);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(QuizzActivity.this, "Ce quizz ne contient pas de questions !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        layoutProposition.addView(tableRow1);
        layoutProposition.addView(tableRow2);
    }

    View.OnClickListener myhandlerButtonNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indiceQuestion+=2;
            if (indiceQuestion >= mesQuestions.size()) {

                finQuizz();

            } else {
                joue(indiceQuestion);
            }
        }
    };

    View.OnClickListener myhandlerButtonVoirReponse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(QuizzActivity.this, ShowAnswerActivity.class);
                intent.putExtra("reponse", mesReponses.get(indiceReponse - 1));
                intent.putExtra("imageQuestion", imageQuestion);
                startActivity(intent);
                estAlleVoirReponse = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                Toast.makeText(QuizzActivity.this, "Ce quizz ne contient pas de questions !", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (verifieReponse(v.getId() + 1, indiceReponse)) {
                if (estAlleVoirReponse) {
                    Toast.makeText(QuizzActivity.this, "VOUS AVEZ TRICHÉ !", Toast.LENGTH_SHORT).show();
                    if (scoreJeu > 0) { scoreJeu-=1; }
                    score.setText(String.valueOf(scoreJeu));
                    estAlleVoirReponse = false;
                } else {
                    Toast.makeText(QuizzActivity.this, "CORRECT !", Toast.LENGTH_SHORT).show();
                    scoreJeu+=1;
                    score.setText(String.valueOf(scoreJeu));
                }
            } else {
                Toast.makeText(QuizzActivity.this, "MAUVAISE REPONSE !", Toast.LENGTH_SHORT).show();
            }
            indiceQuestion+=2;
            if (indiceQuestion >= mesQuestions.size()) {

                finQuizz();

            } else {
                joue(indiceQuestion);
            }
        }
    };

    public void finQuizz() {

        layoutQuestion.removeAllViews();
        layoutButton.removeAllViews();
        layoutProposition.removeAllViews();
        layoutScore.removeAllViews();

        final Button btnRejouer = new Button(this);
        final Button btnRetourQuizz = new Button(this);
        final TextView scoreFinQuizz = new TextView(this);
        scoreFinQuizz.setTextSize(50);
        scoreFinQuizz.setTextColor(Color.parseColor("#009688"));

        btnRejouer.setBackgroundColor(Color.parseColor("#0288D1"));
        btnRejouer.setTextColor(Color.parseColor("#ffffff"));

        btnRetourQuizz.setBackgroundColor(Color.parseColor("#03A9F4"));
        btnRetourQuizz.setTextColor(Color.parseColor("#ffffff"));

        btnRejouer.setText("Rejouer");
        btnRetourQuizz.setText("Retour aux quizzs");
        scoreFinQuizz.setText("Score de : " + scoreJeu + " sur " + mesQuestions.size()/2);

        btnRejouer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnRetourQuizz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scoreFinQuizz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        layoutQuestion.addView(btnRejouer);
        layoutQuestion.addView(btnRetourQuizz);
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
                Intent intent;
                finish();
                intent = new Intent(QuizzActivity.this, SelectQuizzActivity.class);
                intent.putExtra("STATE", "play");
                startActivity(intent);
            }
        });

    }

    private boolean verifieReponse(int idButton, int indiceReponse) {
        boolean result;
        result = idButton == indiceReponse;
        return result;
    }


}

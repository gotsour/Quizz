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

/**
 * Activité qui permet de jouer à un quizz
 */

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

        // On récupère l'id du quizz en question
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

        // On appele la méthode principale qui permet le jeu du quizz
        joue(indiceQuestion);

        buttonNext.setOnClickListener(myhandlerButtonNext);
        buttonVoirReponse.setOnClickListener(myhandlerButtonVoirReponse);

    }

    /* Méthode qui affiche les question et charge les élements */
    public void joue(int indice) {

        // On déclare les données qui peupleront le TableLayout
        TableRow tableRow1 = new TableRow(this);
        TableRow tableRow2 = new TableRow(this);

        TableRow tableRow3 = new TableRow(this);
        TableRow tableRow4 = new TableRow(this);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParamsImg = new TableRow.LayoutParams(450 , 450);
        rowParams.setMargins(10,10,10,10);
        rowParamsImg.setMargins(10,10,10,10);

        tableRow1.setLayoutParams(tableParams);
        tableRow2.setLayoutParams(tableParams);

        tableRow3.setLayoutParams(tableParams);
        tableRow4.setLayoutParams(tableParams);

        mesReponses = new ArrayList<>();

        // Le try permet de lever une exception si jamais il n'y a pas de questions dans le quizz
        try {
            // On charge les réponses
            questionDB.chargerLesReponses(mesReponses, Integer.parseInt(mesQuestions.get(indice)));

            // On charge l'indice de la réponse juste
            indiceReponse = questionDB.getIndiceReponse(Integer.parseInt(mesQuestions.get(indice)));
            // On supprimer les précédent élements présents sur le TableLayout
            layoutProposition.removeAllViews();

            question = (TextView) findViewById(R.id.textViewQuestion);
            // On affiche la question
            question.setText(mesQuestions.get(indice + 1));

            // On parcourt l'ensemble des propositions
            for (int i = 0; i < mesReponses.size(); i++) {

                // Permet de détecter si on a une image ou pas
                if (mesReponses.get(i).startsWith("/")) {

                    imageQuestion = true;

                    // On créer un imageButton
                    final ImageButton imgBtnTag = new ImageButton(this);
                    imgBtnTag.setLayoutParams(rowParamsImg);
                    imgBtnTag.setId(i);
                    // On affiche les images en fonction du chemin
                    Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(mesReponses.get(i)));
                    imgBtnTag.setBackground(d);

                    // Permet de disposer les images proprement dans le tableau avec une image sur deux sur un TableLayout
                    // Affiche en carré donc
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

                    // Affiche les propositions verticalement (Permet de jouer sur téléphone)
                    if (i == 0) {
                        tableRow1.addView(btnTag);
                    } else if (i == 1) {
                        tableRow2.addView(btnTag);
                    }else if (i == 2) {
                        tableRow3.addView(btnTag);
                    }else if (i == 3) {
                        tableRow4.addView(btnTag);
                    }

                    btnTag.setOnClickListener(myhandler1);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(QuizzActivity.this, "Ce quizz ne contient pas de questions !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // On ajoute les deux premiers tableRow qui de toutes manière sont remplis
        layoutProposition.addView(tableRow1);
        layoutProposition.addView(tableRow2);

        // Si la question contient du texte dans ses propositions on affiche ajoute les TableLayout suivants
        if (!imageQuestion) {
            layoutProposition.addView(tableRow3);
            layoutProposition.addView(tableRow4);
        }
    }

    // Gère le bouton suivant
    View.OnClickListener myhandlerButtonNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // On augmente l'indice de la question de deux
            indiceQuestion+=2;
            // Si on est à la dernière question
            if (indiceQuestion >= mesQuestions.size()) {
                // On appele fin quizz
                finQuizz();
            } else {
                // Sinon on continue à jouer avec la question suivante
                joue(indiceQuestion);
            }
        }
    };

    // Gère le bouton qui permet l'affichage de la réponse
    View.OnClickListener myhandlerButtonVoirReponse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                // On démarre l'activité ShowAnswer en envoyant directement la réponse et s'il s'agit d'une question image
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

    // Gère le clique sur une proposition
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Si c'est la bonne réponse
            if (verifieReponse(v.getId() + 1, indiceReponse)) {
                // On vérifie que l'utilisateur n'a pas cliqué sur voir réponse
                if (estAlleVoirReponse) {
                    Toast.makeText(QuizzActivity.this, "VOUS AVEZ TRICHÉ !", Toast.LENGTH_SHORT).show();
                    // On decrémente le score uniquement s'il est positif
                    if (scoreJeu > 0) { scoreJeu-=1; }
                    score.setText(String.valueOf(scoreJeu));
                    estAlleVoirReponse = false;
                } else {
                    Toast.makeText(QuizzActivity.this, "CORRECT !", Toast.LENGTH_SHORT).show();
                    // On augemente le score de 1
                    scoreJeu+=1;
                    score.setText(String.valueOf(scoreJeu));
                }
            } else {
                Toast.makeText(QuizzActivity.this, "MAUVAISE REPONSE !", Toast.LENGTH_SHORT).show();
            }
            // On augmente l'indice de la question de deux
            indiceQuestion+=2;
            // Si on est à la dernière question
            if (indiceQuestion >= mesQuestions.size()) {
                // On appel fin quizz
                finQuizz();
            } else {
                // Sinon on continue avec la question suivante
                joue(indiceQuestion);
            }
        }
    };

    /* Méthode qui permet de terminer le quizz et d'afficher les éléments corespondants */
    public void finQuizz() {

        // On supprime tous les éléments présents sur la fenêtre
        layoutQuestion.removeAllViews();
        layoutButton.removeAllViews();
        layoutProposition.removeAllViews();
        layoutScore.removeAllViews();

        // On créer tous les nouveaux éléments
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

        // On les ajoute sur nos layout
        layoutQuestion.addView(btnRejouer);
        layoutQuestion.addView(btnRetourQuizz);
        layoutProposition.addView(scoreFinQuizz);

        // Si l'utilisateur clique sur rejouer le quizz
        btnRejouer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // On termine l'activité et on la relance en envayant le numéro du quizz
                Intent intent = getIntent();
                finish();
                intent.putExtra("quizzNumber", quizzNumber);
                startActivity(intent);
            }
        });

        // Si l'utilisateur clique sur Retour au quizz
        btnRetourQuizz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // On termine l'activité et on démarre celle qui affiche la liste des quizz en état PLAY
                Intent intent;
                finish();
                intent = new Intent(QuizzActivity.this, SelectQuizzActivity.class);
                intent.putExtra("STATE", "play");
                startActivity(intent);
            }
        });

    }

    /* Méthode qui permet de vérifier si l'utilisateur a bien répondu */
    private boolean verifieReponse(int idButton, int indiceReponse) {
        boolean result;
        // Si idButton et indiceReponse sont égaux, result = true
        result = idButton == indiceReponse;
        return result;
    }


}

package projet.samp.quizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    ArrayAdapter<String> adapter;
    QuestionDataBase questionDB ;

    TextView question;
    int indiceQuestion;
    boolean estAlleVoirReponse;

    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
        indiceQuestion = settings.getInt("myIndice", 0);


        setContentView(R.layout.quizz);

        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuestions(mesQuestions);
        questionDB.chargerLesReponses(mesReponses);

        Button buttonVrai = (Button) findViewById(R.id.buttonVrai);
        Button buttonFaux = (Button) findViewById(R.id.buttonFaux);
        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        Button buttonVoirReponse = (Button) findViewById(R.id.buttonVoirReponse);


        question = (TextView) findViewById(R.id.textViewQuestion);
        question.setText(mesQuestions.get(indiceQuestion));


        buttonVrai.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (verifieReponse("vrai", indiceQuestion) == true) {
                    if (estAlleVoirReponse) {
                        Toast.makeText(getApplicationContext(), "Vous avez déjà consulté la réponse !", Toast.LENGTH_SHORT).show();
                        estAlleVoirReponse = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Correct !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Mauvaise Réponse !", Toast.LENGTH_SHORT).show();
                }

                if (indiceQuestion >= mesQuestions.size()-1) {
                    question.setText(mesQuestions.get(0));
                    indiceQuestion = 0;
                } else {
                    indiceQuestion++;
                    question.setText(mesQuestions.get(indiceQuestion));
                }
            }
        });

        buttonFaux.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (verifieReponse("faux", indiceQuestion) == true) {
                    if (estAlleVoirReponse) {
                        Toast.makeText(getApplicationContext(), "Vous avez déjà consulté la réponse !", Toast.LENGTH_SHORT).show();
                        estAlleVoirReponse = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Correct !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Mauvaise Réponse !", Toast.LENGTH_SHORT).show();
                }

                if (indiceQuestion >= mesQuestions.size()-1) {
                    question.setText(mesQuestions.get(0));
                    indiceQuestion = 0;
                } else {
                    indiceQuestion++;
                    question.setText(mesQuestions.get(indiceQuestion));
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (indiceQuestion >= mesQuestions.size()-1) {
                    question.setText(mesQuestions.get(0));
                    indiceQuestion = 0;
                } else {
                    indiceQuestion++;
                    question.setText(mesQuestions.get(indiceQuestion));
                }
            }
        });

        buttonVoirReponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QuizzActivity.this, ShowAnswerActivity.class);
                intent.putExtra("reponse", mesReponses.get(indiceQuestion));
                startActivity(intent);
                estAlleVoirReponse = true;
            }
        });

    }

    private boolean verifieReponse(String reponse, int indiceQuestion) {
        boolean result;
        if ( mesReponses.get(indiceQuestion).equals(reponse)) {
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
        System.out.println(indiceQuestion);
        // Commit the edits!
        editor.commit();
    }

}

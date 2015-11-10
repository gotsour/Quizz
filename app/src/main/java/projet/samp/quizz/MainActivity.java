package projet.samp.quizz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    String URL = "https://dept-info.univ-fcomte.fr/joomla/images/CR0700/Quizzs.xml";
    QuestionDataBase database;
    static ArrayList<Quizz> quizzsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        database = new QuestionDataBase(this);

        Button boutonJouer = (Button) findViewById(R.id.buttonJouer);
        Button boutonParametrer = (Button) findViewById(R.id.buttonParametrer);
        Button boutonTelecharger = (Button) findViewById(R.id.buttonDL);

        boutonJouer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectQuizzActivity.class);
                intent.putExtra("STATE", "play");
                startActivity(intent);
            }
        });

        boutonParametrer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectQuizzActivity.class);
                intent.putExtra("STATE", "edit");
                startActivity(intent);
            }
        });

        boutonTelecharger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                download();
            }
        });

    }

    public void download() {
        DownloadXML xml = (DownloadXML) new DownloadXML().execute(URL);
        ArrayList<Quizz> quizzsList = new ArrayList<>();
        try {
            quizzsList = xml.get();
            Toast.makeText(MainActivity.this, "Le téléchargement à réussi !", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(MainActivity.this, "Le téléchargement à échoué !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        } catch (ExecutionException e) {
            Toast.makeText(MainActivity.this, "Le téléchargement à échoué !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        insereBDD(quizzsList);
    }

    public void insereBDD(ArrayList<Quizz> quizzsList) {

        int id_quizz = database.getNextId("quizz");
        int id_question = database.getNextId("question");
        int id_reponse = database.getNextId("proposition");

        for (int i = 0 ; i < quizzsList.size() ; i++) {

            String quizzName = quizzsList.get(i).getQuizzName();
            database.creerQuizz(id_quizz, quizzName);

            for (int j = 0 ; j < quizzsList.get(i).questionList.size(); j++) {

                String texteQuestion = quizzsList.get(i).questionList.get(j).getQuestion();
                int indiceReponse = quizzsList.get(i).questionList.get(j).getIndiceReponce();
                database.creerQuestion(id_question, texteQuestion, id_quizz, indiceReponse);

                for (int k = 0 ; k < quizzsList.get(i).questionList.get(j).propositionsList.size(); k++) {

                    String texteProposition = quizzsList.get(i).questionList.get(j).propositionsList.get(k).getProposition();
                    database.creerProposition(id_reponse, texteProposition, id_question);
                    id_reponse++;
                }

                id_question++;
            }

            id_quizz++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
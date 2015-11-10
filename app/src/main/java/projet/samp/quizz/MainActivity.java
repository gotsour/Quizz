package projet.samp.quizz;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    String URL = "https://dept-info.univ-fcomte.fr/joomla/images/CR0700/Quizzs.xml";
    QuestionDataBase database;
    static ArrayList<Quizz> quizzsList = new ArrayList<>();
    DownloadXML xml;

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
                xml = new DownloadXML();
                xml.execute(URL);
                Toast.makeText(MainActivity.this, "Le téléchargement à réussi !", Toast.LENGTH_SHORT).show();
                /**
                 * Trouvez le moyen de faire les deux instructions suivantes seulement après etre sur que execute
                 * est terminé et a réussi
                 */
                quizzsList = xml.getQuizzsList();
                insereBDD(quizzsList);

            }
        });



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
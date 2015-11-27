package projet.samp.quizz;

import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
 * Activité correspondant au menu principal
 */

public class MainActivity extends AppCompatActivity {

    String URL = "https://dept-info.univ-fcomte.fr/joomla/images/CR0700/Quizzs.xml";
    QuestionDataBase database;
    ArrayList<Quizz> quizzsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        quizzsList=new ArrayList<>();

        //this.deleteDatabase("questions.db");
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

    /* Méthode qui appelle la classe Donwload */
    public void download() {

        int testDatabaseAnimal=0;
        int testDatabaseInfo=0;
        int testDatabaseCulture=0;

        // On vérifie si les quizz du fichier XML sont déjà présents dans la base
        try {
            testDatabaseAnimal = database.getQuizzId("Monde Animal");
            testDatabaseInfo = database.getQuizzId("Informatique");
            testDatabaseCulture = database.getQuizzId("Culture générale");
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // On vérifie si l'utilisateur est connecté à internet
        if (isNetworkAvailable()) {
            // Si les quizz du fichier XML ne sont pas présent
            if (testDatabaseAnimal == 0 && testDatabaseCulture == 0 && testDatabaseInfo == 0) {
                DownloadXML xml = (DownloadXML) new DownloadXML().execute(URL);
                try {
                    // On appelle la fonction execute() de Download par la méthode get qui lance un thread
                    quizzsList = xml.get();
                    Toast.makeText(MainActivity.this, "Le téléchargement à réussi !", Toast.LENGTH_SHORT).show();
                    // On insere les quizz dans la base de données
                    insereBDD(quizzsList);
                } catch (InterruptedException | ExecutionException e) {
                    Toast.makeText(MainActivity.this, "Le téléchargement à échoué !", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Vous avez déjà téléchargé les quizz !", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "Vous n'êtes pas connecté à internet !", Toast.LENGTH_SHORT).show();
        }
    }

    /* Méthode qui parcourt l'arrylist pour remplir la base de données */
    public void insereBDD(ArrayList<Quizz> quizzsList) {

        // On récuprère les identifiants des prochains élements dans la base
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

    /* Méthode qui vérifie si le réseau est disponible */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
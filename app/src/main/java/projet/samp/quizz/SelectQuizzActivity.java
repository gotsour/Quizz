package projet.samp.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by twesterm on 07/11/15.
 */
public class SelectQuizzActivity extends MainActivity {

    ArrayList<HashMap<String,String>> mesQuizz = new ArrayList<HashMap<String,String>>();
    SimpleAdapter adapter;
    QuestionDataBase questionDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_quizz);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.hide();

        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuizz(mesQuizz);

        Intent intent = getIntent();
        final String action = intent.getStringExtra("STATE");

        final ListView listQuizz = (ListView) findViewById(R.id.listViewSelectQuizz);

        /* Adapter qui permet de charger la listview avec la qestion et la reponse en subItem */

        String[] from = new String[] { "quizzName" };
        int[] to = new int[] { android.R.id.text1};
        adapter = new SimpleAdapter(this, mesQuizz, android.R.layout.simple_list_item_1, from, to);

        /* On applique l'adapter a la listView */
        listQuizz.setAdapter(adapter);


        if (action.equals("edit")) {
            floatingActionButton.show();
            floatingActionButton.setOnClickListener(myhandler1);

            listQuizz.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {

                    TextView c = (TextView) arg1.findViewById(android.R.id.text1);
                    final String quizzName = c.getText().toString();

                    AlertDialog.Builder alert = new AlertDialog.Builder(SelectQuizzActivity.this);

                    alert.setTitle("Suppression Quizz");
                    alert.setMessage("Etes vous sûr de supprimer ce quizz ?");


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            questionDB.supprimerQuizz(quizzName);
                            mesQuizz.remove(pos);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                    return true;
                }
            });

        }


        /* Si on clique sur l'item de la ListView */
        listQuizz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView c = (TextView) view.findViewById(android.R.id.text1);
                String quizzName = c.getText().toString();
                int id_quizz = questionDB.getQuizzId(quizzName);

                if (action.equals("play")) {
                    // On ouvre le quizz en question et on joue
                    Intent intent = getIntent();
                    finish();
                    intent = new Intent(SelectQuizzActivity.this, QuizzActivity.class);
                    intent.putExtra("quizzNumber", id_quizz);
                    startActivity(intent);

                } else if (action.equals("edit")) {
                    // On ouvre le quizz en question pour le parametrer
                    Intent intent = new Intent(SelectQuizzActivity.this, ShowQuestionsActivity.class);
                    intent.putExtra("quizzNumber", id_quizz);
                    startActivity(intent);

                }

            }
        });

    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SelectQuizzActivity.this);

            alert.setTitle("Nouveau Quizz");
            alert.setMessage("Veuiller saisir le nom de votre quizz");

            // Set an EditText view to get user input
            final EditText input = new EditText(SelectQuizzActivity.this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String txt = input.getText().toString();

                    QuestionDataBase dataBase = new QuestionDataBase(SelectQuizzActivity.this);
                    int nextQuizzId = database.getNextId("quizz");
                    dataBase.creerQuizz(nextQuizzId, txt);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id_quizz", String.valueOf(nextQuizzId));
                    map.put("quizzName", txt);
                    mesQuizz.add(map);

                    adapter.notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
    };

}

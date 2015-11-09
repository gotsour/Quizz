package projet.samp.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twesterm on 07/11/15.
 */
public class SelectQuizzActivity extends MainActivity {

    List<String> mesQuizz = new ArrayList<>();
    ArrayAdapter adapter;
    QuestionDataBase questionDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_quizz);

        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuizz(mesQuizz);

        Intent intent = getIntent();
        final String action = intent.getStringExtra("STATE");

        final ListView listQuizz = (ListView) findViewById(R.id.listViewSelectQuizz);

        /* Adapter qui permet de charger la listview avec la qestion et la reponse en subItem */
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, mesQuizz) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(position+1+"");
                text2.setText(mesQuizz.get(position));
                return view;
            }
        };

        /* On applique l'adapter a la listView */
        listQuizz.setAdapter(adapter);


        if (action.equals("edit")) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutSelectQuizz);
            final Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setId(View.generateViewId());
            btnTag.setText("Ajouter un quizz");
            layout.addView(btnTag);
            btnTag.setOnClickListener(myhandler1);
        }


        /* Si on clique sur l'item de la ListView */
        listQuizz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (action.equals("play")) {
                    // On ouvre le quizz en question et on joue
                    Intent intent = new Intent(SelectQuizzActivity.this, QuizzActivity.class);
                    intent.putExtra("quizzNumber", position + 1);
                    startActivity(intent);

                } else if (action.equals("edit")) {
                    // On ouvre le quizz en question pour le parametrer
                    Intent intent = new Intent(SelectQuizzActivity.this, ShowQuestionsActivity.class);
                    intent.putExtra("quizzNumber", position + 1);
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

                    mesQuizz.add(txt);
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

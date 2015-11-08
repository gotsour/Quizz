package projet.samp.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twesterm on 08/10/15.
 */
public class ShowQuestionsActivity extends MainActivity {

    List<String> mesQuestions = new ArrayList<>();
    List<String> mesReponses = new ArrayList<>();
    ArrayAdapter adapter;
    QuestionDataBase questionDB ;

    EditText editQuestion;
    RadioButton buttonVrai;
    RadioButton buttonFaux;
    Button buttonAjouter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_questions);

        Intent intent = getIntent();
        final int quizzNumber = intent.getIntExtra("quizzNumber", 0);

        editQuestion = (EditText) findViewById(R.id.editTextQuestion);
        buttonVrai = (RadioButton) findViewById(R.id.radioButtonVrai);
        buttonFaux = (RadioButton) findViewById(R.id.radioButtonFaux);
        buttonAjouter = (Button) findViewById(R.id.buttonAdd);

        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuestions(mesQuestions, quizzNumber);
        //questionDB.chargerLesReponses(mesReponses);

        /* Prevent Keyboard to pop up automatically */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final ListView vueQuestions = (ListView) findViewById(R.id.expandableListViewQuestion);

        /* Adapter qui permet de charger la listview avec la qestion et la reponse en subItem */
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, mesQuestions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(mesQuestions.get(position));
                //text2.setText(mesReponses.get(position));
                return view;
            }
        };

        /* On applique l'adapter a la listView */
        vueQuestions.setAdapter(adapter);

        /* Si on clique sur le bouton Ajouter */
        buttonAjouter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String question = editQuestion.getText().toString();

                /* On parcourt la liste des questions pour vérifier que les strings ne soient pas
                identiques et donc pour ne pas avoir d'erreurs lors de la suppression
                 */
                boolean existeDeja = false;
                for (int i = 0; i < mesQuestions.size(); i++) {
                    if (mesQuestions.get(i).equals(question)) {
                        existeDeja = true;
                    }
                }

                if (!existeDeja) {
                    mesQuestions.add(question);
                    String reponse = "faux";
                    if (buttonVrai.isChecked()) {
                        reponse = "vrai";
                    } else if (buttonFaux.isChecked()) {
                        reponse = "faux";
                    }
                    /* On ajoute la réponse a la suite de la liste de réponse */
                    mesReponses.add(reponse);
                    /* On insert le couple question/reponse dans la BDD */
                    questionDB.insertQuestion(question, reponse);
                    /* On indique a l'adapter que notre liste a été modifié ce qui a pour but de la réactualiser */
                    adapter.notifyDataSetChanged();
                    /* On remet le barre d'édition de texte a vide */
                    editQuestion.setText("");
                    Toast.makeText(ShowQuestionsActivity.this, "Question ajoutée !", Toast.LENGTH_SHORT).show();

                    /* Hide keyboard after button press */
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(ShowQuestionsActivity.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                } else {
                    Toast.makeText(ShowQuestionsActivity.this, "Cette question existe déjà !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Si on clique sur l'item de la ListView */
        vueQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView text = (TextView) view.findViewById(android.R.id.text1);

                /* Boite de dialog pour la confirmation de suppression */
                new AlertDialog.Builder(ShowQuestionsActivity.this)
                        .setTitle("Confirmer")
                        .setMessage("Voulez-vous vraiment supprimer cette question ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* On récupère le texte de l'item selectionné */
                                String question = text.getText().toString();

                                int position;
                                /* On parcourt la liste des question pour matcher avec le texte de l'item selectionné*/
                                for (int i = 0; i < mesQuestions.size(); i++) {
                                    if (mesQuestions.get(i).equals(question)) {
                                        position = i;
                                        /* Si c'est le cas on supprime la question et la réponse des deux listes */
                                        mesQuestions.remove(position);
                                        mesReponses.remove(position);
                                        /* On supprimer alors le couple question/reponse correspondant */
                                        questionDB.supprimeQuestion(question);
                                        /* On indique a l'adapter que notre liste a été modifié ce qui a pour but de la réactualiser */
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(ShowQuestionsActivity.this, "Question supprimée !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }
}

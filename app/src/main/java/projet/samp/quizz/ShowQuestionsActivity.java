package projet.samp.quizz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ShowQuestionsActivity extends MainActivity {

    List<String> mesQuestions = new ArrayList<>();
    List<String> mesReponses = new ArrayList<>();
    ArrayAdapter adapter;
    QuestionDataBase questionDB ;
    int quizzNumber;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_questions);

        /* On récupère l'id du quizz renvoyé par l'instance qui a généré cette activité */
        Intent intent = getIntent();
        quizzNumber = intent.getIntExtra("quizzNumber", 0);

        questionDB = new QuestionDataBase(this);
        questionDB.chargerLesQuestionsSansId(mesQuestions, quizzNumber);

        /* Prevent Keyboard to pop up automatically */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final ListView vueQuestions = (ListView) findViewById(R.id.expandableListViewQuestion);

        /* Adapter qui permet de charger la listview avec la qestion */
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mesQuestions);

        /* On applique l'adapter a la listView */
        vueQuestions.setAdapter(adapter);

        /* Si on clique sur le petit plus en bas à droite */
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(ajouterQuestion);

        /* En cas d'appui long sur un item */
        vueQuestions.setOnItemLongClickListener(supprimerQuestion);

        /* Si on clique sur l'item de la ListView */
        vueQuestions.setOnItemClickListener(modifierQuestion);


    }


    AdapterView.OnItemClickListener modifierQuestion = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    AdapterView.OnItemLongClickListener supprimerQuestion = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
            TextView c = (TextView) arg1.findViewById(android.R.id.text1);
            final String questionTexte = c.getText().toString();

            AlertDialog.Builder alert = new AlertDialog.Builder(ShowQuestionsActivity.this);

            alert.setTitle("Suppression Question");
            alert.setMessage("Etes vous sûr de supprimer cette question ?");


            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    questionDB.supprimerQuestion(questionTexte);
                    mesQuestions.remove(pos);
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
    };

    View.OnClickListener ajouterQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(ShowQuestionsActivity.this);
            View promptsView = li.inflate(R.layout.nouvelle_question, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowQuestionsActivity.this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText questionTexte = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
            final EditText propositionTexte1 = (EditText) promptsView.findViewById(R.id.editTextProposition1);
            final EditText propositionTexte2 = (EditText) promptsView.findViewById(R.id.editTextProposition2);
            final EditText propositionTexte3 = (EditText) promptsView.findViewById(R.id.editTextProposition3);
            final EditText propositionTexte4 = (EditText) promptsView.findViewById(R.id.editTextProposition4);
            final EditText indiceReponse = (EditText) promptsView.findViewById(R.id.editTextIndiceReponse);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    /**
                                     * IL FAUT ENCORE VEVRIFIER QUE LE NOMBRE PROPOSITION >= 2
                                     * ET QUE LE NUMERO DE REPONSE EST COMPRIS ENTRE 1 ET NOMBRE PROPOSITION
                                     */

                                    QuestionDataBase dataBase = new QuestionDataBase(ShowQuestionsActivity.this);
                                    int nextQuestionId = database.getNextId("question");
                                    if (!questionTexte.getText().toString().equals("")) {
                                        if (!indiceReponse.getText().toString().equals("")) {
                                            dataBase.creerQuestion(nextQuestionId, questionTexte.getText().toString(), quizzNumber, Integer.parseInt(indiceReponse.getText().toString()));
                                            mesQuestions.add(questionTexte.getText().toString());
                                        }
                                    }

                                    int nextPropositionId = dataBase.getNextId("proposition");
                                    if (!propositionTexte1.getText().toString().equals("")) {
                                        dataBase.creerProposition(nextPropositionId, propositionTexte1.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte1.getText().toString());
                                    }
                                    if (!propositionTexte2.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        dataBase.creerProposition(nextPropositionId, propositionTexte2.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte2.getText().toString());
                                    }
                                    if (!propositionTexte3.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        dataBase.creerProposition(nextPropositionId, propositionTexte3.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte3.getText().toString());
                                    }
                                    if (!propositionTexte4.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        dataBase.creerProposition(nextPropositionId, propositionTexte4.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte4.getText().toString());
                                    }

                                    adapter.notifyDataSetChanged();

                                }
                            }

                    )
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

        }
    };


}

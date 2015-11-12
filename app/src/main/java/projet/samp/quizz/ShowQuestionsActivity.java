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
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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


            TextView c = (TextView) view.findViewById(android.R.id.text1);
            /* On sauvegarde le texte de la question */
            final String questionTexteSave = c.getText().toString();
            questionTexte.setText(questionTexteSave);


            final int id_question = database.getIdQuestion(questionTexteSave);
            List<String> listProposition = new ArrayList<>();
            database.chargerLesReponses(listProposition, id_question);
            String propositionTexte1Save = null;
            String propositionTexte2Save = null;
            String propositionTexte3Save = null;
            String propositionTexte4Save = null;

            if (listProposition.size() >= 1) {
                propositionTexte1.setText(listProposition.get(0));
                propositionTexte1Save = listProposition.get(0);
            }
            if (listProposition.size() >= 2) {
                propositionTexte2.setText(listProposition.get(1));
                propositionTexte2Save = listProposition.get(1);
            }
            if (listProposition.size() >= 3) {
                propositionTexte3.setText(listProposition.get(2));
                propositionTexte3Save = listProposition.get(2);
            }
            if (listProposition.size() >= 4) {
                propositionTexte4.setText(listProposition.get(3));
                propositionTexte4Save = listProposition.get(3);
            }

            final int id_reponseSave = database.getIndiceReponse(id_question);
            indiceReponse.setText(String.valueOf(id_reponseSave));


            // set dialog message
            final String finalPropositionTexte1Save = propositionTexte1Save;
            final String finalPropositionTexte2Save = propositionTexte2Save;
            final String finalPropositionTexte3Save = propositionTexte3Save;
            final String finalPropositionTexte4Save = propositionTexte4Save;
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    if (!questionTexte.getText().toString().equals("") && !questionTexte.getText().toString().equals(questionTexteSave)) {

                                        if (!indiceReponse.getText().toString().equals("") && !indiceReponse.getText().toString().equals(id_reponseSave)) {
                                            /* On update questionTexte et indiceQuestion */
                                            questionDB.updateQuestion(id_question, questionTexte.getText().toString());
                                            questionDB.updateIndiceReponse(id_question, Integer.parseInt(indiceReponse.getText().toString()));
                                        } else {
                                            /* On update questionTexte */
                                            questionDB.updateQuestion(id_question, questionTexte.getText().toString());
                                        }

                                        mesQuestions.set(position, questionTexte.getText().toString());
                                        adapter.notifyDataSetChanged();
                                    }

                                    int nextPropositionId = questionDB.getNextId("proposition");
                                    if (!propositionTexte1.getText().toString().equals("")) {
                                        if (!propositionTexte1.getText().toString().equals(finalPropositionTexte1Save)) {
                                            if (finalPropositionTexte1Save == null) {
                                                /* On créér une nouvelle proposition */
                                                questionDB.creerProposition(nextPropositionId, propositionTexte1.getText().toString(), id_question);
                                            } else {
                                            /* On update la proposition 1*/
                                                questionDB.updateProposition(questionDB.getIdProposition(finalPropositionTexte1Save, id_question), propositionTexte1.getText().toString());
                                            }
                                        }
                                    } else {
                                        if (finalPropositionTexte1Save != null) {
                                            /* On supprime la proposition 1 */
                                            questionDB.supprimerProposition(questionDB.getIdProposition(finalPropositionTexte1Save, id_question));
                                        }
                                    }

                                    if (!propositionTexte2.getText().toString().equals("")) {
                                        if (!propositionTexte2.getText().toString().equals(finalPropositionTexte2Save)) {
                                            if (finalPropositionTexte2Save == null) {
                                                /* On créér une nouvelle proposition */
                                                nextPropositionId++;
                                                questionDB.creerProposition(nextPropositionId, propositionTexte2.getText().toString(), id_question);
                                            } else {
                                            /* On update la proposition 2 */
                                                questionDB.updateProposition(questionDB.getIdProposition(finalPropositionTexte2Save, id_question), propositionTexte2.getText().toString());
                                            }
                                        }
                                    } else {
                                        if (finalPropositionTexte2Save != null) {
                                            /* On supprime la proposition 2 */
                                            questionDB.supprimerProposition(questionDB.getIdProposition(finalPropositionTexte2Save, id_question));
                                        }
                                    }

                                    if (!propositionTexte3.getText().toString().equals("")) {
                                        if (!propositionTexte3.getText().toString().equals(finalPropositionTexte3Save)) {
                                            if (finalPropositionTexte3Save == null) {
                                                /* On créér une nouvelle proposition */
                                                nextPropositionId++;
                                                questionDB.creerProposition(nextPropositionId, propositionTexte3.getText().toString(), id_question);
                                            } else {
                                                /* On update la proposition 3 */
                                                questionDB.updateProposition(questionDB.getIdProposition(finalPropositionTexte3Save, id_question), propositionTexte3.getText().toString());
                                            }
                                        }
                                    } else {
                                        if (finalPropositionTexte3Save != null) {
                                            /* On supprime la proposition 3 */
                                            questionDB.supprimerProposition(questionDB.getIdProposition(finalPropositionTexte3Save, id_question));
                                        }
                                    }

                                    if (!propositionTexte4.getText().toString().equals("")) {
                                        if (!propositionTexte4.getText().toString().equals(finalPropositionTexte4Save)) {
                                            if (finalPropositionTexte4Save == null) {
                                                nextPropositionId++;
                                                /* On créér une nouvelle proposition */
                                                questionDB.creerProposition(nextPropositionId, propositionTexte4.getText().toString(), id_question);
                                            } else {
                                            /* On update la proposition 4*/
                                                questionDB.updateProposition(questionDB.getIdProposition(finalPropositionTexte4Save, id_question), propositionTexte4.getText().toString());
                                            }
                                        }
                                    } else {
                                        if (finalPropositionTexte4Save != null) {
                                            /* On supprime la proposition 4 */
                                            questionDB.supprimerProposition(questionDB.getIdProposition(finalPropositionTexte4Save, id_question));
                                        }

                                    }


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

                                    int nextQuestionId = database.getNextId("question");
                                    if (!questionTexte.getText().toString().equals("")) {
                                        if (!indiceReponse.getText().toString().equals("")) {
                                            questionDB.creerQuestion(nextQuestionId, questionTexte.getText().toString(), quizzNumber, Integer.parseInt(indiceReponse.getText().toString()));
                                            mesQuestions.add(questionTexte.getText().toString());
                                        } else {
                                            questionDB.creerQuestion(nextQuestionId, questionTexte.getText().toString(), quizzNumber, 0);
                                            mesQuestions.add(questionTexte.getText().toString());
                                        }
                                    }

                                    int nextPropositionId = questionDB.getNextId("proposition");
                                    if (!propositionTexte1.getText().toString().equals("")) {
                                        questionDB.creerProposition(nextPropositionId, propositionTexte1.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte1.getText().toString());
                                    }
                                    if (!propositionTexte2.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        questionDB.creerProposition(nextPropositionId, propositionTexte2.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte2.getText().toString());
                                    }
                                    if (!propositionTexte3.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        questionDB.creerProposition(nextPropositionId, propositionTexte3.getText().toString(), nextQuestionId);
                                        mesReponses.add(propositionTexte3.getText().toString());
                                    }
                                    if (!propositionTexte4.getText().toString().equals("")) {
                                        nextPropositionId++;
                                        questionDB.creerProposition(nextPropositionId, propositionTexte4.getText().toString(), nextQuestionId);
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

package projet.samp.quizz;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.*;
        import android.widget.Button;

        import org.w3c.dom.Document;
        import org.w3c.dom.Element;
        import org.w3c.dom.NodeList;
        import org.xml.sax.InputSource;

        import java.net.URL;
        import java.util.ArrayList;
        import java.util.Iterator;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    String URL = "http://raphaello.univ-fcomte.fr/m1/Quizzs.xml";
    static ArrayList<Quizz> quizzsList = new ArrayList<>();
    ProgressDialog pDialog;
    NodeList racine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

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
                Intent intent = new Intent(MainActivity.this, ShowQuestionsActivity.class);
                startActivity(intent);
            }
        });

        boutonTelecharger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DownloadXML().execute(URL);
            }
        });


    }

    private class DownloadXML extends AsyncTask<String, Void, Void> {
        private DownloadXML() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(MainActivity.this);
            // Set progressbar title
            pDialog.setTitle("Android Simple XML Parsing using DOM Tutorial");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }

        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
                racine = doc.getElementsByTagName("Quizzs");

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {
            int indice = 0;

            NodeList racineNoeuds = racine.item(0).getChildNodes();
            int nbRacineNoeuds = racineNoeuds.getLength();

            for (int i = 0; i < nbRacineNoeuds; i++) {
                if (racineNoeuds.item(i).getNodeType() == (short) 1) {

                    Element quizzs = (Element) racineNoeuds.item(i);

                    /* On creer un nouveau Quizz et on l'insere dans notre liste quizz */
                    quizzsList.add(new Quizz(quizzs.getAttribute("type")));

                    NodeList questions = quizzs.getElementsByTagName("Question");
                    int nbQuestionsElements = questions.getLength();

                    for (int j = 0; j < nbQuestionsElements; j++) {

                        Element question = (Element) questions.item(j);

                        /* On creer une nouvelle Question et on l'insere dans la liste de questions du quizz courant */
                        quizzsList.get(indice).questionList.add(new Question(question.getFirstChild().getNodeValue().replaceAll("\\t", "").replaceAll("\\n", "")));

                        NodeList propositions = question.getElementsByTagName("Propositions");
                        int nbPropositionsElements = propositions.getLength();


                        for (int k = 0; k < nbPropositionsElements; k++) {
                            /* On creer une nouvelle proposition et on l'insere dans la liste de proposition de la question courante du quizz courant */
                            quizzsList.get(indice).questionList.get(j).propositionsList.add(new Proposition(propositions.item(k).getTextContent().replaceAll("\\t", "").replaceAll("\\n", "")));
                        }

                        Element indiceReponse = (Element) question.getElementsByTagName("Reponse").item(0);
                        /* On indique l'indice de la proposition vrai dans la question courante du quizz courant */
                        quizzsList.get(indice).questionList.get(j).setIndiceReponce(new Integer(indiceReponse.getAttribute("valeur")).intValue());
                    }

                    indice++;
                }
            }
            pDialog.dismiss();

            /*for (int i = 0 ; i < quizzsList.size(); i++) {

                System.out.println(quizzsList.get(i).getQuizzName());

                for (int j = 0 ; j < quizzsList.get(i).questionList.size(); j++) {

                    System.out.println(quizzsList.get(i).questionList.get(j).getQuestion());

                    for (int k = 0 ; k < quizzsList.get(i).questionList.get(j).propositionsList.size(); k++) {

                        System.out.println(quizzsList.get(i).questionList.get(j).propositionsList.get(k).getProposition());

                    }

                    System.out.println(quizzsList.get(i).questionList.get(j).getIndiceReponce());
                }
            }*/

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

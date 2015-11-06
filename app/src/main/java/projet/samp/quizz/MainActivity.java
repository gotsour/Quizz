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

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    String URL = "http://raphaello.univ-fcomte.fr/m1/Quizzs.xml";
    ArrayList<Quizz> quizzsList = new ArrayList<>();
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
                Intent intent = new Intent(MainActivity.this, QuizzActivity.class);
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

                    quizzsList.add(new Quizz(quizzs.getAttribute("type")));
                    NodeList questions = quizzs.getElementsByTagName("Question");
                    int nbQuestionsElements = questions.getLength();
                    ArrayList<Question> questionsList = new ArrayList<>();

                    for (int j = 0; j < nbQuestionsElements; j++) {
                        Element question = (Element) questions.item(j);
                        questionsList.add(new Question(question.getFirstChild().getNodeValue()));
                        NodeList propositions = question.getElementsByTagName("Propositions");
                        int nbPropositionsElements = propositions.getLength();

                        ArrayList<Proposition> propositionsList = new ArrayList<>();

                        for (int k = 0; k < nbPropositionsElements; k++) {
                            propositionsList.add(new Proposition(propositions.item(k).getTextContent()));
                        }

                        quizzsList.get(indice).questionList.get(j).setPropositionsList(propositionsList);
                        Element indiceReponse = (Element) question.getElementsByTagName("Reponse").item(0);
                        quizzsList.get(indice).questionList.get(j).setIndiceReponce(new Integer(indiceReponse.getAttribute("valeur")).intValue());
                        indice++;
                    }
                    quizzsList.get(indice).setQuestionList(questionsList);
                }
            }
           pDialog.dismiss();
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

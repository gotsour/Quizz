package projet.samp.quizz;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class DownloadXML extends AsyncTask<String, Void, ArrayList<Quizz>> {

    NodeList racine;

    protected ArrayList<Quizz> doInBackground(String... Url) {
        try {
            URL url = new URL(Url[0]);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Download the XML file
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            // Locate the Tag Name
            racine = doc.getElementsByTagName("Quizzs");

            return populate();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Quizz> populate() {
        ArrayList<Quizz> quizzsList = new ArrayList<>();
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

                    NodeList propositions = question.getElementsByTagName("Proposition");
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
        return quizzsList;
    }


    protected void onPostExecute(Void args) {

    }
}
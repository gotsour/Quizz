package projet.samp.quizz;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/*
 * Activité qui permet de modifier une question
 * ave des images en guise de propositions
 */

public class EditImageQuestionActivity extends MainActivity {
    private static final int PICK_IMAGE_1 = 1;
    private static final int PICK_IMAGE_2 = 2;
    private static final int PICK_IMAGE_3 = 3;
    private static final int PICK_IMAGE_4 = 4;
    EditText questionTexte;
    ImageButton image1;
    ImageButton image2;
    ImageButton image3;
    ImageButton image4;
    Button ajout;
    EditText reponseTexte;
    QuestionDataBase questionDB;
    int quizzNumber;
    String picturePath1;
    String picturePath2;
    String picturePath3;
    String picturePath4;
    String questionTexteSave;
    String picturePathSave1;
    String picturePathSave2;
    String picturePathSave3;
    String picturePathSave4;
    String reponseTexteSave;
    int id_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_question_image);

        // On récupère l'id du quizz renvoyé par l'instance qui a généré cette activité
        Intent intent = getIntent();
        quizzNumber = intent.getIntExtra("quizzNumber", 0);
        // On récupère les données de la questions
        questionTexteSave = intent.getStringExtra("texteQuestion");
        picturePathSave1 = intent.getStringExtra("reponse1");
        picturePathSave2 = intent.getStringExtra("reponse2");
        picturePathSave3 = intent.getStringExtra("reponse3");
        picturePathSave4 = intent.getStringExtra("reponse4");
        reponseTexteSave = String.valueOf(intent.getIntExtra("numeroReponse", 0));

        questionDB = new QuestionDataBase(this);

        id_question = questionDB.getIdQuestion(questionTexteSave);

        questionTexte = (EditText) findViewById(R.id.editTextQuestion);
        image1 = (ImageButton) findViewById(R.id.imageButton);
        image2 = (ImageButton) findViewById(R.id.imageButton2);
        image3 = (ImageButton) findViewById(R.id.imageButton3);
        image4 = (ImageButton) findViewById(R.id.imageButton4);
        reponseTexte = (EditText) findViewById(R.id.editTextReponse);
        ajout = (Button) findViewById(R.id.buttonAjout);

        ajout.setText("Modifier Question");

        picturePath1 = null;
        picturePath2 = null;
        picturePath3 = null;
        picturePath4 = null;

        // On affiche dans nos champs texte et nos button la question, les images et l'indice de réponse
        questionTexte.setText(questionTexteSave);
        if (picturePathSave1 != null) {
            Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePathSave1));
            image1.setBackground(d);
        }

        if (picturePathSave2 != null) {
            Drawable d1 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePathSave2));
            image2.setBackground(d1);
        }

        if (picturePathSave3 != null) {
            Drawable d2 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePathSave3));
            image3.setBackground(d2);
        }

        if (picturePathSave4 != null) {
            Drawable d3 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePathSave4));
            image4.setBackground(d3);
        }

        reponseTexte.setText(reponseTexteSave);

        image1.setOnClickListener(chercheImage1);
        image2.setOnClickListener(chercheImage2);
        image3.setOnClickListener(chercheImage3);
        image4.setOnClickListener(chercheImage4);
        ajout.setOnClickListener(ajouterQuestion);

    }

    /* Méthode qui récupère le résultat de l'image envoyée par la gallerie et se
     * charge d'updater la base de données */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // En fonction de la méthode et donc de l'image sur laquelle on a appuyé
            switch (requestCode) {
                case PICK_IMAGE_1:
                    picturePath1 = picturePath;
                    // On insère la nouvelle image dans le button
                    Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePath1));
                    image1.setBackground(d);
                    // On update notre proposition dans la base de données
                    if (picturePathSave1 != null) {
                        questionDB.updateProposition(questionDB.getIdProposition(picturePathSave1, id_question), picturePath1);
                    } else {
                        int next_id = questionDB.getNextId("proposition");
                        questionDB.creerProposition(next_id, picturePath1, id_question);
                    }
                    break;
                case PICK_IMAGE_2:
                    picturePath2 = picturePath;
                    Drawable d2 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePath2));
                    image2.setBackground(d2);
                    if (picturePathSave2 != null) {
                        questionDB.updateProposition(questionDB.getIdProposition(picturePathSave2, id_question), picturePath2);
                    } else {
                        int next_id = questionDB.getNextId("proposition");
                        questionDB.creerProposition(next_id, picturePath2, id_question);
                    }
                    break;
                case PICK_IMAGE_3:
                    picturePath3 = picturePath;
                    Drawable d3 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePath3));
                    image3.setBackground(d3);
                    if (picturePathSave3 != null) {
                        questionDB.updateProposition(questionDB.getIdProposition(picturePathSave3, id_question), picturePath3);
                    } else {
                        int next_id = questionDB.getNextId("proposition");
                        questionDB.creerProposition(next_id, picturePath3, id_question);
                    }
                    break;
                case PICK_IMAGE_4:
                    picturePath4 = picturePath;
                    Drawable d4 = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePath4));
                    image4.setBackground(d4);
                    if (picturePathSave4 != null) {
                        questionDB.updateProposition(questionDB.getIdProposition(picturePathSave4, id_question), picturePath4);
                    } else {
                        int next_id = questionDB.getNextId("proposition");
                        questionDB.creerProposition(next_id, picturePath4, id_question);
                    }
                    break;
            }

        }


    }

    /* Méthode qui permet d'appeler la gallerie pour l'image 1 */
    View.OnClickListener chercheImage1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_1);

        }
    };

    /* Méthode qui permet d'appeler la gallerie pour l'image 2 */
    View.OnClickListener chercheImage2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_2);

        }
    };

    /* Méthode qui permet d'appeler la gallerie pour l'image  3*/
    View.OnClickListener chercheImage3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_3);

        }
    };

    /* Méthode qui permet d'appeler la gallerie pour l'image 4 */
    View.OnClickListener chercheImage4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_4);

        }
    };

    /* Méthode qui permet de terminer la modification de la question */
    View.OnClickListener ajouterQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!questionTexte.getText().toString().equals("") && !questionTexte.getText().toString().equals(questionTexteSave) || !reponseTexte.getText().toString().equals("") && !reponseTexte.getText().toString().equals(reponseTexteSave)) {
                if (!reponseTexte.getText().toString().equals("") && !reponseTexte.getText().toString().equals(reponseTexteSave)) {
                /* On update questionTexte et indiceQuestion */
                    questionDB.updateQuestion(id_question, questionTexte.getText().toString());
                    questionDB.updateIndiceReponse(id_question, Integer.parseInt(reponseTexte.getText().toString()));
                } else {
                /* On update questionTexte */
                    questionDB.updateQuestion(id_question, questionTexte.getText().toString());
                }
            }

            // On retourne à l'activité qui affiche les questions d'un quizz
            Intent intent = new Intent(EditImageQuestionActivity.this, ShowQuestionsActivity.class);
            intent.putExtra("quizzNumber", quizzNumber);
            startActivity(intent);
            // On termine notre activité
            finish();
        }
    };
}

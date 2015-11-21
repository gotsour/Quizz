package projet.samp.quizz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;


public class AddImageQuestionActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouvelle_question_image);

        /* On récupère l'id du quizz renvoyé par l'instance qui a généré cette activité */
        Intent intent = getIntent();
        quizzNumber = intent.getIntExtra("quizzNumber", 0);

        questionDB = new QuestionDataBase(this);

        questionTexte = (EditText) findViewById(R.id.editTextQuestion);
        image1 = (ImageButton) findViewById(R.id.imageButton);
        image2 = (ImageButton) findViewById(R.id.imageButton2);
        image3 = (ImageButton) findViewById(R.id.imageButton3);
        image4 = (ImageButton) findViewById(R.id.imageButton4);
        reponseTexte = (EditText) findViewById(R.id.editTextReponse);
        ajout = (Button) findViewById(R.id.buttonAjout);

        picturePath1 = null;
        picturePath2 = null;
        picturePath3 = null;
        picturePath4 = null;

        image1.setOnClickListener(chercheImage1);
        image2.setOnClickListener(chercheImage2);
        image3.setOnClickListener(chercheImage3);
        image4.setOnClickListener(chercheImage4);
        ajout.setOnClickListener(ajouterQuestion);


    }

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

            switch (requestCode) {
                case PICK_IMAGE_1:
                    picturePath1 = picturePath;
                    image1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
                case PICK_IMAGE_2:
                    picturePath2 = picturePath;
                    image2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
                case PICK_IMAGE_3:
                    picturePath3 = picturePath;
                    image3.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
                case PICK_IMAGE_4:
                    picturePath4 = picturePath;
                    image4.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
            }

        }


    }

    View.OnClickListener chercheImage1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_1);

        }
    };

    View.OnClickListener chercheImage2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_2);

        }
    };

    View.OnClickListener chercheImage3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_3);

        }
    };

    View.OnClickListener chercheImage4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_4);

        }
    };

    View.OnClickListener ajouterQuestion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int nextQuestionId = questionDB.getNextId("question");
            if (!questionTexte.getText().toString().equals("")) {
                if (!reponseTexte.getText().toString().equals("")) {
                    questionDB.creerQuestion(nextQuestionId, questionTexte.getText().toString(), quizzNumber, Integer.parseInt(reponseTexte.getText().toString()));
                } else {
                    questionDB.creerQuestion(nextQuestionId, questionTexte.getText().toString(), quizzNumber, 0);
                }
            }

            int nextPropositionId = questionDB.getNextId("proposition");
            // On vérifie pour chaque ImageButton si une image a été sélectionnée
            if (image1.getDrawable() != null) {
                if (picturePath1 != null) {
                    questionDB.creerProposition(nextPropositionId, picturePath1, nextQuestionId);
                    nextPropositionId++;
                }
            }
            if (image2.getDrawable() != null) {
                if (picturePath2 != null) {
                    questionDB.creerProposition(nextPropositionId, picturePath2, nextQuestionId);
                    nextPropositionId++;
                }
            }
            if (image3.getDrawable() != null) {
                if (picturePath3 != null) {
                    questionDB.creerProposition(nextPropositionId, picturePath3, nextQuestionId);
                    nextPropositionId++;
                }
            }
            if (image4.getDrawable() != null) {
                if (picturePath4 != null) {
                    questionDB.creerProposition(nextPropositionId, picturePath4, nextQuestionId);
                }
            }

            Intent intent = new Intent(AddImageQuestionActivity.this, ShowQuestionsActivity.class);
            intent.putExtra("quizzNumber", quizzNumber);
            startActivity(intent);
            finish();
        }
    };



}

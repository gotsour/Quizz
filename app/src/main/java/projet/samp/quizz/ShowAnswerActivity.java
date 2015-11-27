package projet.samp.quizz;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activité qui montre la réponse à une question
 */

public class ShowAnswerActivity extends MainActivity {

    LinearLayout layoutReponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_answer);

        // On récupère les informations envoyées par Quizz Activity
        Intent intent = getIntent();
        // La réponse
        final String value = intent.getStringExtra("reponse");
        // S'il s'agit d'une question image
        final boolean imageQuestion = intent.getBooleanExtra("imageQuestion", false);

        layoutReponse = (LinearLayout) findViewById(R.id.layoutVoirReponse);
        final TextView reponse = (TextView) findViewById(R.id.textViewReponse);

        // Si c'est une question image
        if (imageQuestion) {
            // On créer un imageView avec l'image et on l'affiche
            final ImageView img = new ImageView(ShowAnswerActivity.this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(300, 300);
            layoutParams.gravity=Gravity.CENTER_HORIZONTAL;
            img.setLayoutParams(layoutParams);
            Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(value));
            img.setBackground(d);
            layoutReponse.addView(img);
        } else {
            // Sinon on écrit le texte de la réponse
            reponse.setText(value);
        }


    }


}

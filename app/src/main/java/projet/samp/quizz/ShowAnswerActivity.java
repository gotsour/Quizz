package projet.samp.quizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by twesterm on 01/10/15.
 */
public class ShowAnswerActivity extends MainActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_answer);

        Intent intent = getIntent();
        final String value = intent.getStringExtra("reponse");

        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirmShowReponse);
        final TextView reponse = (TextView) findViewById(R.id.textViewReponse);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reponse.setText(value);
            }
        });

    }


}

package projet.samp.quizz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;

public class QuestionDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE = "create table questions (_id integer primary key autoincrement, question text not null, reponse text not null) ;";
    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db;

    public QuestionDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getCursor() {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM questions", null);
    }

    public void onCreate(SQLiteDatabase database) {
        this.db = database;
        database.execSQL(DATABASE_CREATE);
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('Le diable de Tasmanie vit dans la jungle du Br\u00e9sil.', 'faux')");
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('La sauterelle saute l \u00e9quivalent de 200 fois sa taille.', 'vrai')");
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('Les pandas hibernent.', 'faux')");
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('On trouve des dromadaires en libert\u00e9 en Australie.', 'vrai')");
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('Le papillon monarque vole plus de 4000km.', 'vrai')");
        this.db.execSQL("INSERT INTO questions (question, reponse) VALUES ('Les gorilles m\u00e2les dorment dans les arbres.', 'faux')");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void chargerLesQuestions(List<String> lcs) {
        Cursor cursor = getCursor();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(DATABASE_VERSION));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void chargerLesReponses(List<String> lcs) {
        Cursor cursor = getCursor();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(2));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void insertQuestion(String question, String reponse) {
        ContentValues values = new ContentValues(2);
        values.put("question", question);
        values.put("reponse", reponse);
        this.db.insert("questions", null, values);
    }

    public boolean supprimeQuestion(String question) {
        String[] strArr = new String[DATABASE_VERSION];
        strArr[0] = question;
        return this.db.delete("questions", " question = ?", strArr) > 0;
    }
}

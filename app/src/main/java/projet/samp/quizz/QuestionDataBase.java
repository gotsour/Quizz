package projet.samp.quizz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;

public class QuestionDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE_TABLE_QUIZZ = "create table quizz (id_quizz integer primary key autoincrement, quizzName text not null);";
    private static final String DATABASE_CREATE_TABLE_QUESTION = "create table question (id_question integer primary key autoincrement, texteQuestion text not null, id_quizz integer not null, id_reponse integer not null);";
    private static final String DATABASE_CREATE_TABLE_PROPOSITION = "create table proposition (id_proposition integer primary key autoincrement, texteProposition text not null, id_question integer not null);";

    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db;

    public QuestionDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getCursorForQuestion() {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM question", null);
    }

    public Cursor getCursorForQuizz() {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM quizz", null);
    }

    public Cursor getCursorForProposition() {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM proposition", null);
    }


    public void onCreate(SQLiteDatabase database) {
        this.db = database;
        database.execSQL(DATABASE_CREATE_TABLE_QUIZZ);
        database.execSQL(DATABASE_CREATE_TABLE_QUESTION);
        database.execSQL(DATABASE_CREATE_TABLE_PROPOSITION);

        for (int i = 0 ; i < MainActivity.quizzsList.size(); i++) {


            String quizzName = MainActivity.quizzsList.get(i).getQuizzName();
            ContentValues values = new ContentValues(2);
            values.put("id_quizz", i);
            values.put("quizzName", quizzName);
            database.insert("quizz", null, values);

            for (int j = 0 ; j < MainActivity.quizzsList.get(i).questionList.size(); j++) {

                String texteQuestion = MainActivity.quizzsList.get(i).questionList.get(j).getQuestion();
                int indiceReponse = MainActivity.quizzsList.get(i).questionList.get(j).getIndiceReponce();
                ContentValues values2 = new ContentValues(4);
                values2.put("id_question", j);
                values2.put("texteQuestion", texteQuestion);
                values2.put("id_quizz", i);
                values2.put("id_reponse", indiceReponse);
                database.insert("question", null, values2);

                for (int k = 0 ; k < MainActivity.quizzsList.get(i).questionList.get(j).propositionsList.size(); k++) {

                    String texteProposition = MainActivity.quizzsList.get(i).questionList.get(j).propositionsList.get(k).getProposition();
                    ContentValues values3 = new ContentValues(3);
                    values3.put("id_proposition", k);
                    values3.put("texteProposition", texteProposition);
                    values3.put("id_question", j);
                    database.insert("proposition", null, values3);

                }
            }
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void chargerLesQuestions(List<String> lcs) {
        Cursor cursor = getCursorForQuestion();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void chargerLesReponses(List<String> lcs) {
        Cursor cursor = getCursorForQuestion();
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
        String[] strArr = new String[1];
        strArr[0] = question;
        return this.db.delete("questions", " question = ?", strArr) > 0;
    }

    public void chargerLesQuizz(List<String> mesQuizz) {
        Cursor cursor = getCursorForQuizz();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mesQuizz.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
    }
}

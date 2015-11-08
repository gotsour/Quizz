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

    public Cursor getCursorForQuestion(int quizzNumber) {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT id_question, texteQuestion FROM question WHERE id_quizz="+quizzNumber, null);
    }

    public Cursor getCursorForQuizz() {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM quizz", null);
    }

    public Cursor getCursorForProposition(int questionNumber) {
        this.db = getWritableDatabase();
        return this.db.rawQuery("SELECT * FROM proposition where id_question="+questionNumber, null);
    }


    public void onCreate(SQLiteDatabase database) {
        this.db = database;
        database.execSQL(DATABASE_CREATE_TABLE_QUIZZ);
        database.execSQL(DATABASE_CREATE_TABLE_QUESTION);
        database.execSQL(DATABASE_CREATE_TABLE_PROPOSITION);

        database.execSQL("INSERT INTO quizz VALUES (1, 'Quizz 1')");
        database.execSQL("INSERT INTO quizz VALUES (2, 'Quizz 2')");
        database.execSQL("INSERT INTO question VALUES (1, 'Question 11', 1, 3)");
        database.execSQL("INSERT INTO question VALUES (2, 'Question 12', 1, 2)");
        database.execSQL("INSERT INTO question VALUES (3, 'Question 21', 2, 1)");
        database.execSQL("INSERT INTO proposition VALUES (1, 'Proposition 11', 1)");
        database.execSQL("INSERT INTO proposition VALUES (2, 'Proposition 12', 1)");
        database.execSQL("INSERT INTO proposition VALUES (3, 'Proposition 13', 1)");
        database.execSQL("INSERT INTO proposition VALUES (4, 'Proposition 14', 1)");
        database.execSQL("INSERT INTO proposition VALUES (5, 'Proposition 21', 2)");
        database.execSQL("INSERT INTO proposition VALUES (6, 'Proposition 22', 2)");
        database.execSQL("INSERT INTO proposition VALUES (7, 'Proposition 23', 2)");



        /*for (int i = 0 ; i < MainActivity.quizzsList.size(); i++) {

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
        }*/
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /** Méthode qui permet de charger les questions d'un quizz donné */
    public void chargerLesQuestions(List<String> lcs, int quizzNumber) {
        Cursor cursor = getCursorForQuestion(quizzNumber);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //J'ajoute successivement l'id de la question et sont texte
            lcs.add(cursor.getString(0));
            lcs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
    }

    /** Méthode qui permet de charger les propositions d'une question donnée */
    public void chargerLesReponses(List<String> lcs, int questionNumber) {
        Cursor cursor = getCursorForProposition(questionNumber);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
    }

    /** Méthode qui permet de récupérer l'indice de reponse de la question */
    public int getIndiceReponse(int questionNumber) {
        this.db = getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT id_reponse FROM question WHERE id_question="+questionNumber, null);
        if (cursor != null)
            cursor.moveToFirst();
        return Integer.parseInt(cursor.getString(0));
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

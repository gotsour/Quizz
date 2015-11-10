package projet.samp.quizz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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

        /*database.execSQL("INSERT INTO quizz VALUES (1, 'Quizz 1')");
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
        database.execSQL("INSERT INTO proposition VALUES (7, 'Proposition 23', 2)");*/
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS quizz");
        db.execSQL("DROP TABLE IF EXISTS question");
        db.execSQL("DROP TABLE IF EXISTS proposition");
        onCreate(db);
    }

    /** Méthode qui permet de charger les questions id + texte d'un quizz donné avec l'id de la question */
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

    /** Méthode qui permet de charger les questions d'un quizz donné */
    public void chargerLesQuestionsSansId(List<String> lcs, int quizzNumber) {
        Cursor cursor = getCursorForQuestion(quizzNumber);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
    }

    /** Méthode qui permet de charger les id_questions d'un quizz donné */
    public void chargerLesQuestionsId(List<String> lcs, int quizzNumber) {
        Cursor cursor = getCursorForQuestion(quizzNumber);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            lcs.add(cursor.getString(0));
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

    public boolean supprimeQuestion(String question) {
        String[] strArr = new String[1];
        strArr[0] = question;
        return this.db.delete("questions", " question = ?", strArr) > 0;
    }


    public int getNextId(String tableName) {
        String countQuery = "SELECT  * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt+1;
    }

    public int getQuizzId(String quizzName) {
        this.db = getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT id_quizz FROM quizz WHERE quizzName='" + quizzName + "'", null);
        if (cursor != null)
            cursor.moveToFirst();
        return Integer.parseInt(cursor.getString(0));
    }

    public long creerQuizz(int id_quizz, String quizzName) {
        this.db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_quizz", id_quizz);
        values.put("quizzName", quizzName);
        return db.insert("quizz", null, values);
    }

    public void creerQuestion(int id_question, String texteQuestion, int id_quizz, int indiceReponse) {
        this.db = getWritableDatabase();
        ContentValues values2 = new ContentValues(4);
        values2.put("id_question", id_question);
        values2.put("texteQuestion", texteQuestion);
        values2.put("id_quizz", id_quizz);
        values2.put("id_reponse", indiceReponse);
        this.db.insert("question", null, values2);
    }

    public void creerProposition(int id_proposition, String texteProposition, int id_question) {
        this.db = getWritableDatabase();
        ContentValues values3 = new ContentValues(3);
        values3.put("id_proposition", id_proposition);
        values3.put("texteProposition", texteProposition);
        values3.put("id_question", id_question);
        this.db.insert("proposition", null, values3);
    }

    public void chargerLesQuizz(ArrayList<HashMap<String, String>> mesQuizz) {
        Cursor cursor = getCursorForQuizz();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id_quizz", cursor.getString(0));
            map.put("quizzName", cursor.getString(1));
            mesQuizz.add(map);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public boolean supprimerQuizz(String quizzName) {
        String table = "quizz";
        int id_quizz = getQuizzId(quizzName);

        supprimerToutesLesQuestionDunQuizz(id_quizz);

        String whereClause = "quizzName" + "=?";
        String[] whereArgs = new String[] { quizzName  };
        return db.delete(table, whereClause, whereArgs) > 0;
    }

    public boolean supprimerToutesLesQuestionDunQuizz(int id_quizz) {
        ArrayList<String> listOfQuestionId = new ArrayList<>();
        chargerLesQuestionsId(listOfQuestionId, id_quizz);
        for (int i = 0 ; i < listOfQuestionId.size() ; i++) {
            supprimerProposition(listOfQuestionId.get(i));
        }

        String whereClause = "id_quizz" + "=?";
        String[] whereArgs = new String[] { String.valueOf(id_quizz) };
        return db.delete("question", whereClause, whereArgs) > 0;
    }

    private boolean supprimerProposition(String s) {
        String whereClause = "id_question" + "=?";
        String[] whereArgs = new String[] { String.valueOf(s) };
        return db.delete("proposition", whereClause, whereArgs) > 0;
    }


}

package edu.uga.cs.statecapitalsquiz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteOpenHelper for the State Capitals Quiz app. Creates and upgrades the
 * three tables used by the app: {@code states} (the quiz question bank, one
 * row per state, loaded once from the bundled CSV asset), {@code quizzes}
 * (one row per quiz attempt), and {@code quiz_questions} (the relationship
 * table linking a quiz to the 6 states it asked about, in random order, and
 * recording the user's answer to each one).
 */
public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "QuizDbHelper";

    private static final String DATABASE_NAME = "state_capitals_quiz.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STATES = "states";
    public static final String COL_STATE_ID = "_id";
    public static final String COL_STATE_NAME = "state_name";
    public static final String COL_CAPITAL = "capital";
    public static final String COL_CITY2 = "city2";
    public static final String COL_CITY3 = "city3";
    public static final String COL_STATEHOOD_YEAR = "statehood_year";

    public static final String TABLE_QUIZZES = "quizzes";
    public static final String COL_QUIZ_ID = "_id";
    public static final String COL_QUIZ_DATE = "quiz_date";
    public static final String COL_QUIZ_SCORE = "score";
    public static final String COL_QUIZ_ANSWERED_COUNT = "answered_count";
    public static final String COL_QUIZ_TOTAL_QUESTIONS = "total_questions";
    public static final String COL_QUIZ_COMPLETED = "completed";

    public static final String TABLE_QUIZ_QUESTIONS = "quiz_questions";
    public static final String COL_QQ_ID = "_id";
    public static final String COL_QQ_QUIZ_ID = "quiz_id";
    public static final String COL_QQ_STATE_ID = "state_id";
    public static final String COL_QQ_QUESTION_ORDER = "question_order";
    public static final String COL_QQ_CHOICE_1 = "choice_1";
    public static final String COL_QQ_CHOICE_2 = "choice_2";
    public static final String COL_QQ_CHOICE_3 = "choice_3";
    public static final String COL_QQ_CORRECT_INDEX = "correct_index";
    public static final String COL_QQ_ANSWERED = "answered";
    public static final String COL_QQ_SELECTED_INDEX = "selected_index";
    public static final String COL_QQ_CORRECT = "correct";

    private static final String CREATE_TABLE_STATES =
            "CREATE TABLE " + TABLE_STATES + " (" +
                    COL_STATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_STATE_NAME + " TEXT NOT NULL, " +
                    COL_CAPITAL + " TEXT NOT NULL, " +
                    COL_CITY2 + " TEXT NOT NULL, " +
                    COL_CITY3 + " TEXT NOT NULL, " +
                    COL_STATEHOOD_YEAR + " INTEGER NOT NULL)";

    private static final String CREATE_TABLE_QUIZZES =
            "CREATE TABLE " + TABLE_QUIZZES + " (" +
                    COL_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_QUIZ_DATE + " INTEGER NOT NULL, " +
                    COL_QUIZ_SCORE + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_QUIZ_ANSWERED_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_QUIZ_TOTAL_QUESTIONS + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_QUIZ_COMPLETED + " INTEGER NOT NULL DEFAULT 0)";

    private static final String CREATE_TABLE_QUIZ_QUESTIONS =
            "CREATE TABLE " + TABLE_QUIZ_QUESTIONS + " (" +
                    COL_QQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_QQ_QUIZ_ID + " INTEGER NOT NULL, " +
                    COL_QQ_STATE_ID + " INTEGER NOT NULL, " +
                    COL_QQ_QUESTION_ORDER + " INTEGER NOT NULL, " +
                    COL_QQ_CHOICE_1 + " TEXT NOT NULL, " +
                    COL_QQ_CHOICE_2 + " TEXT NOT NULL, " +
                    COL_QQ_CHOICE_3 + " TEXT NOT NULL, " +
                    COL_QQ_CORRECT_INDEX + " INTEGER NOT NULL, " +
                    COL_QQ_ANSWERED + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_QQ_SELECTED_INDEX + " INTEGER NOT NULL DEFAULT -1, " +
                    COL_QQ_CORRECT + " INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(" + COL_QQ_QUIZ_ID + ") REFERENCES " + TABLE_QUIZZES + "(" + COL_QUIZ_ID + "), " +
                    "FOREIGN KEY(" + COL_QQ_STATE_ID + ") REFERENCES " + TABLE_STATES + "(" + COL_STATE_ID + "))";

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        db.execSQL(CREATE_TABLE_STATES);
        db.execSQL(CREATE_TABLE_QUIZZES);
        db.execSQL(CREATE_TABLE_QUIZ_QUESTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES);
        onCreate(db);
    }
}

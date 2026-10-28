package edu.uga.cs.statecapitalsquiz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uga.cs.statecapitalsquiz.model.Quiz;
import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;
import edu.uga.cs.statecapitalsquiz.model.StateInfo;

/**
 * Plain (synchronous) data-access layer for the quiz database. All of the
 * methods here perform blocking SQLite I/O and must only be called from a
 * background thread, i.e. from inside an {@link edu.uga.cs.statecapitalsquiz.task.MyAsyncTask}'s
 * {@code doInBackground}.
 */
public class QuizRepository {

    private static final String CSV_ASSET_NAME = "state_capitals.csv";
    private static final int QUESTIONS_PER_QUIZ = 6;

    private final Context context;
    private final QuizDbHelper dbHelper;

    public QuizRepository(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new QuizDbHelper(this.context);
    }

    /**
     * Loads the states table from the bundled CSV asset if, and only if, the
     * table is currently empty. Safe to call on every app start.
     */
    public void initializeStatesIfNeeded() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " + QuizDbHelper.TABLE_STATES, null);
        int count = 0;
        if (countCursor.moveToFirst()) {
            count = countCursor.getInt(0);
        }
        countCursor.close();

        if (count > 0) {
            return;
        }

        try (InputStream in = context.getAssets().open(CSV_ASSET_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            db.beginTransaction();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue;
                }
                ContentValues values = new ContentValues();
                values.put(QuizDbHelper.COL_STATE_NAME, parts[0].trim());
                values.put(QuizDbHelper.COL_CAPITAL, parts[1].trim());
                values.put(QuizDbHelper.COL_CITY2, parts[2].trim());
                values.put(QuizDbHelper.COL_CITY3, parts[3].trim());
                values.put(QuizDbHelper.COL_STATEHOOD_YEAR, Integer.parseInt(parts[4].trim()));
                db.insert(QuizDbHelper.TABLE_STATES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read " + CSV_ASSET_NAME, e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Creates a new quiz by randomly selecting {@value #QUESTIONS_PER_QUIZ}
     * unique states, storing the quiz and its questions (with a randomized
     * city order per question), and returns the full in-memory quiz.
     */
    public long createNewQuiz() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<Long> stateIds = new ArrayList<>();
        Cursor idCursor = db.query(QuizDbHelper.TABLE_STATES,
                new String[]{QuizDbHelper.COL_STATE_ID}, null, null, null, null, null);
        while (idCursor.moveToNext()) {
            stateIds.add(idCursor.getLong(0));
        }
        idCursor.close();

        List<Long> chosenStateIds = pickUniqueRandom(stateIds, QUESTIONS_PER_QUIZ);

        ContentValues quizValues = new ContentValues();
        quizValues.put(QuizDbHelper.COL_QUIZ_DATE, System.currentTimeMillis());
        quizValues.put(QuizDbHelper.COL_QUIZ_SCORE, 0);
        quizValues.put(QuizDbHelper.COL_QUIZ_ANSWERED_COUNT, 0);
        quizValues.put(QuizDbHelper.COL_QUIZ_TOTAL_QUESTIONS, QUESTIONS_PER_QUIZ);
        quizValues.put(QuizDbHelper.COL_QUIZ_COMPLETED, 0);
        long quizId = db.insert(QuizDbHelper.TABLE_QUIZZES, null, quizValues);

        Random random = new Random();
        int order = 0;
        for (Long stateId : chosenStateIds) {
            StateInfo state = getState(db, stateId);

            String[] choices = {state.getCapital(), state.getCity2(), state.getCity3()};
            // Shuffle the 3 choices (Fisher-Yates) so the capital is not always first.
            for (int i = choices.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                String tmp = choices[i];
                choices[i] = choices[j];
                choices[j] = tmp;
            }
            int correctIndex = -1;
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].equals(state.getCapital())) {
                    correctIndex = i;
                    break;
                }
            }

            ContentValues qqValues = new ContentValues();
            qqValues.put(QuizDbHelper.COL_QQ_QUIZ_ID, quizId);
            qqValues.put(QuizDbHelper.COL_QQ_STATE_ID, stateId);
            qqValues.put(QuizDbHelper.COL_QQ_QUESTION_ORDER, order);
            qqValues.put(QuizDbHelper.COL_QQ_CHOICE_1, choices[0]);
            qqValues.put(QuizDbHelper.COL_QQ_CHOICE_2, choices[1]);
            qqValues.put(QuizDbHelper.COL_QQ_CHOICE_3, choices[2]);
            qqValues.put(QuizDbHelper.COL_QQ_CORRECT_INDEX, correctIndex);
            db.insert(QuizDbHelper.TABLE_QUIZ_QUESTIONS, null, qqValues);
            order++;
        }

        return quizId;
    }

    private List<Long> pickUniqueRandom(List<Long> pool, int count) {
        List<Long> copy = new ArrayList<>(pool);
        List<Long> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count && !copy.isEmpty(); i++) {
            int index = random.nextInt(copy.size());
            result.add(copy.remove(index));
        }
        return result;
    }

    private StateInfo getState(SQLiteDatabase db, long stateId) {
        Cursor cursor = db.query(QuizDbHelper.TABLE_STATES, null,
                QuizDbHelper.COL_STATE_ID + "=?", new String[]{String.valueOf(stateId)},
                null, null, null);
        StateInfo state = null;
        if (cursor.moveToFirst()) {
            state = readStateInfo(cursor);
        }
        cursor.close();
        return state;
    }

    private StateInfo readStateInfo(Cursor cursor) {
        return new StateInfo(
                cursor.getLong(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_STATE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_STATE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_CAPITAL)),
                cursor.getString(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_CITY2)),
                cursor.getString(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_CITY3)),
                cursor.getInt(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_STATEHOOD_YEAR)));
    }

    /** Loads a quiz's questions, joined with their state name/statehood year, in question order. */
    public List<QuizQuestionItem> loadQuizQuestions(long quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT qq." + QuizDbHelper.COL_QQ_ID +
                ", qq." + QuizDbHelper.COL_QQ_QUIZ_ID +
                ", qq." + QuizDbHelper.COL_QQ_STATE_ID +
                ", qq." + QuizDbHelper.COL_QQ_QUESTION_ORDER +
                ", qq." + QuizDbHelper.COL_QQ_CHOICE_1 +
                ", qq." + QuizDbHelper.COL_QQ_CHOICE_2 +
                ", qq." + QuizDbHelper.COL_QQ_CHOICE_3 +
                ", qq." + QuizDbHelper.COL_QQ_CORRECT_INDEX +
                ", qq." + QuizDbHelper.COL_QQ_ANSWERED +
                ", qq." + QuizDbHelper.COL_QQ_SELECTED_INDEX +
                ", qq." + QuizDbHelper.COL_QQ_CORRECT +
                ", s." + QuizDbHelper.COL_STATE_NAME +
                ", s." + QuizDbHelper.COL_STATEHOOD_YEAR +
                " FROM " + QuizDbHelper.TABLE_QUIZ_QUESTIONS + " qq" +
                " JOIN " + QuizDbHelper.TABLE_STATES + " s ON qq." + QuizDbHelper.COL_QQ_STATE_ID +
                " = s." + QuizDbHelper.COL_STATE_ID +
                " WHERE qq." + QuizDbHelper.COL_QQ_QUIZ_ID + " = ?" +
                " ORDER BY qq." + QuizDbHelper.COL_QQ_QUESTION_ORDER + " ASC";

        List<QuizQuestionItem> items = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quizId)});
        while (cursor.moveToNext()) {
            String[] choices = {
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            };
            items.add(new QuizQuestionItem(
                    cursor.getLong(0),
                    cursor.getLong(1),
                    cursor.getLong(2),
                    cursor.getInt(3),
                    cursor.getString(11),
                    cursor.getInt(12),
                    choices,
                    cursor.getInt(7),
                    cursor.getInt(8) != 0,
                    cursor.getInt(9),
                    cursor.getInt(10) != 0));
        }
        cursor.close();
        return items;
    }

    /** Persists the user's answer for a single quiz question. */
    public void saveAnswer(QuizQuestionItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuizDbHelper.COL_QQ_ANSWERED, item.isAnswered() ? 1 : 0);
        values.put(QuizDbHelper.COL_QQ_SELECTED_INDEX, item.getSelectedChoiceIndex());
        values.put(QuizDbHelper.COL_QQ_CORRECT, item.isCorrect() ? 1 : 0);
        db.update(QuizDbHelper.TABLE_QUIZ_QUESTIONS, values,
                QuizDbHelper.COL_QQ_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    /** Updates the quiz's running score, answered-question count, and completed flag. */
    public void updateQuizProgress(long quizId, int score, int answeredCount, boolean completed) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QuizDbHelper.COL_QUIZ_SCORE, score);
        values.put(QuizDbHelper.COL_QUIZ_ANSWERED_COUNT, answeredCount);
        values.put(QuizDbHelper.COL_QUIZ_COMPLETED, completed ? 1 : 0);
        db.update(QuizDbHelper.TABLE_QUIZZES, values,
                QuizDbHelper.COL_QUIZ_ID + "=?", new String[]{String.valueOf(quizId)});
    }

    public Quiz loadQuiz(long quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(QuizDbHelper.TABLE_QUIZZES, null,
                QuizDbHelper.COL_QUIZ_ID + "=?", new String[]{String.valueOf(quizId)},
                null, null, null);
        Quiz quiz = null;
        if (cursor.moveToFirst()) {
            quiz = readQuiz(cursor);
        }
        cursor.close();
        return quiz;
    }

    /** Returns the most recent quiz that has not yet been completed, or null if there is none. */
    public Quiz findInProgressQuiz() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(QuizDbHelper.TABLE_QUIZZES, null,
                QuizDbHelper.COL_QUIZ_COMPLETED + "=0", null, null, null,
                QuizDbHelper.COL_QUIZ_ID + " DESC", "1");
        Quiz quiz = null;
        if (cursor.moveToFirst()) {
            quiz = readQuiz(cursor);
        }
        cursor.close();
        return quiz;
    }

    /** Returns all completed quizzes, newest first. */
    public List<Quiz> loadPastResults() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Quiz> quizzes = new ArrayList<>();
        Cursor cursor = db.query(QuizDbHelper.TABLE_QUIZZES, null,
                QuizDbHelper.COL_QUIZ_COMPLETED + "=1", null, null, null,
                QuizDbHelper.COL_QUIZ_DATE + " DESC");
        while (cursor.moveToNext()) {
            quizzes.add(readQuiz(cursor));
        }
        cursor.close();
        return quizzes;
    }

    private Quiz readQuiz(Cursor cursor) {
        return new Quiz(
                cursor.getLong(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_SCORE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_ANSWERED_COUNT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_TOTAL_QUESTIONS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(QuizDbHelper.COL_QUIZ_COMPLETED)) != 0);
    }
}

package edu.uga.cs.statecapitalsquiz.task;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;

/** Updates a quiz's running score, answered-question count, and completed flag. */
public class UpdateQuizProgressTask extends MyAsyncTask<Object, Void> {

    private final QuizRepository repository;
    private final TaskCallback<Void> callback;

    public UpdateQuizProgressTask(QuizRepository repository, TaskCallback<Void> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    /** Pass (long quizId, int score, int answeredCount, boolean completed). */
    @Override
    protected Void doInBackground(Object... params) {
        long quizId = (Long) params[0];
        int score = (Integer) params[1];
        int answeredCount = (Integer) params[2];
        boolean completed = (Boolean) params[3];
        repository.updateQuizProgress(quizId, score, answeredCount, completed);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (callback != null) {
            callback.onComplete(null);
        }
    }
}

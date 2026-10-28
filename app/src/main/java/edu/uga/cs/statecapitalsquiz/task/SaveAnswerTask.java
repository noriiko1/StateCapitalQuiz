package edu.uga.cs.statecapitalsquiz.task;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;
import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;

/** Persists the user's answer to a single quiz question. */
public class SaveAnswerTask extends MyAsyncTask<QuizQuestionItem, Void> {

    private final QuizRepository repository;
    private final TaskCallback<Void> callback;

    public SaveAnswerTask(QuizRepository repository, TaskCallback<Void> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(QuizQuestionItem... params) {
        repository.saveAnswer(params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (callback != null) {
            callback.onComplete(null);
        }
    }
}

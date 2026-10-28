package edu.uga.cs.statecapitalsquiz.task;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;
import edu.uga.cs.statecapitalsquiz.model.Quiz;

/** Looks up the most recent incomplete quiz, if any, so it can be resumed. */
public class FindInProgressQuizTask extends MyAsyncTask<Void, Quiz> {

    private final QuizRepository repository;
    private final TaskCallback<Quiz> callback;

    public FindInProgressQuizTask(QuizRepository repository, TaskCallback<Quiz> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected Quiz doInBackground(Void... params) {
        return repository.findInProgressQuiz();
    }

    @Override
    protected void onPostExecute(Quiz result) {
        if (callback != null) {
            callback.onComplete(result);
        }
    }
}

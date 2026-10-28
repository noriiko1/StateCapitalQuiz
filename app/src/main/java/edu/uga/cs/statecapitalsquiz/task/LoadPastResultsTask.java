package edu.uga.cs.statecapitalsquiz.task;

import java.util.List;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;
import edu.uga.cs.statecapitalsquiz.model.Quiz;

/** Loads all completed quizzes, newest first. */
public class LoadPastResultsTask extends MyAsyncTask<Void, List<Quiz>> {

    private final QuizRepository repository;
    private final TaskCallback<List<Quiz>> callback;

    public LoadPastResultsTask(QuizRepository repository, TaskCallback<List<Quiz>> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected List<Quiz> doInBackground(Void... params) {
        return repository.loadPastResults();
    }

    @Override
    protected void onPostExecute(List<Quiz> result) {
        if (callback != null) {
            callback.onComplete(result);
        }
    }
}

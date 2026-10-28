package edu.uga.cs.statecapitalsquiz.task;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;

/** Loads the bundled CSV asset into the states table, if it hasn't been loaded yet. */
public class InitDatabaseTask extends MyAsyncTask<Void, Void> {

    private final QuizRepository repository;
    private final TaskCallback<Void> callback;

    public InitDatabaseTask(QuizRepository repository, TaskCallback<Void> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        repository.initializeStatesIfNeeded();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (callback != null) {
            callback.onComplete(null);
        }
    }
}

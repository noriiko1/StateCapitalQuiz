package edu.uga.cs.statecapitalsquiz.task;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;

/** Creates a new quiz (6 randomly chosen, unique states) and returns its new quiz id. */
public class CreateQuizTask extends MyAsyncTask<Void, Long> {

    private final QuizRepository repository;
    private final TaskCallback<Long> callback;

    public CreateQuizTask(QuizRepository repository, TaskCallback<Long> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected Long doInBackground(Void... params) {
        return repository.createNewQuiz();
    }

    @Override
    protected void onPostExecute(Long quizId) {
        if (callback != null) {
            callback.onComplete(quizId);
        }
    }
}

package edu.uga.cs.statecapitalsquiz.task;

import java.util.List;

import edu.uga.cs.statecapitalsquiz.db.QuizRepository;
import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;

/** Loads the questions (with any already-saved answers) belonging to a quiz. */
public class LoadQuizQuestionsTask extends MyAsyncTask<Long, List<QuizQuestionItem>> {

    private final QuizRepository repository;
    private final TaskCallback<List<QuizQuestionItem>> callback;

    public LoadQuizQuestionsTask(QuizRepository repository, TaskCallback<List<QuizQuestionItem>> callback) {
        this.repository = repository;
        this.callback = callback;
    }

    @Override
    protected List<QuizQuestionItem> doInBackground(Long... params) {
        return repository.loadQuizQuestions(params[0]);
    }

    @Override
    protected void onPostExecute(List<QuizQuestionItem> result) {
        if (callback != null) {
            callback.onComplete(result);
        }
    }
}

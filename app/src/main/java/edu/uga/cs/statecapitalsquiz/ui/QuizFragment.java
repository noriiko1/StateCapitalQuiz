package edu.uga.cs.statecapitalsquiz.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;
import edu.uga.cs.statecapitalsquiz.task.CreateQuizTask;
import edu.uga.cs.statecapitalsquiz.task.LoadQuizQuestionsTask;
import edu.uga.cs.statecapitalsquiz.task.SaveAnswerTask;
import edu.uga.cs.statecapitalsquiz.task.UpdateQuizProgressTask;

/**
 * Hosts a running quiz: a {@link ViewPager2} of {@link QuestionFragment} pages
 * (one per question) followed by a {@link ResultFragment} page. This fragment
 * owns the in-memory quiz state (the question list, score, and answered
 * count) and is the single point where answers are persisted to the database.
 *
 * <p>Quiz progress is saved to the database immediately after every answer,
 * so {@link #onPause()} only needs to remember which quiz is in progress;
 * {@link #onResume()} reloads the quiz data from the database, which also
 * correctly rebuilds the in-memory state after a configuration change (e.g.
 * a screen rotation) recreates this fragment.</p>
 */
public class QuizFragment extends Fragment
        implements QuestionFragment.OnAnswerChangedListener, ResultFragment.ResultDataProvider {

    private static final String TAG = "QuizFragment";
    private static final String ARG_QUIZ_ID = "arg_quiz_id";
    private static final String ARG_IS_NEW = "arg_is_new";

    /** Implemented by the hosting activity to handle quiz-complete navigation requests. */
    public interface QuizHostCallbacks {
        void onQuizFinishedNavigateHome();

        void onPlayAgainRequested();
    }

    private QuizHostCallbacks hostCallbacks;

    private long quizId = -1;
    private List<QuizQuestionItem> questions = new ArrayList<>();
    private boolean resultSaved = false;

    private ViewPager2 viewPager;
    private QuizPagerAdapter adapter;

    public static QuizFragment newInstanceForNewQuiz() {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static QuizFragment newInstanceForExistingQuiz(long quizId) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW, false);
        args.putLong(ARG_QUIZ_ID, quizId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof QuizHostCallbacks) {
            hostCallbacks = (QuizHostCallbacks) context;
        } else {
            throw new IllegalStateException("Host activity must implement QuizHostCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.view_pager_quiz);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);

        // Restore the previously-known quiz id, if this fragment instance is
        // being recreated after a configuration change.
        if (savedInstanceState != null) {
            quizId = savedInstanceState.getLong(ARG_QUIZ_ID, -1);
        } else if (getArguments() != null) {
            quizId = getArguments().getLong(ARG_QUIZ_ID, -1);
        }

        if (quizId > 0) {
            loadExistingQuiz(quizId);
        } else {
            createNewQuiz();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_QUIZ_ID, quizId);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Already-given answers are persisted immediately as they are made
        // (see onAnswerChanged), so nothing further needs to be saved here;
        // the quiz id saved in onSaveInstanceState is enough to resume.
        Log.d(TAG, "onPause: quiz " + quizId + " progress already persisted");
    }

    private void createNewQuiz() {
        MainActivity activity = (MainActivity) requireActivity();
        new CreateQuizTask(activity.getRepository(), newQuizId -> {
            if (!isAdded()) return;
            quizId = newQuizId;
            loadExistingQuiz(quizId);
        }).execute();
    }

    private void loadExistingQuiz(long id) {
        MainActivity activity = (MainActivity) requireActivity();
        new LoadQuizQuestionsTask(activity.getRepository(), loadedQuestions -> {
            if (!isAdded()) return;
            questions = loadedQuestions;
            setUpPager();
        }).execute(id);
    }

    private void setUpPager() {
        adapter = new QuizPagerAdapter(this, questions);
        viewPager.setAdapter(adapter);

        int firstUnanswered = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).isAnswered()) {
                firstUnanswered = i;
                break;
            }
            firstUnanswered = i + 1;
        }
        viewPager.setCurrentItem(Math.min(firstUnanswered, questions.size()), false);
    }

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            if (position == questions.size() && !resultSaved) {
                saveFinalResult();
            }
        }
    };

    private void saveFinalResult() {
        resultSaved = true;
        int score = computeScore();
        MainActivity activity = (MainActivity) requireActivity();
        new UpdateQuizProgressTask(activity.getRepository(), result ->
                Log.d(TAG, "Quiz " + quizId + " completed with score " + score))
                .execute(quizId, score, questions.size(), true);
    }

    private int computeScore() {
        int score = 0;
        for (QuizQuestionItem item : questions) {
            if (item.isCorrect()) {
                score++;
            }
        }
        return score;
    }

    private int computeAnsweredCount() {
        int count = 0;
        for (QuizQuestionItem item : questions) {
            if (item.isAnswered()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onAnswerChanged(int questionPosition, int selectedChoiceIndex) {
        QuizQuestionItem item = questions.get(questionPosition);
        item.setAnswer(selectedChoiceIndex);

        MainActivity activity = (MainActivity) requireActivity();
        new SaveAnswerTask(activity.getRepository(), result -> { }).execute(item);
        new UpdateQuizProgressTask(activity.getRepository(), result -> { })
                .execute(quizId, computeScore(), computeAnsweredCount(), false);
    }

    @Override
    public int getFinalScore() {
        return computeScore();
    }

    @Override
    public int getTotalQuestions() {
        return questions.size();
    }

    public void navigateHome() {
        hostCallbacks.onQuizFinishedNavigateHome();
    }

    public void playAgain() {
        hostCallbacks.onPlayAgainRequested();
    }
}

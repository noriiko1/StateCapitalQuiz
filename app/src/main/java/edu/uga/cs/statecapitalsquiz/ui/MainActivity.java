package edu.uga.cs.statecapitalsquiz.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.db.QuizRepository;
import edu.uga.cs.statecapitalsquiz.task.InitDatabaseTask;

/**
 * The app's single activity. Hosts every screen as a fragment inside
 * {@code fragment_container}, following the single-activity architecture.
 * On startup it asynchronously loads the states CSV into the database (if
 * this is the first run) and then shows the splash screen.
 */
public class MainActivity extends AppCompatActivity
        implements SplashFragment.SplashHostCallbacks, QuizFragment.QuizHostCallbacks,
        PastResultsFragment.PastResultsHostCallbacks {

    private static final String TAG = "MainActivity";

    private QuizRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new QuizRepository(getApplicationContext());

        if (savedInstanceState == null) {
            new InitDatabaseTask(repository, result -> {
                Log.d(TAG, "States table initialized");
                showSplash();
            }).execute();
        }
    }

    public QuizRepository getRepository() {
        return repository;
    }

    private void showSplash() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, new SplashFragment())
                .commit();
    }

    @Override
    public void onStartNewQuizRequested() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment.newInstanceForNewQuiz())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResumeQuizRequested(long quizId) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment.newInstanceForExistingQuiz(quizId))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onViewPastResultsRequested() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, new PastResultsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onQuizFinishedNavigateHome() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        showSplash();
    }

    @Override
    public void onPlayAgainRequested() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        onStartNewQuizRequested();
    }

    @Override
    public void onPastResultsBackRequested() {
        getSupportFragmentManager().popBackStack();
    }
}

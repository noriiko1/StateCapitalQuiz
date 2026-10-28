package edu.uga.cs.statecapitalsquiz.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.task.FindInProgressQuizTask;

/**
 * The initial (splash) screen: explains the quiz and lets the user start a
 * new quiz, resume an interrupted one, or view past results.
 */
public class SplashFragment extends Fragment {

    /** Implemented by the hosting activity to handle splash screen navigation requests. */
    public interface SplashHostCallbacks {
        void onStartNewQuizRequested();

        void onResumeQuizRequested(long quizId);

        void onViewPastResultsRequested();
    }

    private SplashHostCallbacks hostCallbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SplashHostCallbacks) {
            hostCallbacks = (SplashHostCallbacks) context;
        } else {
            throw new IllegalStateException("Host activity must implement SplashHostCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button startQuizButton = view.findViewById(R.id.button_start_quiz);
        Button pastResultsButton = view.findViewById(R.id.button_past_results);
        Button resumeQuizButton = view.findViewById(R.id.button_resume_quiz);

        startQuizButton.setOnClickListener(v -> hostCallbacks.onStartNewQuizRequested());
        pastResultsButton.setOnClickListener(v -> hostCallbacks.onViewPastResultsRequested());

        MainActivity activity = (MainActivity) requireActivity();
        new FindInProgressQuizTask(activity.getRepository(), quiz -> {
            if (quiz != null && isAdded()) {
                resumeQuizButton.setVisibility(View.VISIBLE);
                resumeQuizButton.setOnClickListener(v -> hostCallbacks.onResumeQuizRequested(quiz.getId()));
            }
        }).execute();
    }
}

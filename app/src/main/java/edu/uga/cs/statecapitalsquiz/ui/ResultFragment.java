package edu.uga.cs.statecapitalsquiz.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.uga.cs.statecapitalsquiz.R;

/** Shown as the final page of the quiz pager once all questions have been answered. */
public class ResultFragment extends Fragment {

    /** Implemented by the parent {@link QuizFragment} to supply the final score. */
    public interface ResultDataProvider {
        int getFinalScore();

        int getTotalQuestions();
    }

    private QuizFragment quizFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent instanceof QuizFragment) {
            quizFragment = (QuizFragment) parent;
        } else {
            throw new IllegalStateException("Parent fragment must be a QuizFragment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView scoreText = view.findViewById(R.id.text_result_score);
        Button playAgainButton = view.findViewById(R.id.button_play_again);
        Button backHomeButton = view.findViewById(R.id.button_back_home);

        ResultDataProvider provider = quizFragment;
        scoreText.setText(getString(R.string.result_score_format,
                provider.getFinalScore(), provider.getTotalQuestions()));

        playAgainButton.setOnClickListener(v -> quizFragment.playAgain());
        backHomeButton.setOnClickListener(v -> quizFragment.navigateHome());
    }
}

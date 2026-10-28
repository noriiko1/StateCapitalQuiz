package edu.uga.cs.statecapitalsquiz.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;

/**
 * Supplies the pages for the quiz's {@code ViewPager2}: one
 * {@link QuestionFragment} per quiz question, followed by a single
 * {@link ResultFragment} page shown once the user swipes past the last question.
 */
public class QuizPagerAdapter extends FragmentStateAdapter {

    private final List<QuizQuestionItem> questions;

    public QuizPagerAdapter(@NonNull Fragment fragment, List<QuizQuestionItem> questions) {
        super(fragment);
        this.questions = questions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position < questions.size()) {
            QuizQuestionItem item = questions.get(position);
            return QuestionFragment.newInstance(position, questions.size(), item);
        }
        return new ResultFragment();
    }

    @Override
    public int getItemCount() {
        return questions.size() + 1;
    }
}

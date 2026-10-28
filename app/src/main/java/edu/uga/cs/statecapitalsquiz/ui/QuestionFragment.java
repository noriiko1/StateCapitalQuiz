package edu.uga.cs.statecapitalsquiz.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.model.QuizQuestionItem;

/**
 * Displays a single quiz question: the year the state joined the Union, the
 * "What is the capital of X?" prompt, and the 3 candidate cities as
 * RadioButtons. Reports the user's choice back to the parent {@link QuizFragment}
 * as soon as it is made.
 */
public class QuestionFragment extends Fragment {

    private static final String ARG_POSITION = "arg_position";
    private static final String ARG_TOTAL = "arg_total";
    private static final String ARG_STATE_NAME = "arg_state_name";
    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_CHOICE_1 = "arg_choice_1";
    private static final String ARG_CHOICE_2 = "arg_choice_2";
    private static final String ARG_CHOICE_3 = "arg_choice_3";
    private static final String ARG_SELECTED_INDEX = "arg_selected_index";

    /** Implemented by the parent {@link QuizFragment} to receive answer selections. */
    public interface OnAnswerChangedListener {
        void onAnswerChanged(int questionPosition, int selectedChoiceIndex);
    }

    private OnAnswerChangedListener listener;

    public static QuestionFragment newInstance(int position, int total, QuizQuestionItem item) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_TOTAL, total);
        args.putString(ARG_STATE_NAME, item.getStateName());
        args.putInt(ARG_YEAR, item.getStatehoodYear());
        args.putString(ARG_CHOICE_1, item.getChoices()[0]);
        args.putString(ARG_CHOICE_2, item.getChoices()[1]);
        args.putString(ARG_CHOICE_3, item.getChoices()[2]);
        args.putInt(ARG_SELECTED_INDEX, item.getSelectedChoiceIndex());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent instanceof OnAnswerChangedListener) {
            listener = (OnAnswerChangedListener) parent;
        } else {
            throw new IllegalStateException("Parent fragment must implement OnAnswerChangedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();

        int position = args.getInt(ARG_POSITION);
        int total = args.getInt(ARG_TOTAL);
        String stateName = args.getString(ARG_STATE_NAME);
        int year = args.getInt(ARG_YEAR);
        String[] choices = {
                args.getString(ARG_CHOICE_1),
                args.getString(ARG_CHOICE_2),
                args.getString(ARG_CHOICE_3)
        };
        int selectedIndex = args.getInt(ARG_SELECTED_INDEX, -1);

        TextView counterText = view.findViewById(R.id.text_question_counter);
        TextView yearText = view.findViewById(R.id.text_statehood_year);
        TextView questionText = view.findViewById(R.id.text_question);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_choices);
        RadioButton choice1 = view.findViewById(R.id.radio_choice_1);
        RadioButton choice2 = view.findViewById(R.id.radio_choice_2);
        RadioButton choice3 = view.findViewById(R.id.radio_choice_3);

        counterText.setText(getString(R.string.question_of_format, position + 1, total));
        yearText.setText(getString(R.string.statehood_format, stateName, year));
        questionText.setText(getString(R.string.question_format, stateName));

        choice1.setText(choices[0]);
        choice2.setText(choices[1]);
        choice3.setText(choices[2]);

        RadioButton[] buttons = {choice1, choice2, choice3};
        if (selectedIndex >= 0 && selectedIndex < buttons.length) {
            buttons[selectedIndex].setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selected = -1;
            if (checkedId == R.id.radio_choice_1) selected = 0;
            else if (checkedId == R.id.radio_choice_2) selected = 1;
            else if (checkedId == R.id.radio_choice_3) selected = 2;

            if (selected >= 0) {
                listener.onAnswerChanged(position, selected);
            }
        });
    }
}

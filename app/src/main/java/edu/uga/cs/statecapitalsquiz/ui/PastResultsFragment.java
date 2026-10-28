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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.task.LoadPastResultsTask;

/** Shows all of the user's completed quizzes, newest first. */
public class PastResultsFragment extends Fragment {

    /** Implemented by the hosting activity to handle navigating back from this screen. */
    public interface PastResultsHostCallbacks {
        void onPastResultsBackRequested();
    }

    private PastResultsHostCallbacks hostCallbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PastResultsHostCallbacks) {
            hostCallbacks = (PastResultsHostCallbacks) context;
        } else {
            throw new IllegalStateException("Host activity must implement PastResultsHostCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_past_results);
        TextView emptyText = view.findViewById(R.id.text_empty);
        Button backButton = view.findViewById(R.id.button_past_results_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        backButton.setOnClickListener(v -> hostCallbacks.onPastResultsBackRequested());

        MainActivity activity = (MainActivity) requireActivity();
        new LoadPastResultsTask(activity.getRepository(), quizzes -> {
            if (!isAdded()) return;
            if (quizzes.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new PastResultsAdapter(quizzes));
            }
        }).execute();
    }
}

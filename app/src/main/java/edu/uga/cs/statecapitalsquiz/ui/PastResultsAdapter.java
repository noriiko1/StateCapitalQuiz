package edu.uga.cs.statecapitalsquiz.ui;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uga.cs.statecapitalsquiz.R;
import edu.uga.cs.statecapitalsquiz.model.Quiz;

/** RecyclerView adapter that shows each past (completed) quiz's date and score. */
public class PastResultsAdapter extends RecyclerView.Adapter<PastResultsAdapter.ResultViewHolder> {

    private final List<Quiz> quizzes;

    public PastResultsAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        CharSequence dateText = DateFormat.format("MMM d, yyyy h:mm a", quiz.getQuizDateMillis());
        String text = holder.itemView.getContext().getString(R.string.past_result_format,
                dateText, quiz.getScore(), quiz.getTotalQuestions());
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_past_result);
        }
    }
}

package edu.uga.cs.statecapitalsquiz.model;

/**
 * Represents one quiz attempt: when it was taken (or started), how many of
 * its questions have been answered so far, the running score, and whether
 * the quiz has been completed.
 */
public class Quiz {

    private long id;
    private long quizDateMillis;
    private int score;
    private int answeredCount;
    private int totalQuestions;
    private boolean completed;

    public Quiz(long id, long quizDateMillis, int score, int answeredCount,
                int totalQuestions, boolean completed) {
        this.id = id;
        this.quizDateMillis = quizDateMillis;
        this.score = score;
        this.answeredCount = answeredCount;
        this.totalQuestions = totalQuestions;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public long getQuizDateMillis() {
        return quizDateMillis;
    }

    public int getScore() {
        return score;
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public boolean isCompleted() {
        return completed;
    }
}

package edu.uga.cs.statecapitalsquiz.model;

/**
 * Represents a single question that belongs to a quiz: the state being
 * asked about, the three candidate cities in randomized display order, which
 * of the three is correct, and the user's progress on this question.
 */
public class QuizQuestionItem {

    private long id;
    private long quizId;
    private long stateId;
    private int questionOrder;

    private String stateName;
    private int statehoodYear;

    /** The three candidate cities, already shuffled for display. */
    private String[] choices;
    /** Index (0-2) into {@link #choices} that holds the correct capital. */
    private int correctChoiceIndex;

    private boolean answered;
    private int selectedChoiceIndex = -1;
    private boolean correct;

    public QuizQuestionItem(long id, long quizId, long stateId, int questionOrder,
                             String stateName, int statehoodYear, String[] choices,
                             int correctChoiceIndex, boolean answered,
                             int selectedChoiceIndex, boolean correct) {
        this.id = id;
        this.quizId = quizId;
        this.stateId = stateId;
        this.questionOrder = questionOrder;
        this.stateName = stateName;
        this.statehoodYear = statehoodYear;
        this.choices = choices;
        this.correctChoiceIndex = correctChoiceIndex;
        this.answered = answered;
        this.selectedChoiceIndex = selectedChoiceIndex;
        this.correct = correct;
    }

    public long getId() {
        return id;
    }

    public long getQuizId() {
        return quizId;
    }

    public long getStateId() {
        return stateId;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public String getStateName() {
        return stateName;
    }

    public int getStatehoodYear() {
        return statehoodYear;
    }

    public String[] getChoices() {
        return choices;
    }

    public int getCorrectChoiceIndex() {
        return correctChoiceIndex;
    }

    public boolean isAnswered() {
        return answered;
    }

    public int getSelectedChoiceIndex() {
        return selectedChoiceIndex;
    }

    public boolean isCorrect() {
        return correct;
    }

    /** Records the user's choice for this question and whether it was correct. */
    public void setAnswer(int selectedChoiceIndex) {
        this.selectedChoiceIndex = selectedChoiceIndex;
        this.answered = true;
        this.correct = selectedChoiceIndex == correctChoiceIndex;
    }
}

package edu.uga.cs.statecapitalsquiz.task;

/** Invoked on the UI thread once a {@link MyAsyncTask} finishes its background work. */
public interface TaskCallback<Result> {
    void onComplete(Result result);
}

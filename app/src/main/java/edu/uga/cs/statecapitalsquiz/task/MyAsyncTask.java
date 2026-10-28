package edu.uga.cs.statecapitalsquiz.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A minimal replacement for Android's deprecated {@code android.os.AsyncTask}.
 * Subclasses override {@link #doInBackground(Object[])}, which runs on a
 * background thread pool, and {@link #onPostExecute(Object)}, which runs back
 * on the main (UI) thread once the background work completes. All of the
 * app's database I/O must go through a subclass of this class so that the
 * UI thread is never blocked by SQLite or file access.
 *
 * @param <Params> the type of the input(s) to the background task
 * @param <Result> the type of the result produced by the background task
 */
public abstract class MyAsyncTask<Params, Result> {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    /** Runs on the background thread. Perform blocking I/O here. */
    protected abstract Result doInBackground(Params... params);

    /** Runs on the UI thread with the result of {@link #doInBackground(Object[])}. */
    protected void onPostExecute(Result result) {
        // Default implementation does nothing; subclasses may override.
    }

    /** Runs on the UI thread before the background work starts. */
    protected void onPreExecute() {
        // Default implementation does nothing; subclasses may override.
    }

    @SafeVarargs
    public final void execute(Params... params) {
        onPreExecute();
        EXECUTOR.execute(() -> {
            final Result result = doInBackground(params);
            mainThreadHandler.post(() -> onPostExecute(result));
        });
    }
}

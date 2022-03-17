package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class BookLoader extends AsyncTaskLoader {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookLoader.class.getSimpleName();
    /**
     * String containing the Url
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * First method executed in the loader
     * forceLoad() initiliazes the loader
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        try {
            if (mUrl == null) {
                return null;
            }

            /**
             * Performs a network request to the string url passed.
             * Returns a List with the data of the googleBooks API.
             */
            List<Book> result = QueryUtils.fetchBooks(mUrl);
            return result;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while fetching data: " + e);
            return null;
        }
    }
}

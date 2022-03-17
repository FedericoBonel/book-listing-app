package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Constant value for our loader ID
     */
    private static final int BOOK_LOADER_ID = 1;
    /**
     * Adapter for the list of books
     */
    private BooksAdapter mAdapter;
    /**
     * URL for book data from the Google Books dataset
     */
    private String google_request_url;
    /**
     * Boolean holding state of connection
     */
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find ProgressBar
        ProgressBar mProgressCircle = findViewById(R.id.progress_circle);

        // Find a reference to the {@link ListView} in the layout
        ListView booksList = findViewById(R.id.list);

        //Sets an empty state of the list view in case of no earthquakes
        booksList.setEmptyView(findViewById(R.id.empty_state));

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BooksAdapter(this, new ArrayList<Book>());

        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check Connection
        connected(connMgr);

        // If there is a network connection initialize the loader
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            noInternet();
        }

        //Find the view of the search button and add a listener to it, when clicked replace the
        // query url.
        TextView searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check the connection
                connected(connMgr);

                if (isConnected) {
                    //Fetch the user input
                    SearchView searchView = findViewById(R.id.search_view);
                    //Add the query to the url
                    google_request_url = toGoogleQueryUrl(searchView.getQuery());

                    //Restart the loader so it downloads the new information
                    restartLoader();

                } else {
                    noInternet();
                }

            }

            ;
        });

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        booksList.setAdapter(mAdapter);
    }

    /**
     * Loader methods
     */

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        //When the loader is initialized it creates a new loader that will download and parse the
        //JSON data from google books api
        return new BookLoader(this, google_request_url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        //Find ProgressBar
        ProgressBar mProgressCircle = findViewById(R.id.progress_circle);
        //Hide the progress circle when finished loading
        mProgressCircle.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link books}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        } else {
            // Set empty state text to display "No books found." Only if there is no books
            TextView emptyStateView = findViewById(R.id.empty_state);
            emptyStateView.setText(R.string.empty_state);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /**
     * Helper method that transforms a CharSequence google books query URL
     */
    public String toGoogleQueryUrl(CharSequence userInput) {
        //Transform the CharSequence to string
        String userString = userInput.toString();
        //Replace the spaces with + so it can work in the query
        if (userString.contains(" ")) {
            userString = userString.replaceAll("\\s", "+");
        }
        //Add the query to the url
        return "https://www.googleapis.com/books/v1/volumes?q="
                + userString + "&filter=paid-ebooks&maxResults=20";
    }

    /**
     * Helper that checks the state of connection
     */
    private void connected(ConnectivityManager connectivityManager) {

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, set isConnected to true
        if (networkInfo != null && networkInfo.isConnected()) {
            isConnected = true;

        } else {
            isConnected = false;
        }
    }

    /**
     * Helper method that restarts the loader
     */
    private void restartLoader() {
        //Find the empty state view
        TextView emptyState = findViewById(R.id.empty_state);
        //Hide it
        emptyState.setVisibility(View.GONE);

        //Find progressBar
        ProgressBar mProgressCircle = findViewById(R.id.progress_circle);
        //Show the loading circle
        mProgressCircle.setVisibility(View.VISIBLE);

        //Restart the loader
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BooksActivity.this);
    }

    /**
     * Helper method that shows no internet text on empty state
     */
    private void noInternet() {
        //Find progress bar
        ProgressBar mProgressCircle = findViewById(R.id.progress_circle);
        // First, hide loading indicator so error message will be visible
        mProgressCircle.setVisibility(View.GONE);
        // Update empty state with no connection error message
        TextView indicator = findViewById(R.id.empty_state);
        indicator.setText(R.string.no_internet);
    }

}
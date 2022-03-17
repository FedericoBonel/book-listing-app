package com.example.android.booklistingapp;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from Google Books API.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google API dataset and return an {@link Book} object to represent a single earthquake.
     */
    public static List<Book> fetchBooks(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Book> books = extractBooks(jsonResponse);

        // Return the {@link Event}
        return books;
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<Book> extractBooks(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty List that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON Response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            //Extract “items”(Books) JSONArray
            JSONArray booksArray = jsonRootObject.optJSONArray("items");

            //Loop through each book in the array
            for (int i = 0; i < booksArray.length(); i++) {
                //Get the book JSONObject at position i
                JSONObject bookObject = booksArray.getJSONObject(i);

                //Get “volumeInfo” JSONObject from the bookObject
                JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");

                //Get "saleInfo" JSONObject from the bookObject
                JSONObject saleInfo = bookObject.getJSONObject("saleInfo");

                //Extract “title” for the book's title
                String title = volumeInfo.getString("title");
                //Extract "url" of the book in Google Store.
                String url = saleInfo.optString("buyLink");

                //Extract the first “author” of the book
                //As the author array is optional, assert the author variable with an empty string
                String author = "";
                JSONArray authorArray = volumeInfo.optJSONArray("authors");
                if (authorArray != null) {
                    author = authorArray.getString(0);
                }

                //Extract "description" of the book
                String description = volumeInfo.optString("description");
                //Extract the "price" of the book with the currency code attached to it
                JSONObject retailPrice = saleInfo.optJSONObject("retailPrice");

                //As retailPrice is optional, assert the price and currency values
                String price = "";
                String currency = "";
                if (retailPrice != null) {
                    price = String.valueOf(retailPrice.getDouble("amount"));
                    //Extract the currency code of the price
                    currency = retailPrice.getString("currencyCode");
                }

                // Extract the "language" of the book
                String language = volumeInfo.getString("language");

                //Extract the thumbnail for the book
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String thumbnailString = imageLinks.getString("thumbnail");
                Uri thumbnailUri = Uri.parse(thumbnailString);

                //Add earthquake to list of earthquakes
                books.add(new Book(title, url, description, price, author, language, currency, thumbnailUri));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}

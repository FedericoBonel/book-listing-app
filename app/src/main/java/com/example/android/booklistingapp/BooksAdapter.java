package com.example.android.booklistingapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BooksAdapter extends ArrayAdapter<Book> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books   A List of earthquake objects to display in a list.
     */
    public BooksAdapter(Context context, ArrayList<Book> books) {

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // The adapter is not going to use this second argument,
        // so it can be any value. Here, we used 0.
        super(context, 0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        // Get the {@link currentBook} object located at this position in the list
        Book currentBook = getItem(position);

        // Find the Layout that contains the Title
        TextView titleView = listItemView.findViewById(R.id.book_title);
        // Set the title of the current book
        titleView.setText(currentBook.getTitle());

        // Find the Layout that contains the Author
        TextView authorView = listItemView.findViewById(R.id.book_author);
        // Set the author of the current book
        // if no author found, hide the view
        String author = currentBook.getAuthor();
        if (author.isEmpty()) {
            authorView.setVisibility(View.GONE);
        } else {
            authorView.setText(author);
        }


        // Find the Layout that contains the Description
        TextView descriptionView = listItemView.findViewById(R.id.description);
        // Set the description of the current book
        // If no description found, hide the view
        String description = currentBook.getDescription();
        if (description.isEmpty()) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setText(currentBook.getDescription());
        }

        // Find the Layout that contains the language
        TextView languageView = listItemView.findViewById(R.id.book_language);
        // Set the language of the current book
        languageView.setText(currentBook.getLanguage());

        // Find the Layout that contains the price
        TextView priceView = listItemView.findViewById(R.id.price);
        // Set the price of the current book only if it has a price
        // otherwise hide the views related to pricing
        String price = currentBook.getPrice();
        String currencyCode = currentBook.getCurrencyCode();
        if (currencyCode.isEmpty() || price.isEmpty()) {
            priceView.setVisibility(View.GONE);
            TextView buyView = listItemView.findViewById(R.id.buy_text_view);
            buyView.setVisibility(View.GONE);
        } else {
            price = currentBook.getCurrencyCode() + " " + currentBook.getPrice();
            priceView.setText(price);
        }

        // Find the imageView for the thumbnail of the book
        ImageView bookThumbnail = listItemView.findViewById(R.id.book_thumbnail);
        // Get the Url from the object and set it as the image
        Uri image = currentBook.getThumbnailImage();
        // Use picasso library to show the image in the view
        Picasso.get().load(image).into(bookThumbnail);


        // Return the whole list item layout so that it can be shown in
        // the ListView.
        return listItemView;
    }

}

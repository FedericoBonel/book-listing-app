package com.example.android.booklistingapp;

import android.net.Uri;

/**
 * @link represents a book, with a title, description, price, author, language, url of the book,
 * and a picture's url.
 */
public class Book {
    //Variables definition:

    //Book's title
    private String mTitle;
    //Url of the book;
    private String mUrl;
    //Book's description
    private String mDescription;
    //Book's price
    private String mPrice;
    //Book's author
    private String mAuthor;
    //Book's language
    private String mLanguage;
    //Currency code
    private String mCurrencyCode;
    //Thumbnail's url
    private Uri mThumbnail;


    /**
     * Constructs a new Attraction object including a Contact Phone Number
     *
     * @param title       Book's title.
     * @param url         Url of the book in google store.
     * @param description Book's description.
     * @param price       Book's price in google store.
     * @param author      Book's author.
     * @param language    Book's language
     */
    public Book(String title, String url, String description, String price, String author,
                String language, String currency, Uri thumbnailImage) {
        mTitle = title;
        mUrl = url;
        mDescription = description;
        mPrice = price;
        mAuthor = author;
        mLanguage = language;
        mCurrencyCode = currency;
        mThumbnail = thumbnailImage;
    }

    //Methods

    /**
     * @return the title of the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the Url of the book
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the description of the book
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return the price of the book
     */
    public String getPrice() {
        return mPrice;
    }

    /**
     * @return book's author
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return the book's language
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * @return the currency code of the price
     */
    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    /**
     * @return the thumbnail's drawable
     */
    public Uri getThumbnailImage() {
        return mThumbnail;
    }
}


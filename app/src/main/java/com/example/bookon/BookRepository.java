package com.example.bookon;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public List<Book> getTrendingBooks() {
        List<Book> books = new ArrayList<>();

        books.add(new Book(
                "1984_id",
                "1984",
                "George Orwell",
                "placeholder",
                null,
                null
        ));

        books.add(new Book(
                "mice_id",
                "Of Mice and Men",
                "John Steinbeck",
                "placeholder",
                null,
                null
        ));

        books.add(new Book(
                "flies_id",
                "Lord of the Flies",
                "William Golding",
                "placeholder",
                null,
                null
        ));

        return books;
    }
}
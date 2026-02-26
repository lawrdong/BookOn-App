package com.example.bookon;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    //TEMPORARY (will use Google API)
    public List<Book> getTrendingBooks() {
        List<Book> books = new ArrayList<>();

        books.add(new Book("id_1984", "1984", "George Orwell", null,
                "A dystopian novel about surveillance and control."));
        books.add(new Book("id_mice", "Of Mice and Men", "John Steinbeck", null,
                "Two migrant workers pursue a dream during the Great Depression."));
        books.add(new Book("id_flies", "Lord of the Flies", "William Golding", null,
                "A group of boys struggle to survive on an uninhabited island."));

        return books;
    }
}
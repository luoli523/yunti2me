package com.luoli523.book;

public class Book {
    private String name = null;
    private String author = null;
    public Book(String name, String author) {
        this.name = name;
        this.author = author;
    }
    @Override
    public String toString() {
        return "Book {" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    //getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }
}


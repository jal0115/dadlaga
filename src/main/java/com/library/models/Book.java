package com.library.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty; 
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty author;
    private final StringProperty isbn;
    private final IntegerProperty availableQty;
    private final IntegerProperty qty;  
    
    public Book(int id, String title, String author, String isbn, int availableQty, int qty) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.isbn = new SimpleStringProperty(isbn);
        this.availableQty = new SimpleIntegerProperty(availableQty);
        this.qty = new SimpleIntegerProperty(qty);
    }

    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }

    public String getAuthor() {
        return author.get();
    }
    public StringProperty authorProperty() {
        return author;
    }

    public String getIsbn() {
        return isbn.get();
    }
    public StringProperty isbnProperty() {
        return isbn;
    }

    public int getAvailableQty() {
        return availableQty.get();
    }
    public IntegerProperty availableQtyProperty() {
        return availableQty;
    }

    public int getQty() {
        return qty.get();
    }
    public IntegerProperty qtyProperty() {
        return qty;
    }

    public int getQuantity() {
        return qty.get();
    }
    public IntegerProperty quantityProperty() {
        return qty;
    }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getTitle() + " (Үлдэгдэл: " + getAvailableQty() + ")";
    }
}

package com.library.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Rental {
    private final IntegerProperty id;
    private final StringProperty memberName;
    private final StringProperty bookTitle;
    private final StringProperty borrowDate;
    private final StringProperty dueDate;
    private final StringProperty status;

    public Rental(int id, String memberName, String bookTitle, String borrowDate, String dueDate, String status) {
        this.id         = new SimpleIntegerProperty(id);
        this.memberName = new SimpleStringProperty(memberName);
        this.bookTitle  = new SimpleStringProperty(bookTitle);
        this.borrowDate = new SimpleStringProperty(borrowDate);
        this.dueDate    = new SimpleStringProperty(dueDate);
        this.status     = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getMemberName() { return memberName.get(); }
    public StringProperty memberNameProperty() { return memberName; }

    public String getBookTitle() { return bookTitle.get(); }
    public StringProperty bookTitleProperty() { return bookTitle; }

    public String getBorrowDate() { return borrowDate.get(); }
    public StringProperty borrowDateProperty() { return borrowDate; }

    public String getDueDate() { return dueDate.get(); }
    public StringProperty dueDateProperty() { return dueDate; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}

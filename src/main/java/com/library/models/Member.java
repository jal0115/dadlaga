package com.library.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Member {
    private final IntegerProperty id;
    private final StringProperty memberName;
    private final StringProperty surname;
    private final StringProperty phone;
    private final StringProperty email;

    public Member(int id, String memberName, String surname, String phone, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.memberName = new SimpleStringProperty(memberName);
        this.surname = new SimpleStringProperty(surname);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getMemberName() { return memberName.get(); }
    public StringProperty memberNameProperty() { return memberName; }

    public String getSurname() { return surname.get(); }
    public StringProperty surnameProperty() { return surname; }

    public String getPhone() { return phone.get(); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    @Override
    public String toString() {
        return "[" + getId() + "] " + getSurname() + " " + getMemberName();
    }
}

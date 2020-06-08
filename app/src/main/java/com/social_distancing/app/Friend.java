package com.social_distancing.app;

public class Friend {
    private String FirstName;
    private String LastName;
    private String Email;

    public final String getFirstName() {
        return this.FirstName;
    }

    public final String getLastName() {
        return this.LastName;
    }

    public final String getEmail() {
        return this.Email;
    }

    public final String getFullName() {
        return this.FirstName + " " + this.LastName;
    }

    public Friend() {}

    public Friend(String FirstName, String LastName, String Email) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Email = Email;
    }
}
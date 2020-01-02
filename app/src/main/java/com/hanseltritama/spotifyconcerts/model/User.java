package com.hanseltritama.spotifyconcerts.model;

public class User {

    private String birthdate;
    private String country;
    private String display_name;
    private String email;
    public String id;

    public User(String birthdate, String country, String display_name, String email, String id) {
        this.birthdate = birthdate;
        this.country = country;
        this.display_name = display_name;
        this.email = email;
        this.id = id;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getCountry() {
        return country;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}

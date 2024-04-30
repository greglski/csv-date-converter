package com.kodilla.csvdateconverter.domain;

public class PersonWithBirthdate extends Person {

    private String birthdate;

    public PersonWithBirthdate() {
        super();
    }

    public PersonWithBirthdate(int id, String name, String familyName, String birthdate) {
        super(id, name, familyName);
        this.birthdate = birthdate;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}

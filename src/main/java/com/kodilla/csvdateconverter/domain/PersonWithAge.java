package com.kodilla.csvdateconverter.domain;

public class PersonWithAge extends Person {
    private int age;

    public PersonWithAge() {
        super();
    }

    public PersonWithAge(int id, String name, String familyName, int age) {
        super(id, name, familyName);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

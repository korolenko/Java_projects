package com.company;

public class Main {

    public static void main(String[] args) {
        Person Denis = new Person.personBuilder()
                .withName("Denis")
                .withSurname("Smith")
                .whithAge(25)
                .getPerson();
        
        Denis.getName();
        Denis.getSurName();
        Denis.getAge();
    }
}

package com.company;

//демонстрация шаблона проектирования Builder
//создаем класс Person
public class Person {
    private String Name;
    private String SurName;
    private int Age;

    //создаем геттеры, сеттеры
    public String getName() {
        System.out.println("Person.Name: " + this.Name);
        return Name;
    }

    private void setName(String name) {
        Name = name;
    }

    public String getSurName() {
        System.out.println("Person.SurName: " + this.SurName);
        return SurName;
    }

    private void setSurName(String surName) {
        SurName = surName;
    }

    public int getAge() {
        System.out.println("Person.Age: " + this.Age);
        return Age;
    }

    private void setAge(int age) {
        Age = age;
    }

    public String toString() {
        System.out.println("Переопределение метода toString()..");
        return this.Name + ", "+ this.SurName + ", "+ this.Age;
    }
    //создаем класс, в который вынесено заполнение свойств класса Person
    static class personBuilder {
        private Person person;

        personBuilder(){
            person = new Person();
        }
        public personBuilder withName(String name){
            this.person.setName(name);
            return this;
        }
        public personBuilder withSurname(String surname){
            this.person.setSurName(surname);
            return this;
        }
        public personBuilder whithAge(int age){
            this.person.setAge(age);
            return this;
        }
        public Person getPerson(){
            return this.person;
        }
    }
}

package com.company;

public class Main {

    public static void main(String[] args) {
        //Upcasting
        //переменная типа Animal ссылается на объект класса Dog
        //рассматриваем Dog в общем виде с точки зрения Animal
        Animal animal = new Dog();
        animal.saying();
        ((Dog) animal).Bark();

        //Downcasting
        Dog dog = (Dog) animal;
        dog.saying();
        dog.Bark();

        //Downcasting with error
        Animal animal1 = new Animal();
        Dog dog1 = (Dog) animal1;
        dog1.saying();
        dog1.Bark();
    }
}

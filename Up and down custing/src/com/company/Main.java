package com.company;

import java.util.ArrayList;
import java.util.List;

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
        /*Animal animal1 = new Animal();
        Dog dog1 = (Dog) animal1;
        dog1.saying();
        dog1.Bark();*/

        ////Generics
        List<Animal> animalList = new ArrayList<>();
        animalList.add(new Animal(1));
        animalList.add(new Animal(2));

        List<Dog> dogList = new ArrayList<>();
        dogList.add(new Dog());
        dogList.add(new Dog());

        showInfo(animalList);
        showInfo(dogList);
    }
    private static void showInfo(List<? extends Animal> list){
        for (Animal animal: list){
            animal.saying();
        }
    }
}

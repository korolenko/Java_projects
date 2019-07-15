package com.company;

public class Animal {
    int number;
    Animal(){}
    Animal(int number){
        this.number = number;
    }
    public String toString(){
        return  String.valueOf(number);
    }
    public void saying(){
        System.out.println("I`m an animal");
    }
}

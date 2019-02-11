package com.company;

public class Main {

    public static void main(String[] args) {
        new Thread(() -> System.out.println("Hello world!")).start();

        Node first = new Node();
        first.setElement(3);
        first.getElement();

        Node second = new Node();
        second.setElement(5);
        second.getElement();

        first.setNext(second);
        Node a = first.getNext();
        a.getElement();
        
        //Разнинца между == и equals
        // == - сравнивание выделенной памяти
        // equals сравниевает значения
        System.out.println(a==second);
        System.out.println(String.valueOf(a.getElement()).equals(String.valueOf(first.getElement())));
    }
}


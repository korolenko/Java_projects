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
        System.out.println(second.getNext());
    }
}


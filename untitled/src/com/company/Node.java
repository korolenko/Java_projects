package com.company;

public class Node {
    private int element;
    //указатель на элемент списка
    private Node next;

    //вывод содержимого текущего элемента
    public int getElement(){
        System.out.println(element);
        return element;
    }

    //установка содержимого для текущего элемента списка
    public void setElement(int e){
        element = e;
    }

    //получение указателя на следующий элемент списка
    public Node getNext() {
        return next;
    }
    //установка следующего элемента списка
    public void setNext(Node n) {
        next = n;
    }

}

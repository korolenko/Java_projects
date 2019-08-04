package com.example.passwordfeatures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 5896847825186216539L;
    private transient String userLogin;
    private String userPassword;

    User(String userLogin, String userPassword){
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }
    public void serialisePassword(){
        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("password.bin"))) {
            oos.writeObject(this.userPassword);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void getserialisePassword(){
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("password.bin"))) {
            this.userPassword = (String) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public String toString() {
        return "login: " + this.userLogin + " , password: " + this.userPassword;
    }
}

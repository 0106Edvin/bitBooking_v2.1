package models;


import com.avaje.ebean.Model;

/**
 * Created by User on 9/30/2015.
 */
public class Email extends Model {
    private String name;
    private String mail;
    private String message;

    public Email(){

    }
    public Email(String name, String mail, String message) {
        this.name = name;
        this.mail = mail;
        this.message = message;
    }

    public String toString() {
        return String.format("%s,%s,%s", name, mail, message);
    }


}

package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

import helpers.*;

/**
 * Model of App_User. App_User is a person who sign up into database on bitBooking.ba web page
 * and has permissions depending on type of user
 * Created by ajla on 9/20/15.
 */
@Entity
public class AppUser extends Model {
    public static Finder<String, AppUser> finder = new Finder<>(AppUser.class);

    /*
     * App_User attributes
     */
    @Id
    public Integer id;

    @Constraints.Required(message = "Please input your first name")
    @Constraints.MinLength(value = 2, message = "First name must be at least 2 letters long!")
    @Constraints.Pattern("^[a-zA-Z]+$")
    public String firstname;

    @Constraints.Required(message = "Please input your last name")
    @Constraints.MinLength(value = 2, message = "Last name must be at leastt 2 letters long!")
    @Constraints.Pattern("^[a-zA-Z]+$")
    public String lastname;

    @Constraints.Required(message = "Please input email!")
    @Column(unique = true)
    public String email;

    @Constraints.Required(message = "Please input password!")
    @Constraints.MinLength(6)
    public String password;

    @Constraints.Required
    @Constraints.MaxLength(15)
    @Constraints.Pattern(value = "\\d+", message = "Phone number can contain digits only!")
    public String phoneNumber;

    public Integer userAccessLevel = UserAccessLevel.BUYER;

    @OneToOne
    public Image profileImg;

    /**
     * Default constructor
     */
    public AppUser() {
    }

    /**
     * Constructor for creating new App_User object.
     *
     * @param firstName   - App_User's first name.
     * @param lastName    - App_User's last name.
     * @param email       - App_User's email address.
     * @param password    - App_User's password.
     * @param phoneNumber - App_User's phone number.
     */
    public AppUser(String firstName, String lastName, String email, String password, String phoneNumber,Image profileImg) {
        this.firstname = firstName;
        this.lastname = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
    }

    /**
     * Tries to authenticate user who is trying to log in.
     * Hashes entered password for entered email, and checks
     * if provided combination exists in the database.
     *
     * @param email
     * @param password
     * @return
     */
    public static AppUser authenticate(String email, String password) {

        AppUser user = finder.where().eq("email", email.toString()).findUnique();

        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Checks if user with provided email exists in database.
     * Should be used when using an email from the cookies,
     * to be sure that email from cookies exists in the database.
     *
     * @param email
     * @returns App_User object if user exists, and NULL if doesn't.
     */
    public static AppUser existsInDB(String email) {
        AppUser user = finder.where().eq("email", email.toString()).findUnique();

        if (user == null) {
            return null;
        } else {
            return user;
        }
    }

    /**
     * Hashes user password.
     */
    public void hashPass() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
    }


    /**
     * Retrieves user from the database by provided email.
     * @param email
     * @return
     */
    public static AppUser getUserByEmail(String email) {
        AppUser user = finder.where().eq("email", email).findUnique();
        return user;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", firstname, lastname);
    }

    public static List<AppUser> getUsersByUserTypeId(Integer userTypeId) {
        List<AppUser> users = finder.where().eq("userAccessLevel", userTypeId).findList();
        return users;
    }

    public static AppUser findUserById(Integer id){
        AppUser user = finder.where().eq("id",id).findUnique();
        return user;
    }
}




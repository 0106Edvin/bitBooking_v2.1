package models;

import com.avaje.ebean.Model;
import helpers.Constants;
import helpers.UserAccessLevel;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    public String firstname;

    @Constraints.Required(message = "Please input your last name")
    @Constraints.MinLength(value = 2, message = "Last name must be at leastt 2 letters long!")
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

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    public Integer userAccessLevel = UserAccessLevel.BUYER;

    @OneToOne
    public Image profileImg;

    @OneToMany(mappedBy="user")
    public List<Reservation> reservations;

    @Column(unique = true)
    public String token;
    public boolean validated = Constants.NOT_VALIDATED_USER;

    @Column(unique = true)
    public String forgottenPassToken;

    @OneToMany
    public HotelVisit hotelVisit;

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
    public AppUser(String firstName, String lastName, String email, String password, String phoneNumber,Image profileImg, List<Reservation> reservations, String token, String forgottenPassToken, HotelVisit hotelVisit) {
        this.firstname = firstName;
        this.lastname = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
        this.reservations = reservations;
        this.token = token;
        this.forgottenPassToken = forgottenPassToken;
        this.hotelVisit = hotelVisit;
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

    /**
     * Retreives user from database with provided token
     * @param token
     * @return
     */
    public static AppUser findUserByToken(String token) {
        return finder.where().eq("token", token).findUnique();
    }

    /**
     * Retreives user from database with provided token for forgotten password
     * @param forgottenPassToken
     * @return
     */
    public static AppUser findUserByForgottenPasswordToken(String forgottenPassToken) {
        return finder.where().eq("forgotten_pass_token", forgottenPassToken).findUnique();
    }

    /**
     * Validates user
     * @param user
     * @return
     */
    public static boolean validateUser(AppUser user) {
        if (user == null) {
            return false;
        }
        user.token = null;
        user.validated = true;
        user.update();
        return true;
    }

    public static Boolean sellersHotel(Hotel hotel, AppUser seller) {
        if(hotel.sellerId == seller.id){
            return true;
        }
        return false;
    }

    public static Boolean sellersHotelByRoomId(Room room, AppUser user) {
        Hotel hotel = room.hotel;
        if(user != null)
        if(hotel.sellerId == user.id) {
            return true;
        }
        return false;
    }

    public static AppUser sellersHotel2(Hotel hotel, AppUser seller) {
        if(hotel.sellerId == seller.id) {
            return seller;
        } else {
            return null;
        }
    }

    /**
     * Hashes the new password and saves it into the database.
     *
     * Clears the forgotten password token field in the database
     * @param user
     * @param newPassword
     * @return
     */
    public void updatePassword(AppUser user, String newPassword) {
            user.password = newPassword;
            user.hashPass();
            user.forgottenPassToken = null;
            user.save();
    }

    /**
     * Clears the forgotten password token field for provided user
     * @param user
     */
    public static void clearChangePasswordToken(AppUser user) {
            user.forgottenPassToken = null;
            user.save();
    }

    public static boolean sellerHaveHotel(AppUser user) {
        if (Hotel.finder.where().eq("seller_id", user.id).findRowCount() > 0) {
            return true;
        }
        return false;
    }
}




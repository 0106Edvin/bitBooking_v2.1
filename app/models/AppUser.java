package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.Constants;
import helpers.MailHelper;
import helpers.UserAccessLevel;
import org.mindrot.jbcrypt.BCrypt;
import play.Play;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "user")
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
    public AppUser(String firstName, String lastName, String email, String password, String phoneNumber, Image profileImg, List<Reservation> reservations, String token, String forgottenPassToken, HotelVisit hotelVisit) {
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

    public static List<AppUser> getAllUsers() {
        return finder.all();
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
     *
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

    public static AppUser findUserById(Integer id) {
        AppUser user = finder.where().eq("id", id).findUnique();
        return user;
    }

    /**
     * Retreives user from database with provided token
     *
     * @param token
     * @return
     */
    public static AppUser findUserByToken(String token) {
        return finder.where().eq("token", token).findUnique();
    }

    /**
     * Retreives user from database with provided token for forgotten password
     *
     * @param forgottenPassToken
     * @return
     */
    public static AppUser findUserByForgottenPasswordToken(String forgottenPassToken) {
        return finder.where().eq("forgotten_pass_token", forgottenPassToken).findUnique();
    }

    /**
     * Saves new user to database, user is checked for being null value, depending on userType,
     * seller doesn't have to authenticate email address.
     *
     * @param user     <code>AppUser</code> type value of user
     * @param userType <code>String</code> type value of user type
     * @return <code>boolean</code> type value if user is successfully saved to database, false if not
     */
    public static boolean saveNewUser(AppUser user, String userType) {
        if (user != null) {
            user.hashPass();
            user.token = UUID.randomUUID().toString();
            if (Constants.USER_SELLER.equals(userType)) {
                user.userAccessLevel = UserAccessLevel.SELLER;
                user.validated = Constants.VALIDATED_USER;
            }
            try {
                user.save();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to save user. Possible duplicate email entry.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Updates user profile with new values and adds a picture to user profile if one is selected.
     *
     * @param user         <code>AppUser</code> type value of user
     * @param password     <code>String</code> type value of user password
     * @param name         <code>String</code> type value of user name
     * @param lastname     <code>String</code> type value of user lastname
     * @param phone        <code>String</code> type value of user phone
     * @param profileImage <code>Image</code> type value of user profile image
     * @return <code>boolean</code> type value true if user profile was successfully updated, false if not
     */
    public static boolean updateUserProfile(AppUser user, String password, String name, String lastname, String phone, Image profileImage) {
        if (user != null) {
            try {
                user.firstname = name;
                user.lastname = lastname;
                user.password = password;
                user.hashPass();
                user.phoneNumber = phone;
                if (profileImage != null) {
                    user.profileImg = profileImage;
                }
                user.update();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to update user profile.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes specific user from database, user is foundby provided email.
     *
     * @param email <code>String</code> type alue of email
     * @return <code>boolean</code> type value true if user is successfully deleted, false if not
     */
    public static boolean deleteUser(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        if (user != null) {
            try {
                user.delete();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to delete user.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Changes role of user, new role is taken from html selection box.
     * User is found with provided email.
     *
     * @param email <code>String</code> type value of user email
     * @param role  <code>String</code> type value of new user role
     * @return <code>boolean</code> type value true if user is successfully updated, false if not
     */
    public static boolean changeUserRole(String email, String role) {
        AppUser user = AppUser.getUserByEmail(email);
        if (user != null) {
            if ("buyer".equals(role)) {
                user.userAccessLevel = UserAccessLevel.BUYER;
            } else if ("seller".equals(role)) {
                user.userAccessLevel = UserAccessLevel.SELLER;
            } else if ("hotelmanager".equals(role)) {
                user.userAccessLevel = UserAccessLevel.HOTEL_MANAGER;
            }
            try {
                user.update();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to change user role.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    public static boolean activateForgottenPassword(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        if (user != null) {
            try {
                user.forgottenPassToken = UUID.randomUUID().toString();
                user.update();
                // Sending Email To user
                String host = Play.application().configuration().getString("url") + "user/forgotyourpassword/" + user.forgottenPassToken;
                String cancelRequest = Play.application().configuration().getString("url") + "user/cancelpasswordchangerequest/" + user.forgottenPassToken;
                MailHelper.send(user.email, host, Constants.CHANGE_PASSWORD, cancelRequest, null, null);
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to set password reset token.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Finds all sellers in database and orders them by first name ascending.
     *
     * @return <code>List</code> type value of AppUser
     */
    public static List<AppUser> getAllSellersOrderedByFirstname() {
        return finder.where().eq("user_access_level", UserAccessLevel.SELLER).orderBy("firstname asc").findList();
    }

    /**
     * Validates user
     *
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
        if (hotel.sellerId == seller.id) {
            return true;
        }
        return false;
    }

    public static Boolean sellersHotelByRoomId(Room room, AppUser user) {
        Hotel hotel = room.hotel;
        if (user != null)
            if (hotel.sellerId == user.id) {
                return true;
            }
        return false;
    }

    public static AppUser sellersHotel2(Hotel hotel, AppUser seller) {
        if (hotel.sellerId == seller.id) {
            return seller;
        } else {
            return null;
        }
    }

    /**
     * Hashes the new password and saves it into the database.
     * <p>
     * Clears the forgotten password token field in the database
     *
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
     *
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




package models;

import com.avaje.ebean.Model;
import helpers.Constants;
import helpers.NewsletterMail;
import play.Play;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by boris on 10/17/15.
 */
@Entity
@Table(name = "newsletter")
public class Newsletter extends Model {

    public static Finder<Integer, Newsletter> finder = new Finder<>(Newsletter.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false)
    public Integer id;
    @Column(name = "email", unique = true, length = 40)
    public String email;
    @Column(name = "is_subscribed")
    public Boolean isSubscribed = Constants.NEWSLETTERS_SUBSCRIBED;
    @Column(name = "token")
    public String token = UUID.randomUUID().toString();
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    /**
     * Empty constructor for Ebean use
     */
    public Newsletter() {
        //leave empty
    }

    /**
     * Creates new Newsletter subscription in database for inputed email.
     * Email is validated before being sent to this method.
     *
     * @param email <code>String</code> type value of email
     * @param user  <code>AppUser</code> type value of user
     * @return <code>Boolean</code> type value true if newsletter is saved successfully, false if not
     */
    public static Boolean createNewNewsletter(String email, AppUser user) {
        Newsletter nl = new Newsletter();
        nl.email = email;

        if (user != null) {
            nl.createdBy = user.firstname + " " + user.lastname;
        } else {
            nl.createdBy = "Unregistered user";
        }

        try {
            nl.save();
            return true;
        } catch (PersistenceException e) {
            ErrorLogger.createNewErrorLogger("Subscribed user tried to subscribe to newsletters again.", e.getMessage());
            return false;
        }
    }

    /**
     * Sends promotion written by seller to all newsletter subscribers.
     *
     * @param hotel   <code>String</code> type value of hotel's id
     * @param title   <code>String</code> type value of promotion title
     * @param content <code>String</code> type value of promotion content
     * @return <code>Boolean</code> type value true if newsletters are send, false if not
     */
    public static Boolean sendPromotionToSubscribers(String hotel, String title, String content) {
        Hotel temp = null;
        Boolean isSent = true;
        try {
            temp = Hotel.findHotelById(Integer.parseInt(hotel));
        } catch (NumberFormatException e) {
            ErrorLogger.createNewErrorLogger("Could not parse given String into Integer hotel id.", e.getMessage());
            return false;
        }
        String host = Play.application().configuration().getString("unsubscribe");
        List<Newsletter> newsletters = Newsletter.finder.where().eq("is_subscribed", true).findList();
        for (Newsletter nl : newsletters) {
            isSent = NewsletterMail.send(nl.email, host, title, content, temp, nl.token);
        }
        return isSent;
    }

    /**
     * For given token finds user subscription and unsubscribes user from receiving newsletter emails.
     *
     * @param token <code>String</code> type value of unique token
     * @param user  <code>AppUser</code> type value of user updating subscription status
     * @return <code>Boolean</code> type value true if unsubscription is successfull, false if not
     */
    public static Boolean unsubscribeFromNewsletters(String token, AppUser user) {
        Newsletter temp = Newsletter.findByToken(token);
        if (temp != null) {
            temp.isSubscribed = Constants.NEWSLETTERS_UNSUBSCRIBED;
            if (user != null) {
                temp.updatedBy = user.firstname + " " + user.lastname;
            } else {
                temp.updatedBy = "Unregistered user";
            }
            temp.update();
            return true;
        }
        return false;
    }

    /**
     * Checks if user is already signed up for newsletters
     *
     * @param user <code>AppUser</code> type value
     * @return <code>true</code> if user is already signed up, <code>false</code> if not
     */
    public static Boolean isSignedUp(AppUser user) {
        if (finder.where().eq("email", user.email).findUnique() != null) {
            return true;
        }
        return false;
    }

    /**
     * Finds Newsletter by inputed unique token.
     *
     * @param token <code>String</code> type value of token
     * @return <code>Newsletter</code> type value
     */
    public static Newsletter findByToken(String token) {
        return finder.where().eq("token", token).findUnique();
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

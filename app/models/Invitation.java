package models;

import com.avaje.ebean.Model;
import helpers.ConfigProvider;
import helpers.Constants;
import helpers.MailHelper;
import play.Play;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by boris.tomic on 26/10/15.
 */
@Entity
@Table(name = "invitation")
public class Invitation extends Model {

    public static Finder<Integer, Invitation> finder = new Finder<>(Invitation.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false)
    public Integer id;
    @Column(name = "title", length = 2000)
    public String title;
    @Column(name = "content", columnDefinition = "TEXT")
    public String content;
    @Column(name = "token")
    public String token = UUID.randomUUID().toString();
    @Column(name = "is_active")
    public Boolean isActive = Constants.INVITATION_ACTIVE;
    @Column(name = "email", length = 50)
    public String email;
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
    public Invitation() {
        // leave empty
    }

    /**
     * Creates new invitation used to send seller email with registration link.
     *
     * @param email   <code>String</code> type value of seller email
     * @param title   <code>String</code> type value of invitation title
     * @param content <code>String</code> type value of invitation content
     * @param manager <code>AppUser</code> type value of manager
     * @return <code>true</code> if invitation is successfully created, <code>false</code> if not
     */
    public static boolean createNewInvitation(String email, String title, String content, AppUser manager) {
        Invitation temp = new Invitation();
        temp.title = title;
        temp.content = content;
        temp.email = email;
        temp.setCreatedBy(manager);
        String host = ConfigProvider.APPLICATION_URL + "register/seller/" + temp.token;
        try {
            temp.save();
            MailHelper.send(temp.email, host, Constants.REGISTER_SELLER, null, title, content);
            return true;
        } catch (PersistenceException e) {
            ErrorLogger.createNewErrorLogger("Failed to create invitation for seller registration.", e.getMessage());
            return false;
        }
    }

    /**
     * Updates specific invitation for seller to expired when seller registers to application
     *
     * @param token <code>String</code> type value of invitation token
     * @return <code>boolean</code> type value true if update is successfull, false if not
     */
    public static boolean resetInvitation(String token) {
        Invitation invitation = Invitation.finder.where().eq("token", token).findUnique();
        if (invitation != null) {
            invitation.isActive = Constants.INVITATION_EXPIRED;
            try {
                invitation.update();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to update invitation when seller tried to register.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Set's created by parameter by providing <code>AppUser</code> first and last name
     *
     * @param user <code>AppUser</code> type value
     */
    public void setCreatedBy(AppUser user) {
        createdBy = user.firstname + " " + user.lastname;
    }

    /**
     * Set's updated by parameter by providing <code>AppUser</code> first and last name
     *
     * @param user <code>AppUser</code> type value
     */
    public void setUpdatedBy(AppUser user) {
        updatedBy = user.firstname + " " + user.lastname;
    }

    /**
     * Overrided Ebean update method, used to set updateDate parameter
     */
    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

package models;

import com.avaje.ebean.Model;
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

    public static boolean createNewInvitation(String email, String title, String content, AppUser manager) {
        Invitation temp = new Invitation();
        temp.title = title;
        temp.content = content;
        temp.email = email;
        temp.setCreatedBy(manager);
        String host = Play.application().configuration().getString("url") + "register/seller/" + temp.token;
        try {
            temp.save();
            MailHelper.send(temp.email, host, Constants.REGISTER_SELLER, null, title, content);
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public void setCreatedBy(AppUser user) {
        createdBy = user.firstname + " " + user.lastname;
    }

    public void setUpdatedBy(AppUser user) {
        updatedBy = user.firstname + " " + user.lastname;
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

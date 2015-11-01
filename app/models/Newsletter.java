package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;
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
    public Boolean isSubscribed = true;
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

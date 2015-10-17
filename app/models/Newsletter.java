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
    public Newsletter(){
        //leave empty
    }

    public static Boolean isSignedUp(AppUser user) {
        if (finder.where().eq("email", user.email).findUnique() != null) {
            return true;
        }
        return false;
    }

    public static Newsletter findByToken(String token) {
        return finder.where().eq("token", token).findUnique();
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

package models;

import com.avaje.ebean.Model;
import helpers.Constants;

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
    @Column(name = "token")
    public String token = UUID.randomUUID().toString();
    @Column(name = "is_active")
    public Boolean isActive = Constants.INVITATION_ACTIVE;
    @Column(name = "email", unique = true)
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

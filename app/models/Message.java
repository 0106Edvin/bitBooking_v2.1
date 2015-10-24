package models;

import com.avaje.ebean.Model;
import helpers.Constants;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by boris on 10/24/15.
 */
@Entity
@Table(name = "message")
public class Message extends Model {

    public static Finder<Integer, Message> finder = new Finder<>(Message.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false)
    public Integer id;
    @Column(name = "title", length = 2000)
    public String title;
    @Column(name = "content", columnDefinition = "TEXT")
    public String content;
    @Column(name = "status")
    public Boolean status;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();
    @ManyToOne(cascade = CascadeType.PERSIST)
    public AppUser sender;
    @ManyToOne(cascade = CascadeType.PERSIST)
    public AppUser receiver;

    /**
     * Empty constructor for Ebean use
     */
    public Message() {
        // leave empty
    }

    public static boolean createNewMessage(String subject, String content, Integer hotelId, AppUser sender) {
        Message temp = new Message();
        temp.title = subject;
        temp.content = content;
        temp.receiver = Hotel.findUserByHotelId(hotelId);
        temp.sender = sender;
        temp.status = Constants.MESSAGE_NEW;
        temp.setCreatedBy(sender.firstname, sender.lastname);
        try {
            temp.save();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public String getShortContent() {
        try {
            return content.substring(0, 20);
        } catch (StringIndexOutOfBoundsException e) {
            return content;
        }
    }

    public String getSentDate() {
        return new SimpleDateFormat("HH:mm EEE, dd MMM yyyy").format(createDate);
    }

    /**
     * Sets created by string in format name surname of sender
     *
     * @param name    <code>String</code> type value of sender first name
     * @param surname <code>String</code> type value of sender last name
     */
    public void setCreatedBy(String name, String surname) {
        createdBy = name + " " + surname;
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

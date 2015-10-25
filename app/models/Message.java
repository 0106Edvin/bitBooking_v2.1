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
    public Boolean status = Constants.MESSAGE_NEW;
    @Column(name = "inbox_active")
    public Boolean statusIn = Constants.MESSAGE_ACTIVE;
    @Column(name = "outbox_active")
    public Boolean statusOut = Constants.MESSAGE_ACTIVE;
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

    public static boolean createNewMessageFromHotelPage(String subject, String content, Integer hotelId, AppUser sender) {
        Message temp = new Message();
        temp.title = subject;
        temp.content = content;
        temp.receiver = Hotel.findUserByHotelId(hotelId);
        temp.sender = sender;
        temp.setCreatedBy(sender);
        try {
            temp.save();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public static boolean createReplyMessage(String subject, String content, Integer receiver, AppUser sender) {
        Message temp = new Message();
        temp.title = subject;
        temp.content = content;
        temp.receiver = AppUser.findUserById(receiver);
        temp.sender = sender;
        temp.setCreatedBy(sender);
        try {
            temp.save();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public static boolean deleteMessageFromInbox(Integer messageId, AppUser user) {
        Message temp = finder.byId(messageId);
        if (temp == null) {
            return false;
        }
        temp.status = Constants.MESSAGE_READ;
        temp.statusIn = Constants.MESSAGE_DELETED;
        temp.setUpdatedBy(user);
        try {
            temp.update();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public static boolean deleteMessageFromOutbox(Integer messageId, AppUser user) {
        Message temp = finder.byId(messageId);
        if (temp == null) {
            return false;
        }
        temp.statusOut = Constants.MESSAGE_DELETED;
        temp.setUpdatedBy(user);
        try {
            temp.update();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    public static boolean clearanceToRead(Message message, AppUser user) {
        if(user.id.equals(message.receiver.id) || user.id.equals(message.sender.id)) {
            if (user.id.equals(message.receiver.id)) {
                message.status = Constants.MESSAGE_READ;
                message.setUpdatedBy(user);
                message.update();
            }
            return true;
        }
        return false;
    }

    public static Integer numberOfNewMessages(AppUser receiver) {
        return finder.where().eq("receiver", receiver).eq("status", Constants.MESSAGE_NEW).findRowCount();
    }

    public String getShortContent() {
        try {
            return content.substring(0, 80);
        } catch (StringIndexOutOfBoundsException e) {
            return content;
        }
    }

    public String getSentDate() {
        if (createDate == null) {
            return "NO DATE RECORDED";
        }
        return new SimpleDateFormat("HH:mm EEE, dd MMM yyyy").format(createDate);
    }

    /**
     * Sets created by string in format name surname of sender
     *
     * @param user <code>AppUser</code> type value of sender
     */
    public void setCreatedBy(AppUser user) {
        createdBy = user.firstname + " " + user.lastname;
    }

    /**
     * Sets updated by string in format name surname of sender
     *
     * @param user <code>AppUser</code> type value of sender
     */
    public void setUpdatedBy(AppUser user) {
        updatedBy = user.firstname + " " + user.lastname;
    }


    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

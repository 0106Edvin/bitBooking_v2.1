package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    public AppUser sender;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    public AppUser receiver;

    /**
     * Empty constructor for Ebean use
     */
    public Message() {
        // leave empty
    }

    /**
     * Creates new message when buyer engages seller. Seller is found from inputed hotel id.
     *
     * @param subject <code>String</code> type value of message subject
     * @param content <code>String</code> type value of message content
     * @param hotelId <code>Integer</code> type value of hotel id used to find seller
     * @param sender  <code>AppUser</code> type value of sender
     * @return <code>true</code> if successfully saved, <code>false</code> if not
     */
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
            ErrorLogger.createNewErrorLogger("Failed to save new message.", e.getMessage());
            return false;
        }
    }

    /**
     * Creates new message when user replies to received one and saves it to database.
     *
     * @param subject  <code>String</code> type value of message subject
     * @param content  <code>String</code> type value of message content
     * @param receiver <code>Integer</code> type value of receiver id
     * @param sender   <code>AppUser</code> type value of sender
     * @return <code>true</code> if successfully saved, <code>false</code> if not
     */
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
            ErrorLogger.createNewErrorLogger("Failed to save replied message.", e.getMessage());
            return false;
        }
    }

    /**
     * Deletes message from inbox. Message is not deleted from database but only hidden for provided user.
     * Message inbox status is set to deleted message.
     *
     * @param messageId <code>Integer</code> type value of message id
     * @param user      <code>AppUser</code> type value of user who wanted to delete message from inbox
     * @return <code>true</code> if message was deleted successfully, <code>false</code> if it wasn't
     */
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
            ErrorLogger.createNewErrorLogger("Failed to delete message from inbox.", e.getMessage());
            return false;
        }
    }

    /**
     * Deletes message from outbox. Message is not deleted from database but only hidden for provided user.
     * Message outbox status is set to deleted message.
     *
     * @param messageId <code>Integer</code> type value of message id
     * @param user      <code>AppUser</code> type value of user who wanted to delete message from outbox
     * @return <code>true</code> if message was deleted successfully, <code>false</code> if it wasn't
     */
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
            ErrorLogger.createNewErrorLogger("Failed to delete message from outbox.", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if provided user is either receiver of message or sender of message.
     * If it is he/she can open message and read it.
     * If provided user is receiver of message and message is new it's status is changed to read message.
     *
     * @param message <code>Message</code> type value of message
     * @param user    <code>AppUser</code> type value of user
     * @return <code>true</code> if user has rights to read message, <code>false</code> if it doesn't
     */
    public static boolean clearanceToRead(Message message, AppUser user) {
        if (user.id.equals(message.receiver.id) || user.id.equals(message.sender.id)) {
            if (user.id.equals(message.receiver.id) && message.status == Constants.MESSAGE_NEW) {
                message.status = Constants.MESSAGE_READ;
                message.setUpdatedBy(user);
                message.update();
            }
            return true;
        }
        return false;
    }

    /**
     * Finds number of new messages, unread ones.
     *
     * @param receiver <code>AppUser</code> type value of message receiver
     * @return <code>Integer</code> type value of number of unread messages
     */
    public static Integer numberOfNewMessages(AppUser receiver) {
        return finder.where().eq("receiver", receiver).eq("status", Constants.MESSAGE_NEW).findRowCount();
    }

    /**
     * Shortens the message in inbox and outbox so it can be shown next to title.
     * If possible message is shortenedto 80 characters, otherwise full mesage is returned.
     *
     * @return <code>String</code> type value of message content
     */
    public String getShortContent() {
        try {
            return content.substring(0, 80);
        } catch (StringIndexOutOfBoundsException e) {
            ErrorLogger.createNewErrorLogger("Message content too short to parse to 80 substring. Full message is shown.", e.getMessage());
            return content;
        }
    }

    /**
     * Formats date message was sent in following format.
     * hour:minutes dayInWeek, day month year.
     *
     * @return <code>String</code> type value od timestamp message was sent
     */
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


    /**
     * Overrided Ebean update method used to set parameter updateDate
     */
    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

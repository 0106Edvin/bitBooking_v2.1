package models;

import com.avaje.ebean.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by boris on 10/31/15.
 */
@Entity
@Table(name = "error_logger")
public class ErrorLogger extends Model {

    private static final Logger logger = LoggerFactory.getLogger(ErrorLogger.class);
    private static Finder<Integer, ErrorLogger> finder = new Finder<>(ErrorLogger.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;
    @Column(name = "custom_message", length = 500)
    private String customMessage;
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "error_date", updatable = false, columnDefinition = "datetime")
    private Date errorDate = new Date();

    /**
     * Empty constructor for Ebean use
     */
    public ErrorLogger() {
        // leave empty
    }

    /**
     * Constructor used to save new object
     *
     * @param customMessage <code>String</code> type value of custom message
     * @param errorMessage  <code>String</code> type value of logged message
     */
    public ErrorLogger(String customMessage, String errorMessage) {
        this.customMessage = customMessage;
        this.errorMessage = errorMessage;
    }

    public static void createNewErrorLogger(String customMessage, String errorMessage) {
        try {
            new ErrorLogger(customMessage, errorMessage).save();
        } catch (PersistenceException e) {
            logger.error("Saving to database failed.", e.getLocalizedMessage());
            logger.debug("Full stack trace.", e.getStackTrace());
        }
    }

    /**
     * Returns all errors in List in descending order
     *
     * @return <code>List</code> type value of ErrorLogger
     */
    public static List<ErrorLogger> getAllErrors() {
        return finder.orderBy("id desc").findList();
    }

    /**
     * Finder used to make query's in database
     *
     * @return <code>Finder</code> of <code>ErrorLogger</code> class
     */
    public static Finder<Integer, ErrorLogger> getFinder() {
        return finder;
    }

    /**
     * Formats date error was logged to string in format hours:min:sec [day, month, year]
     *
     * @return <code>String</code> type value of formatted date
     */
    public String getFormattedDate() {
        return new SimpleDateFormat("HH:mm:ss [dd.MMM.yyyy]").format(errorDate);
    }

    /**
     * Return custom message from <code>ErrorLogger</code> object
     *
     * @return <code>String</code> type value of custom message
     */
    public String getCustomMessage() {
        return customMessage;
    }

    /**
     * Return error message from <code>ErrorLogger</code> object
     *
     * @return <code>String</code> type value of error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}

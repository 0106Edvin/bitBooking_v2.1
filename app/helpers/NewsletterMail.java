package helpers;

import models.ErrorLogger;
import models.Hotel;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by boris on 10/17/15.
 */
public class NewsletterMail {

    final static Logger logger = LoggerFactory.getLogger(MailHelper.class);

    /**
     * Method is used to send promotional newsletter by seller about specific hotel.
     * First string is built from html file template, HTMLEmail is created by providing, subject,
     * from, to, message, and username and password.
     * <p>
     * If email is sent return statement true is sent back, otherwise false is sent.
     *
     * @param email   <code>String</code> type value of email to send promotion to
     * @param host    <code>String</code> type value of host address
     * @param title   <code>String</code> type value of promotion title
     * @param content <code>String</code> type value of promotion content
     * @param hotel   <code>Hotel</code> type value of hotel being promoted
     * @param token   <code>String</code> type value of unsubscription token
     * @return <code>Boolean</code> type value true if email is successfully sent, false if not
     */
    public static Boolean send(String email, String host, String title, String content, Hotel hotel, String token) {

        String builtHTMLString = readTemplate(hotel, title, content, host, token);

        try {
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject(title);
            mail.setFrom(Play.application().configuration().getString("mail.smtp.user"));
            mail.addTo(email);
            mail.setMsg(host);
            mail.setHtmlMsg(builtHTMLString);
            mail.setHostName(Play.application().configuration().getString("smtp.host"));
            mail.setStartTLSEnabled(true);
            mail.setSSLOnConnect(true);
            mail.setAuthenticator(new DefaultAuthenticator(
                    Play.application().configuration().getString("mail.smtp.user"),
                    Play.application().configuration().getString("mail.smtp.pass")
            ));
            mail.send();
            return true;
        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to send Newsletter email.", e.getMessage());
            logger.warn("Email error" + e);
            return false;
        }
    }

    /**
     * Method is used by send method above to create HTML rich string from HTML email template.
     * Template takes eleven (11) strings to format newsletter correctly. First values are taken from inputed hotel,
     * if hotel has no images, default one is applied.
     * Then reader tries to read template file into a string, if successful string is formatted with eleven parameters
     * before being returned.
     *
     * @param hotel   <code>Hotel</code> type value of hotel being promoted
     * @param title   <code>String</code> type value  of promotion title
     * @param content <code>String</code> type value of promotion content
     * @param host    <code>String</code> type value of host address
     * @param token   <code>String</code> type value of unsubscription token
     * @return <code>String</code> type value of built and formatted html rich string
     */
    private static String readTemplate(Hotel hotel, String title, String content, String host, String token) {

        String mailString = "";

        String homepage = Play.application().configuration().getString("application.host");
        String name = hotel.name;
        String country = hotel.country;
        String city = hotel.city;
        String address = hotel.location;
        String hotelPage = String.valueOf(hotel.id);

        String image = "";
        if (hotel.images.size() > 0) {
            image = hotel.images.get(0).getSize(300, 300);
        } else {
            image = "https://res.cloudinary.com/dzkq8z522/image/upload/v1445942565/uofvut1ec1fx8ogqynqa.jpg";
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Play.application().getFile("app/views/utils/newsletterMail.html")));
            while (reader.ready()) {
                mailString += reader.readLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            ErrorLogger.createNewErrorLogger("Failed to find HTML file for Newsletter email.", e.getMessage());
            logger.debug("file missing", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            ErrorLogger.createNewErrorLogger("Failed to read HTML file for Newsletter email.", e.getMessage());
            logger.debug("cant read", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return String.format(mailString, homepage, title, image, name, country, city, address, hotelPage, title, content, host + token);

    }
}

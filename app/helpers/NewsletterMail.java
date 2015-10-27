package helpers;

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

    public static void send(String email, String host, String title, String content, Hotel hotel, String token) {
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
            logger.debug("file missing", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("cant read", e.getLocalizedMessage());
            e.printStackTrace();
        }

        String s = String.format(mailString, homepage, title, image, name, country, city, address, hotelPage, title, content, host + token);

        try {
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject(title);
            mail.setFrom(Play.application().configuration().getString("mail.smtp.user"));
            mail.addTo(email);
            mail.setMsg(host);

            mail.setHtmlMsg(s);

            mail.setHostName(Play.application().configuration().getString("smtp.host"));
            mail.setStartTLSEnabled(true);
            mail.setSSLOnConnect(true);
            mail.setAuthenticator(new DefaultAuthenticator(
                    Play.application().configuration().getString("mail.smtp.user"),
                    Play.application().configuration().getString("mail.smtp.pass")
            ));
            mail.send();
        } catch (Exception e) {
            logger.warn("Email error" + e);
        }
    }
}

package helpers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

/**
 * Created by ajla.eltabari on 12/10/15.
 */
public class MailHelper {
    final static Logger logger = LoggerFactory.getLogger(MailHelper.class);
    public static void send(String email, String host) {
        try {
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject("Welcome to bitBooking");
            mail.setFrom(Play.application().configuration().getString("mail.smtp.user"));
            mail.addTo(email);
            mail.setMsg(host);
            mail.setHtmlMsg(String
                    .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> </body></html>",
                            "Thanks for signing up to bitBooking!",
                            "Please confirm your Email adress", host));
            mail.setHostName(Play.application().configuration().getString("smtp.host"));
            mail.setStartTLSEnabled(true);
            mail.setSSLOnConnect(true);
            mail.setAuthenticator(new DefaultAuthenticator(
                    Play.application().configuration().getString("mail.smtp.user"),
                    Play.application().configuration().getString("mail.smtp.pass")
            ));
            //mail.setAuthentication(Play.application().configuration().getString("mailFromPass"), Play.application().configuration().getString("mail.smtp.pass"));
            mail.send();
        } catch (Exception e) {
            logger.warn("Email error" + e);
        }
    }
}

package helpers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import helpers.Constants;

/**
 * Created by ajla.eltabari on 12/10/15.
 */
public class MailHelper {
    final static Logger logger = LoggerFactory.getLogger(MailHelper.class);
    public static void send(String email, String host, Integer type, String cancelRequest, String title, String content) {
        try {
            HtmlEmail mail = new HtmlEmail();
            if (type == Constants.REGISTER) {
                mail.setSubject("Welcome to bitBooking");
            } else if (type == Constants.CHANGE_PASSWORD) {
                mail.setSubject("bitBooking - change password");
            } else if (type.equals(Constants.REGISTER_SELLER)) {
                mail.setSubject(title);
            } else if (type == Constants.SUCCESSFUL_RESERVATION) {
                mail.setSubject("bitBooking - reservation successful");
            }
            mail.setFrom(Play.application().configuration().getString("mail.smtp.user"));
            mail.addTo(email);
            mail.setMsg(host);

            if (type == Constants.REGISTER) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> </body></html>",
                                "Thanks for signing up to bitBooking!",
                                "Please confirm your Email adress", host));
            } else if (type == Constants.CHANGE_PASSWORD) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> <p> %s </p> <p> %s </p></body></html>",
                                "You have requested to change your password.",
                                "Please confirm your request and complete your password change following this link:", host,
                                "If you did not ask for password change, please cancel this request following this link:", cancelRequest));
            } else if (type.equals(Constants.REGISTER_SELLER)) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> </body></html>",
                                "Join us at bitBooking and promote your hotel",
                                content, host));
            } else if (type == Constants.SUCCESSFUL_RESERVATION) {
                mail.setHtmlMsg(host);
            }

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

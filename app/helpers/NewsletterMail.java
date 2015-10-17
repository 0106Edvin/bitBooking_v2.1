package helpers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

/**
 * Created by boris on 10/17/15.
 */
public class NewsletterMail {

    final static Logger logger = LoggerFactory.getLogger(MailHelper.class);

    public static void send(String email, String host, String title, String content, String hotel, String token) {
        try {
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject(title);
            mail.setFrom(Play.application().configuration().getString("mail.smtp.user"));
            mail.addTo(email);
            mail.setMsg(host);
            mail.setHtmlMsg(String
                    .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> <br> <br> <p> %s </p> </body></html>",
                            title, hotel, content, "To unsubscribe from newsletters visit this link " + host + token));
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

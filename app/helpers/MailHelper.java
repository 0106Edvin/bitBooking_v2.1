package helpers;

import models.ErrorLogger;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This helper class is used for sending an email from our application.
 * We are able to send few different email messages, using HtmlEmail class.
 *
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
            } else if (type == Constants.HOTEL_CREATED) {
                mail.setSubject("bitBooking - Hotel manager has created hotel for you");
            }
            mail.setFrom(ConfigProvider.SMTP_USER);
            mail.addTo(email);
            mail.setMsg(host);

            if (type == Constants.REGISTER) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> <img src='%s'></body></html>",
                                "Thanks for signing up to bitBooking!",
                                "Please confirm your email address", host,
                                ConfigProvider.LOGO));
            } else if (type == Constants.CHANGE_PASSWORD) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> <p> %s </p> <p> %s </p> <img src='%s'></body></html>",
                                "You have requested to change your password.",
                                "Please confirm your request and complete your password change following this link:", host,
                                "If you did not ask for password change, please cancel this request following this link:", cancelRequest,
                                ConfigProvider.LOGO));
            } else if (type.equals(Constants.REGISTER_SELLER)) {
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong> <p> %s </p> <p> %s </p> <img src='%s'></body></html>",
                                "Join us at bitBooking and promote your hotel",
                                content, host,
                                ConfigProvider.LOGO));
            } else if (type == Constants.SUCCESSFUL_RESERVATION) {
                mail.setHtmlMsg(host);
            } else if (type == Constants.HOTEL_CREATED) {
                mail.setHtmlMsg(host);
            }

            mail.setHostName(ConfigProvider.SMTP_HOST);
            mail.setStartTLSEnabled(true);
            mail.setSSLOnConnect(true);
            mail.setAuthenticator(new DefaultAuthenticator(
                    ConfigProvider.SMTP_USER,
                            ConfigProvider.SMTP_PASS));
            mail.send();
        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to send email from MailHelper", e.getMessage());
            logger.warn("Email error" + e);
        }
    }
}

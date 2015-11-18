package controllers;

import models.Email;
import models.ErrorLogger;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Play;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.ws.*;
import helpers.*;
import javax.inject.Inject;

/**
 * Created by Edvin Mulabdic on 9/30/2015.
 */

public class Emails extends Controller {
    private static Form<Email> mailForm = Form.form(Email.class);

    /* This method sends email to bitBooking team (Contact us form)*/
    @Inject WSClient ws;
    public Result sendMail() {
        //taking values from input fields
        String user_name = mailForm.bindFromRequest().field("user_name").value();
        String mail = mailForm.bindFromRequest().field("mail").value();
        String subject = mailForm.bindFromRequest().field("subject").value();
        String message = mailForm.bindFromRequest().field("message").value();

        String recaptcha = mailForm.bindFromRequest().field("g-recaptcha-response").value();

        //checking if user is real person or someone want to send a spam mails
        Boolean verify = GoogleRecaptcha.verifyGoogleRecaptcha(ws, recaptcha);

        // If user is verifyed create mail and send to bitCamp team
        if (verify) {
            SimpleEmail email = new SimpleEmail();
            email.setHostName(ConfigProvider.SMTP_HOST);
            email.setSmtpPort(Integer.parseInt(ConfigProvider.SMTP_PORT));
            try {
                /*Configuring mail*/
                email.setFrom(ConfigProvider.MAIL_FROM);
                email.setAuthentication(ConfigProvider.MAIL_FROM_PASS, ConfigProvider.SMTP_PASS);
                email.setStartTLSEnabled(true);
                email.setDebug(true);
                email.addTo(ConfigProvider.SMTP_USER);
                email.setSubject(subject);
                email.setMsg(user_name + "\n" + mail + "\n\n"+subject +"\n" + message);

                email.send();
            } catch (EmailException e) {
                ErrorLogger.createNewErrorLogger("Failed to send mail from Contact form.", e.getMessage());
                e.printStackTrace();
            }
            /*If mail is sent flash appears and user is redirected to index page */
            flash("success", "Your message was sent! Thank You for contacting us!");
            return redirect(routes.Application.index());
        } else {
            flash("error", "Not validated!");
            return redirect(routes.Application.index());
        }
    }

}
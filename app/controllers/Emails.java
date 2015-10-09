package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import models.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Play;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.ws.*;
import helpers.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import views.html.*;
import javax.inject.Inject;

/**
 * Created by User on 9/30/2015.
 */

public class Emails extends Controller {
    private static Form<Email> mailForm = Form.form(Email.class);

    @Inject WSClient ws;
    public Result sendMail() {

        String user_name = mailForm.bindFromRequest().field("user_name").value();
        String mail = mailForm.bindFromRequest().field("mail").value();
        String subject = mailForm.bindFromRequest().field("subject").value();
        String message = mailForm.bindFromRequest().field("message").value();

        String recaptcha = mailForm.bindFromRequest().field("g-recaptcha-response").value();

        Boolean verify = GoogleRecaptcha.verifyGoogleRecaptcha(ws, recaptcha);

        if (verify) {
            SimpleEmail email = new SimpleEmail();
            email.setHostName(Play.application().configuration().getString("smtp.host"));
            email.setSmtpPort(587);
            try {
                email.setFrom(Play.application().configuration().getString("mailFrom"));
                email.setAuthentication(Play.application().configuration().getString("mailFromPass"), Play.application().configuration().getString("mail.smtp.pass"));
                email.setStartTLSEnabled(true);
                email.setDebug(true);
                email.addTo(Play.application().configuration().getString("mail.smtp.user"));
                email.setSubject(subject);
                email.setMsg(user_name + "\n" + mail + "\n\n"+subject +"\n" + message);

                email.send();
            } catch (EmailException e) {
                e.printStackTrace();
            }
            return redirect(routes.Application.index());
        } else {
            flash("error", "Not validated!");
            return redirect(routes.Application.index());
        }
    }

}
package controllers;


import models.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by User on 9/30/2015.
 */

public class Emails extends Controller {
    private static Form<Email> mailForm = Form.form(Email.class);

    public Result sendMail(){

        String user_name = mailForm.bindFromRequest().field("user_name").value();
        String mail = mailForm.bindFromRequest().field("mail").value();
        String subject = mailForm.bindFromRequest().field("subject").value();
        String message = mailForm.bindFromRequest().field("message").value();

        SimpleEmail email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        try {
            email.setFrom("userbooking2015@gmail.com");
            email.setAuthentication("userBooking2015", "Booking2015");
            email.setStartTLSEnabled(true);
            email.setDebug(true);
            email.addTo("bitBooking2015@gmail.com");
            email.setSubject(subject);
            email.setMsg(user_name + "\n" + mail + "\n\n"+subject +"\n" + message);

            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return redirect(routes.Application.index());

    }


}
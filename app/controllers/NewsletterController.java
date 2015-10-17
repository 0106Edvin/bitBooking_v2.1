package controllers;

import models.AppUser;
import models.Newsletter;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.PersistenceException;
import java.util.regex.Pattern;

/**
 * Created by boris on 10/17/15.
 */
public class NewsletterController extends Controller {

    /**
     * Gets value from ajax request, email is passes as parameter. If all goes well
     * newsletter is saved, otherwise a bad request is returned.
     *
     * @return ok if everything goes well, badRequest with input text if email doesn't pass
     * regex validation or empty badRequest if user is already subscribed to newsletters.
     */
    public Result signUp() {
        final Pattern pattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.field("email").value();
        if (!pattern.matcher(email).matches()) {
            return badRequest("input");
        }
        Newsletter nl = new Newsletter();
        nl.email = email;
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user != null) {
            nl.createdBy = user.firstname + " " + user.lastname;
        } else {
            nl.createdBy = "Unregistered";
        }
        try {
            nl.save();
        } catch (PersistenceException e) {
            return badRequest();
        }
        return ok();
    }

    public Result sendPromotion() {
        return ok();
    }

}

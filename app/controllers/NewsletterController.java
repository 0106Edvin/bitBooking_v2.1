package controllers;

import helpers.Authenticators;
import helpers.NewsletterMail;
import models.AppUser;
import models.Hotel;
import models.Newsletter;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.persistence.PersistenceException;
import java.util.List;
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

    /**
     * Binds hotel name, promotion title and content from request.
     * Gets string for unsubscribing from newsletter finds all newsletter subscribers that are active
     * sends binded data, subscribers email and token to <code>NewsletterMail.send()</code> method.
     *
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result sendPromotion() {
        DynamicForm form = Form.form().bindFromRequest();

        String hotel = form.field("hotel").value();
        String title = form.field("title").value();
        String content = form.field("content").value();

        Hotel temp = Hotel.findHotelById(Integer.parseInt(hotel));

        String host = Play.application().configuration().getString("unsubscribe");

        List<Newsletter> newsletters = Newsletter.finder.where().eq("is_subscribed", true).findList();
        for (Newsletter nl : newsletters) {
            NewsletterMail.send(nl.email, host, title, content, temp, nl.token);
        }
        flash("info", "Successfully sent to all subscribers.");
        return redirect(routes.Application.index());
    }

    /**
     * Takes token from unsubscribed link in email and sets status as unsubscribed.
     *
     * @param token <code>String</code> type value of registration token
     * @return index page with flash info if all goes well, otherwise flash error is used
     */
    public Result unsubscribe(String token) {
        Newsletter temp = Newsletter.findByToken(token);
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (temp != null) {
            temp.isSubscribed = false;
            if (user != null) {
                temp.createdBy = user.firstname + " " + user.lastname;
            } else {
                temp.createdBy = "Unregistered";
            }
            temp.update();
            flash("info", "Successfully unsubscribed from newsletters.");
            return redirect(routes.Application.index());
        }
        flash("error-search", "Could not find your subscription, contact our staff.");
        return redirect(routes.Application.index());
    }


}

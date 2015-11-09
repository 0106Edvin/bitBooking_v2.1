package controllers;

import helpers.Authenticators;
import helpers.CommonHelperMethods;
import models.AppUser;
import models.Newsletter;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

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
        AppUser user = AppUser.getUserByEmail(session("email"));
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.field("email").value();

        if (!CommonHelperMethods.validateEmail(email)) {
            return badRequest("input");
        }
        if (Newsletter.createNewNewsletter(email, user)) {
            return ok();
        }
        return badRequest();
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

        if (Newsletter.sendPromotionToSubscribers(hotel, title, content)) {
            flash("info", "Successfully sent to all subscribers.");
            return redirect(routes.Application.index());
        }
        flash("error-search", "Promotion could not be sent. Please contact our staff.");
        return redirect(routes.Application.index());
    }

    /**
     * Takes token from unsubscribed link in email and sets status as unsubscribed.
     *
     * @param token <code>String</code> type value of registration token
     * @return index page with flash info if all goes well, otherwise flash error is used
     */
    public Result unsubscribe(String token) {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (Newsletter.unsubscribeFromNewsletters(token, user)) {
            flash("info", "Successfully unsubscribed from newsletters.");
            return redirect(routes.Application.index());
        }
        flash("error-search", "Could not find your subscription, contact our staff.");
        return redirect(routes.Application.index());
    }


}

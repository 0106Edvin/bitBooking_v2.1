package controllers;

import helpers.Authenticators;
import helpers.SessionsAndCookies;
import models.*;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.list;

import javax.persistence.PersistenceException;
import java.util.List;

public class Application extends Controller {

    /**
     * Opens a front page with list of hotels, and update SiteStats with information
     * about visits to home page
     *
     * @return ok and renders list of hotels
     */
    public Result index() {
        String ipAddress = request().remoteAddress();
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
        SiteStats.createNewStats(temp, ipAddress);

        List<Hotel> hotels = Hotel.hotelsForHomepage();
        return ok(list.render(hotels));
    }

    /**
     * Opens a page with message that payment on pay pal is rejected
     *
     * @return ok
     */
    public Result reject() {
        return ok(views.html.user.rejectPayment.render());
    }

    /**
     * Opens a page with message that payment on pay pal is successful
     *
     * @return ok
     */
    public Result success() {
        return ok(views.html.user.successfulPayment.render());
    }

    /**
     * Renders all FAQ for public users
     *
     * @return ok
     */
    public Result showFAQ() {
        List<Question> q = Question.getAllQuestions();
        return ok(views.html.user.userFAQ.render(q));
    }

    /**
     * Renders view with all FAQ to sort and search
     *
     * @return ok
     */
    public Result searchFAQ() {
        List<Question> q = Question.getAllQuestions();
        return ok(views.html.user.searchFAQ.render(q));
    }

    /**
     * Renders all errors for admin to see
     *
     * @return ok
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result seeErrors() {
        List<ErrorLogger> errors = ErrorLogger.getAllErrors();
        return ok(views.html.admin.errors.render(errors));
    }
}
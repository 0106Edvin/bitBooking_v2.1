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
    /*Opens a front page with list of hotels*/
    public Result index() {
        String ipAddress = request().remoteAddress();
        SiteStats tempStat = SiteStats.finder.where().eq("ip_address", ipAddress).findUnique();
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
        //site stats
        SiteStats stats = new SiteStats();
        stats.ipAddress = ipAddress;
        if (temp != null) {
            stats.setCreatedBy(temp.firstname, temp.lastname);
            if (tempStat != null) {
                tempStat.setUpdatedBy(temp.firstname, temp.lastname);
            }
        } else {
            stats.createdBy = "Anonymous user";
            if (tempStat != null) {
                tempStat.updatedBy = "Anonymous user";
            }
        }
        try {
            stats.save();
        } catch (PersistenceException e) {
            if(tempStat != null) {
                tempStat.update();
            }
        }
        List<Hotel> hotels = Hotel.hotelsForHomepage();
        return ok(list.render(hotels));
    }
    /*Opens a page with message that payment on pay pal is rejected*/
    public Result reject(){
        return ok(views.html.user.rejectPayment.render());
    }
    /*Opens a page with message that payment on pay pal is successful*/
    public Result success(){
        return ok(views.html.user.successfulPayment.render());
    }

    public Result showFAQ() {
        List<Question> q = Question.finder.all();
        return ok(views.html.user.userFAQ.render(q));
    }

    /*Returns list of FAQ*/
    public Result searchFAQ() {
        List<Question> q = Question.finder.all();
        return ok(views.html.user.searchFAQ.render(q));
    }

    /*Allows admin to see all errors that appears on page*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result seeErrors() {
        List<ErrorLogger> errors = ErrorLogger.getAllErrors();
        return ok(views.html.admin.errors.render(errors));
    }
}

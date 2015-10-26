package controllers;

import helpers.SessionsAndCookies;
import models.AppUser;
import models.Hotel;
import models.Question;
import models.SiteStats;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.list;

import javax.persistence.PersistenceException;
import java.util.List;

public class Application extends Controller {

    public Result index() {
        String ipAddress = request().remoteAddress();
        SiteStats tempStat = SiteStats.finder.where().eq("ip_address", ipAddress).findUnique();
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
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

    public Result see(){
        return ok(views.html.user.rejectPayment.render());
    }

    public Result see1(){
        return ok(views.html.user.successfulPayment.render());
    }

    public Result showFAQ() {
        List<Question> q = Question.finder.all();
        return ok(views.html.user.userFAQ.render(q));
    }

}

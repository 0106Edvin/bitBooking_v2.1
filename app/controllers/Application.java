package controllers;

import helpers.SessionsAndCookies;
import models.AppUser;
import models.Hotel;
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
            stats.createdBy = "Anonimous user";
            if (tempStat != null) {
                tempStat.updatedBy = "Anonimous user";
            }
        }
        try {
            stats.save();
        } catch (PersistenceException e) {
            tempStat.update();
        }
        List<Hotel> hotels = Hotel.finder.all();
        return ok(list.render(hotels));
    }



}

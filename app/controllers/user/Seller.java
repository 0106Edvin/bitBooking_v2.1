package controllers.user;

import helpers.SessionsAndCookies;
import models.SiteStats;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
public class Seller extends Controller {

    public Result statistics() {
        return ok(views.html.seller.statistic.render(SessionsAndCookies.getCurrentUser(ctx())));
    }

    public Result uniqueIndexVisits() {
        return ok(String.valueOf(SiteStats.getUniquePageVisits()));
    }

    public Result overallIndexVisits() {
        return ok(String.valueOf(SiteStats.getTotalOfPageVisits()));
    }
}

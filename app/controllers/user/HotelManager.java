package controllers.user;

import helpers.Authenticators;
import models.SiteStats;
import play.mvc.Security;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
//@Security.Authenticated(Authenticators.HotelManagerFilter.class)
public class HotelManager extends Controller {

    public Result statistics() {
        SiteStats stats = new SiteStats();
        return ok(views.html.manager.statistic.render(stats));
    }
}

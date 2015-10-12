package controllers.user;

import helpers.SessionsAndCookies;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
public class Seller extends Controller {

    public Result statistics() {
        return ok(views.html.seller.statistic.render(SessionsAndCookies.getCurrentUser(ctx())));
    }
}

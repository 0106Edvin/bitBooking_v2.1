package controllers.user;

import helpers.Authenticators;
import helpers.SessionsAndCookies;
import it.innove.play.pdf.PdfGenerator;
import models.AppUser;
import models.Hotel;
import models.SiteStats;
import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
@Security.Authenticated(Authenticators.SellerFilter.class)
public class Seller extends Controller {

    @Inject
    public PdfGenerator pdfGenerator;

    public Result statistics() {
        return ok(views.html.seller.statistic.render(SessionsAndCookies.getCurrentUser(ctx())));
    }

    public Result pdf() {
        return pdfGenerator.ok(views.html.seller.statsToPDF.render(SessionsAndCookies.getCurrentUser(ctx())), Configuration.root().getString("application.host"));
    }

    public Result uniqueIndexVisits() {
        return ok(String.valueOf(SiteStats.getUniquePageVisits()));
    }

    public Result overallIndexVisits() {
        return ok(String.valueOf(SiteStats.getTotalOfPageVisits()));
    }

    public Result createPromotion() {
        AppUser seller = AppUser.getUserByEmail(session("email"));
        List<Hotel> hotels = Hotel.finder.where().eq("seller_id", seller.id).findList();
        return ok(views.html.seller.createPromotion.render(hotels));
    }

}

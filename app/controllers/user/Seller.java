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

    /**
     * Renders statistics for seller, sends current user to view so it can be used in static methods.
     *
     * @return ok and renders stats for seller
     */
    public Result statistics() {
        return ok(views.html.seller.statistic.render(SessionsAndCookies.getCurrentUser(ctx())));
    }

    /**
     * Renders statsToPDF file into PDF file, PdfGenerator is used to render.
     *
     * @return ok and generates stats in PDF format
     */
    public Result pdf() {
        return pdfGenerator.ok(views.html.seller.statsToPDF.render(SessionsAndCookies.getCurrentUser(ctx())), Configuration.root().getString("application.host"));
    }

    /**
     * Method is used for ajax request by script numberOfUniqueVisits, method is called every 3.5 seconds.
     *
     * @return ok and returns number of unique visitors
     */
    public Result uniqueIndexVisits() {
        return ok(String.valueOf(SiteStats.getUniquePageVisits()));
    }

    /**
     * Method is used for ajax request by script numberOfOverallVisits, method is called every 3.5 seconds.
     *
     * @return ok and returns number of overall visitors
     */
    public Result overallIndexVisits() {
        return ok(String.valueOf(SiteStats.getTotalOfPageVisits()));
    }

    /**
     * Renders wiew for seller so he/she can create promotion for hotel.
     * List of hotels is sent sorted by hotel name in ascending order.
     *
     * @return ok and renders view for creating promotion
     */
    public Result createPromotion() {
        AppUser seller = AppUser.getUserByEmail(session("email"));
        List<Hotel> hotels = Hotel.getHotelsBySellerAndSortedByNameAscending(seller);
        return ok(views.html.seller.createPromotion.render(hotels));
    }

}
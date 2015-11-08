package helpers;

import models.AppUser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;
import views.html.user.profilePage;

import java.util.logging.Logger;


/**
 * This helper contains static classes that extend Security.Authenticator class
 * from Play. It allows us to create filters, that will provide us security on
 * methods level. If you put security annotation above particular method
 * (example: @Security.Authenticated(Authenticators.Filter.class)) you will be able
 * to control who can access to that method (or that block of code.
 *
 * Created by ajla.eltabari on 22/09/15.
 */
public class Authenticators {

    /**
     * Filter for users with Administrator rights.
     * Should be used with methods that only administrator
     * can run or access.
     */
    public static class AdminFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.ADMIN)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Filter for users with Hotel Manager rights.
     * Should be used with methods that only hotel manager
     * can run or access.
     */
    public static class HotelManagerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.HOTEL_MANAGER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Filter for users with Seller rights.
     * Should be used with methods that only seller
     * can run or access.
     */
    public static class SellerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.SELLER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Filter for users with Buyer rights.
     * Should be used with methods that only buyer
     * can run or access.
     */
    public static class BuyerFilter extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.BUYER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Filter for logged users.
     * Should be used with methods that only logged
     * users can run or access.
     */
    public static class isUserLogged extends Security.Authenticator {

        AppUser user = null;

        @Override
        public String getUsername(Http.Context ctx) {
            if (!ctx.session().containsKey("email"))
                return null;
            String email = ctx.session().get("email");
            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.validated == Constants.VALIDATED_USER) {
                user = u;
                return u.email;
            } else {
                return null;
            }

        }

        @Override
        public Result onUnauthorized(Http.Context ctx) {
            return ok(profilePage.render(user));
        }
    }

}
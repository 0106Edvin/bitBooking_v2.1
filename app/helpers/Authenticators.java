package helpers;

import models.AppUser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;


/**
 * Created by ajla.eltabari on 22/09/15.
 */
public class Authenticators {

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

}
package helpers;

import models.AppUser;
import play.api.mvc.Result;
import play.mvc.Security;

/**
 * Created by ajla.eltabari on 22/09/15.
 */
public class Authenticators {

    public static class AdminFilter extends Security.Authenticator {

        @Override
        public String getUsername(String email) {

            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.ADMIN)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(String email) {
            return redirect(routes.Application.index());
        }
    }

    public static class HotelManagerFilter extends Security.Authenticator {

        @Override
        public String getUsername(String email) {

            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.HOTEL_MANAGER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(String email) {
            return redirect(routes.Application.index());
        }
    }

    public static class SellerFilter extends Security.Authenticator {

        @Override
        public String getUsername(String email) {

            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.SELLER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(String email) {
            return redirect(routes.Application.index());
        }
    }

    public static class BuyerFilter extends Security.Authenticator {

        @Override
        public String getUsername(String email) {

            AppUser u = AppUser.getUserByEmail(email);
            if (u != null && u.userAccessLevel == UserAccessLevel.BUYER)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(String email) {
            return redirect(routes.Application.index());
        }
    }
}

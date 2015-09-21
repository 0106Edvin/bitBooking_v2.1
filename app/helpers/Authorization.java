package helpers;

import controllers.routes;
import models.AppUser;
import models.user.Role;
import models.user.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import utility.UserConstants;

import static play.mvc.Controller.flash;
import static play.mvc.Controller.request;

/**
 * Created by ajla on 9/21/15.
 */
public class Authorization {

    public static class Admin extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context context) {
            models.AppUser user = AppUser.getUserByEmail(request().cookie("email").value());
            if (user == null)
                return null;

            if (user.userAccessLevel == UserAccessLevel.ADMIN)
                return user.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context context) {
            return Results.redirect(routes.Users.registerUser());
        }
    }

    public static class User extends Security.Authenticator {

        @Override
        public String getUsername(Http.Context context) {
            models.User u = SessionHelper.getCurrentUser();
            if (u != null)
                return u.email;
            return null;
        }

        @Override
        public Result onUnauthorized(Http.Context context) {
            return redirect(routes.UsersController.loginView());
        }
    }
}
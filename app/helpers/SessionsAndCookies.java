package helpers;

import models.AppUser;
import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import models.AppUser;

import static play.mvc.Controller.response;
import static play.mvc.Controller.session;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
public class SessionsAndCookies extends Security.Authenticator {

    /**
     * Stores user data to the cookies.
     * @param user
     */
    public static void setCookies(AppUser user) {
        response().setCookie("email", user.email);

        String firstname = "";
        String[] name = user.firstname.split(" ");

        for (String s : name) {
            firstname += s;
        }

        response().setCookie("name", firstname);
        response().setCookie("userAccessLevel", user.userAccessLevel.toString());
        response().setCookie("userId", user.id.toString());
    }

    /**
     * Clears user data from the cookies.
     * Should be used within logout function.
     */
    public static void clearCookies() {
        response().discardCookie("email");
        response().discardCookie("name");
        response().discardCookie("userAccessLevel");
        response().discardCookie("userId");
    }

    /**
     * Stores user data to the session.
     * @param user
     */
    public static void setUserSessionSata(AppUser user) {
        session("email", user.email);

        String firstname = "";
        String[] name = user.firstname.split(" ");

        for (String s : name) {
            firstname += s;
        }

        session("name", firstname);
        session("userAccessLevel", user.userAccessLevel.toString());
        session("userId", user.id.toString());
    }

    /**
     * Clears user data from the session.
     * Should be used within logout function.
     */
    public static void clearUserSessionData() {
        session().clear();
    }


    public static AppUser getCurrentUser(Context ctx) {
        String email = ctx.session().get("email");
        if(email == null)
            return null;
        return AppUser.getUserByEmail(email);
    }

    @Override
    public Result onUnauthorized(Context ctx){
        return redirect(routes.Application.index());
    }
}

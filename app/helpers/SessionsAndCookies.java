package helpers;

import models.AppUser;

import static play.mvc.Controller.response;

/**
 * Created by ajla.eltabari on 21/09/15.
 */
public class SessionsAndCookies {
    public static void setCookies(AppUser user) {
        response().setCookie("email", user.email);
        response().setCookie("name", user.firstname);
        response().setCookie("userAccessLevel", user.userAccessLevel.toString());
        response().setCookie("userId", user.id.toString());
    }

    public static void clearCookies() {
        response().discardCookie("email");
        response().discardCookie("name");
        response().discardCookie("userAccessLevel");
        response().discardCookie("userId");
    }
}

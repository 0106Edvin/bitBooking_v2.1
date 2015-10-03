package helpers;

import play.Play;

/**
 * Created by ajla.eltabari on 01/10/15.
 */
public class ApiTokenHelper {
    public static Boolean isValid(String token) {
        return (token.equals(Play.application().configuration().getString("token"))) ? true : false;
    }
}

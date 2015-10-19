package controllers.user;

import models.AppUser;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by boris.tomic on 19/10/15.
 */
public class UserApiController extends Controller {

    public Result getAllUsers() {
        List<AppUser> users = AppUser.finder.all();
        return ok(play.libs.Json.toJson(users));
    }
}

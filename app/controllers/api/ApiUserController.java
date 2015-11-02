package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import models.AppUser;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class ApiUserController extends Controller {

    public static Result login() {
        JsonNode json = request().body().asJson();
        String email = json.findPath("email").textValue();
        String password = json.findPath("password").textValue();

        AppUser user = AppUser.authenticate(email, password);
        if(user != null) {
            return ok();
        }
        return unauthorized();
    }



    public Result getAllUsers() {
        List<AppUser> users = AppUser.finder.all();
        return ok(Json.toJson(users));
    }

}

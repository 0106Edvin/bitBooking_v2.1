package controllers.api;

import models.Hotel;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by stvorenje on 10/26/15.
 */
public class ApiHotelController extends Controller{

    public Result getHotelList() {
        List<Hotel> hotelList = Hotel.finder.all();
        return ok(play.libs.Json.toJson(hotelList));
    }
}

package controllers.api;

import models.Hotel;
import models.HotelForNavigator;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains method that returns list of hotel objects
 * situated for connection with bitNavigator
 *
 * Created by ajla on 11/11/15.
 */
public class ApiHotelsForNavigator extends Controller {

    public Result getHotelsForExternalUsages() {

        // Getting list of all hotels
        List<Hotel> allHotels = Hotel.finder.all();

        // Declaring and initializing list of hotel objects, situated for bitNavigator
        List<HotelForNavigator> hotelsToGo = new ArrayList<>();

        /* Iterating through the list of hotels, creating HotelForNavigator obj,
        and filling the list that will be returned as a result */
        for (Hotel h : allHotels) {
            HotelForNavigator hfn = new HotelForNavigator(h.name, h.coordinateX, h.coordinateY, h.description);
            hotelsToGo.add(hfn);
        }

        // Returning the list as Json
        return ok(Json.toJson(hotelsToGo));
    }
}

package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import models.Hotel;
import models.Price;
import models.Restaurant;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Security;
import views.html.restaurant.*;

import java.util.Calendar;

/**
 * Created by ajla on 10/8/15.
 */
public class Restaurants extends Controller {

    public static final Form<Restaurant> restaurantForm = Form.form(Restaurant.class);

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result saveRestaurant(Integer hotelId) {
        Form<Restaurant> boundForm = restaurantForm.bindFromRequest();
        Restaurant restaurant = boundForm.get();

        Hotel hotel = Hotel.findHotelById(hotelId);
        restaurant.hotel = hotel;

        Calendar c = Calendar.getInstance();
        restaurant.timestamp = c.getTime();
        
        Ebean.save(restaurant);
        return redirect(routes.Rooms.showRooms(hotel.id));

    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result createRestaurant(Integer hotelId) {
        return ok(createRestaurant.render(hotelId));
    }
}

package controllers;

import helpers.Authenticators;
import models.Hotel;
import models.Image;
import models.Restaurant;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Security;
import views.html.restaurant.*;

import java.io.File;
import java.util.Calendar;
import java.util.List;

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
        
        restaurant.save();

        // PROMIJENITI OVU RUTU!!!!!!!!!!
        return redirect(routes.Rooms.showRooms(hotel.id));

    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result createRestaurant(Integer hotelId) {
        return ok(createRestaurant.render(hotelId));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateRestaurant(Integer restaurantId) {

        Restaurant restaurant = Restaurant.findRestaurantById(restaurantId);
        Form<Restaurant> restaurantForm1 = restaurantForm.bindFromRequest();

        String name = restaurantForm1.field("name").value();
        String restauranType = restaurantForm1.field("restauranType").value();
        Integer capacity = Integer.parseInt(restaurantForm1.field("capacity").value());
        String workingHours = restaurantForm1.field("workingHours").value();
        String description = restaurantForm1.field("description").value();


        restaurant.name = name;
        restaurant.restauranType = restauranType;
        restaurant.capacity = capacity;
        restaurant.workingHours = workingHours;
        restaurant.description = description;

        Http.MultipartFormData body1 = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> fileParts = body1.getFiles();
        if(fileParts != null) {
            for (Http.MultipartFormData.FilePart filePart1 : fileParts) {
                File file = filePart1.getFile();
                Image image = Image.create(file, null, null, null, null, restaurantId);
                restaurant.images.add(image);
            }
        }


        restaurant.update();

        // PROMIJENITI OVU RUTU!!!!!!!!!!!!
        return redirect(routes.Hotels.showHotel(restaurant.id));
        //return redirect(routes.Application.index());
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editRestaurant(Integer restaurantId) {
        Restaurant restaurant = Restaurant.findRestaurantById(restaurantId);
        return ok(updateRestaurant.render(restaurant));
    }
}

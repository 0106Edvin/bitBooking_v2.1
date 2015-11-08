package controllers;

import helpers.Authenticators;
import models.Hotel;
import models.Image;
import models.Restaurant;
import play.Logger;
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

    /**
     * Saves restaurant into the database.
     * Collects all data from the form, checks if provided
     * hotel already contains a restaurant (according to
     * project specifications, one hotel can contain only
     * one restaurant).
     *
     * @param hotelId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result saveRestaurant(Integer hotelId) {

        // Checking if there is a restaurant with provided
        // hotel id in the database.
        if (!Restaurant.existsInDB(hotelId)) {
            Form<Restaurant> boundForm = restaurantForm.bindFromRequest();

            Restaurant restaurant = new Restaurant();
            Form<Restaurant> restaurantForm1 = restaurantForm.bindFromRequest();

            // Collecting data from the form
            String name = restaurantForm1.field("name").value();
            String restaurantType = restaurantForm1.field("restauranType").value();
            Integer capacity = Integer.parseInt(restaurantForm1.field("capacity").value());
            String description = restaurantForm1.field("description").value();
            String open = restaurantForm1.field("restOpen").value();
            String close = restaurantForm1.field("restClose").value();
            String workingHours = open + " - " + close;

            restaurant.name = name;
            restaurant.restauranType = restaurantType;
            restaurant.capacity = capacity;
            restaurant.workingHours = workingHours;
            restaurant.description = description;


            // Finding hotel with provided hotel id
            Hotel hotel = Hotel.findHotelById(hotelId);

            // Checking if hotel with provided id exists
            if (hotel != null) {
                restaurant.hotel = hotel;
            }

            // Getting timestamp
            Calendar c = Calendar.getInstance();
            restaurant.timestamp = c.getTime();

            // Saving the restaurant into the database
            restaurant.save();

        } else { flash("error", "There is already added restaurant for selected hotel.");
            return ok(createRestaurant.render(hotelId));
        }

        if (session("userId") != null) {
            flash("create","The restaurant was created!");
            return redirect(routes.Hotels.showSellerHotels());
        } else {
            return redirect(routes.Application.index());
        }

    }

    /**
     * Checks if restaurant for provided hotel exists.
     * If it does returns the view with form for updating
     * hotel, filled with existed data. If it does not, returns
     * the view with form for creating the restaurant.
     *
     * @param hotelId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result createRestaurant(Integer hotelId) {

        if (Restaurant.existsInDB(hotelId)) {
            Restaurant restaurant = Restaurant.findRestaurantByHotelId(hotelId);
            return ok(updateRestaurant.render(restaurant));
        } else {
            return ok(createRestaurant.render(hotelId));
        }
    }

     /**
     * Updates currently selected restaurant.
     * Checks if room with selected id exists, if it does,
     * collects data from the form and updates the room.

     * @param restaurantId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateRestaurant(Integer restaurantId) {

        // Creating restaurant with provided restaurantId
        Restaurant restaurant = Restaurant.findRestaurantById(restaurantId);

        // Checking if such restaurant exists
        // If it does, collects its data and updates the restaurant.
        if (restaurant != null) {
            Form<Restaurant> restaurantForm1 = restaurantForm.bindFromRequest();

            String name = restaurantForm1.field("name").value();
            String restaurantType = restaurantForm1.field("restauranType").value();
            Integer capacity = Integer.parseInt(restaurantForm1.field("capacity").value());
            String description = restaurantForm1.field("description").value();
            String open = restaurantForm1.field("restOpen").value();
            String close = restaurantForm1.field("restClose").value();
            String workingHours = open + " - " + close;

            restaurant.name = name;
            restaurant.restauranType = restaurantType;
            restaurant.capacity = capacity;
            restaurant.workingHours = workingHours;
            restaurant.description = description;

            // Adding images for the restaurant.
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
        }

        if (session("userId") != null) {
            flash("edit","The restaurant was updated!");
            return redirect(routes.Hotels.showSellerHotels());
        } else {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Returns the view for editing the restaurant.
     * Checks if restaurant with provided id exists,
     * if it does, returns view for editing.
     * @param restaurantId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editRestaurant(Integer restaurantId) {
        Restaurant restaurant = Restaurant.findRestaurantById(restaurantId);

        if (restaurant != null) {
            return ok(views.html.restaurant.updateRestaurant.render(restaurant));
        } else {
            return redirect(routes.Application.index());
        }

    }

    /**
     * Checks if restaurant with provided id exists,
     * and deletes it.
     * @param restaurantId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result deleteRestaurant(Integer restaurantId) {

        Restaurant restaurant = Restaurant.findRestaurantById(restaurantId);
        if (session("userId") != null && restaurant != null) {
            restaurant.delete();
            flash("delete", "Restaurant deleted");
            return ok(Integer.parseInt(session("userId")) + "");
        }
        return internalServerError();
    }
}

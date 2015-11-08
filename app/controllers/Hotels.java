package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
import helpers.Constants;
import helpers.MailHelper;
import helpers.UserAccessLevel;
import models.*;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.hotel.createhotel;
import views.html.hotel.hotel;
import views.html.hotel.updateHotel;
import views.html.manager.managerHotels;
import views.html.seller.sellerPanel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Edvin Mulabdic on 9/30/2015.
 */
public class Hotels extends Controller {

    private Form<Hotel> hotelForm = Form.form(Hotel.class);
    private Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);
    private Model.Finder<String, AppUser> userfinder = new Model.Finder<>(AppUser.class);
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);


    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result createHotel() {
        List<Feature> features = Hotels.featureFinder.all();
        List<AppUser> users = AppUser.finder.all();
        return ok(createhotel.render(features, users));
    }

    /* Saving hotel to data base */

    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result saveHotel() {

        AppUser user = AppUser.getUserByEmail(session("email"));
        Hotel.saveHotel(user);
        List<Hotel> hotels = finder.all();
        List<AppUser> users = userfinder.all();


        return ok(managerHotels.render(hotels, users));
    }

    /* updating hotel  */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateHotel(Integer id) {
        AppUser user = AppUser.getUserByEmail(session("email"));

        Hotel hotel = Hotel.findHotelById(id);
        Form<Hotel> boundForm = hotelForm.bindFromRequest();

        //filling input fields with values
        String name = boundForm.field("name").value();
        String city = boundForm.field("city").value();
        String country = boundForm.field("country").value();
        String location = boundForm.field("location").value();
        String description = boundForm.field("description").value();
        String stars = boundForm.field("stars").value();

        Integer starsHotel = null;
        if (stars != null && !"".equals(stars.trim())) {
            try {
                starsHotel = Integer.parseInt(stars);
            } catch (NumberFormatException e) {
                ErrorLogger.createNewErrorLogger("Failed to parse input for hotel stars.", e.getMessage());
            }
        }

        List<Feature> features = listOfFeatures();
        Map<Integer, String> featurePrice = new HashMap<>();

        //allowing seller to set prices for features or leave them blank if feature is free
        for (int i = 0; i < features.size(); i++) {
            String price = boundForm.field(features.get(i).id.toString()).value();

            if (price != null) {
                featurePrice.put(features.get(i).id, price);
            }
        }

        for (Map.Entry<Integer, String> entry : featurePrice.entrySet()) {
            if (!"".equals(entry.getValue().trim())) {
                int featureId = entry.getKey();
                String price = featurePrice.get(featureId);
                HotelFeature temp = HotelFeature.getHotelFeatureByHotelIdAndFeatureId(id, featureId);
                temp.isFree = Constants.FEATURE_NOT_FREE;
                temp.price = price;
                temp.setUpdatedBy(user);
                temp.update();
            }
        }

        hotel.stars = starsHotel;
        hotel.name = name;
        hotel.location = location;
        hotel.description = description;
        hotel.city = city;
        hotel.country = country;

        //adding a pictures to hotel gallery
        Http.MultipartFormData body1 = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> fileParts = body1.getFiles();
        if(fileParts != null){
            for (Http.MultipartFormData.FilePart filePart1 : fileParts){
                File file = filePart1.getFile();
                Image image = Image.create(file, hotel.id, null, null, null, null);
                hotel.images.add(image);
            }
        }


        hotel.update();

        return redirect(routes.Hotels.showHotel(hotel.id));
    }

    /* method that finds all features  */
    public List<Feature> listOfFeatures() {
        List<Feature> features = featureFinder.all();
        return features;
    }


    /* showing hotel with his properties */
    public Result showHotel(Integer id) {
        Hotel hotel1 = Hotel.findHotelById(id);
        List<HotelFeature> features = HotelFeature.getFeaturesByHotelId(id);
        List<Room> rooms = hotel1.rooms;
        if(hotel1 != null) {
            hotel1.update();
        }
        AppUser user = null;
        if (request().cookies().get("email") != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
            Boolean hasRights = Comment.userHasRightsToCommentThisHotel(request().cookies().get("email").value(), hotel1);
            Boolean alreadyCommented = Comment.userAlreadyCommentedThisHotel(request().cookies().get("email").value(), hotel1);

            // Checking if UserAccessLevel is BUYER, and records his/hers visit for recommendation purposes
            if (user.userAccessLevel == UserAccessLevel.BUYER) {
                HotelVisit.createOrUpdateVisit(hotel1, user);
            }

            return ok(hotel.render(hotel1, hasRights, alreadyCommented, user, rooms, features));
        } else {
            return ok(views.html.hotel.hotel.render(hotel1, false, true, user, rooms, features));
        }
    }


    /* opens a  new window for editing hotel */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        List<HotelFeature> features = HotelFeature.getFeaturesByHotelId(id);
        return ok(updateHotel.render(hotel, features));
    }


    /*This method allows hotel manager to delete hotels*/
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result deleteHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        hotel.delete();

        return redirect(routes.Users.showManagerHotels());
    }

    /*This method allows admin to delete hotels*/

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteHotelAdmin(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        hotel.delete();

        return redirect(routes.Users.showAdminHotels());
    }

    /*This method send a list of all hotels to seller pael */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result showSellerHotels() {
        List<Hotel> hotels = finder.all();
        return ok(sellerPanel.render(hotels));

    }
    /* search method for hotels */
    public Result search() {
        DynamicForm form = Form.form().bindFromRequest();

        String searchWhat = form.field("search").value();
        String checkin = form.field("firstDate").value();
        String checkout = form.field("secondDate").value();

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        Date firstDate = null;
        Date secondDate = null;

        try {
            firstDate = dtf.parse(checkin);
            secondDate = dtf.parse(checkout);

            if(firstDate.after(secondDate)) {
                flash("error-search","First date can't be after second date!");
                return redirect(routes.Application.index());
            }
        } catch (ParseException e) {
            ErrorLogger.createNewErrorLogger("Failed to parse inputed dates in search.", e.getMessage());
            System.out.println(e.getMessage());
        }

        List<Hotel> hotels = Hotel.searchHotels(firstDate, secondDate, searchWhat);
        return ok(views.html.hotel.searchedhotels.render(hotels));
    }

    /* redirect on advanced search view */
    public Result advancedSearch() {
        return ok(views.html.hotel.advancedSearch.render(Hotel.finder.all()));
    }

    /* This method allows hotel manager to decide which hotels should appear on main page */
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result changeVisibility(Integer hotelId) {
        Hotel hotel = Hotel.findHotelById(hotelId);
        Boolean visibility = hotel.showOnHomePage;
        Hotel.setHotelVisibilityOnHomePage(hotel, !visibility);
        return redirect(routes.Users.showManagerHotels());
    }



}

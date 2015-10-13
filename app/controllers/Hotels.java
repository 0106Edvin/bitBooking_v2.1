package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
import models.*;
import play.Logger;
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
import views.html.user.profilePage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Hotels extends Controller {

    private Form<Hotel> hotelForm = Form.form(Hotel.class);
    private Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);
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

        Form<Hotel> boundForm = hotelForm.bindFromRequest();
        Hotel hotel = boundForm.get();

        List<Feature> features = listOfFeatures();
        //Getting values from checkboxes
        List<String> checkBoxValues = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.field(features.get(i).name).value();

            if (feature != null) {
                checkBoxValues.add(feature);
            }

            Logger.debug(checkBoxValues.toString());
        }

        List<Feature> featuresForHotel = new ArrayList<Feature>();

        for (int i = 0; i < checkBoxValues.size(); i++) {
            for (int j = 0; j < features.size(); j++) {
                if (features.get(j).name.equals(checkBoxValues.get(i))) {
                    featuresForHotel.add(features.get(j));
                }
            }
        }

        hotel.features = featuresForHotel;
        Integer sellerId = Integer.parseInt(boundForm.field("seller").value());

        hotel.sellerId = sellerId;

        hotel.save();

        List<Hotel> hotels = finder.all();
        return ok(managerHotels.render(hotels));


    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateHotel(Integer id) {

        Logger.debug("Usao u update!!!!!!!!!!!!!!!!");
        Hotel hotel = Hotel.findHotelById(id);
        Form<Hotel> hotelForm1 = hotelForm.bindFromRequest();

        String name = hotelForm1.field("name").value();
        String city = hotelForm1.field("city").value();
        String country = hotelForm1.field("country").value();
        String location = hotelForm1.field("location").value();
        String description = hotelForm1.field("description").value();


        hotel.name = name;
        hotel.location = location;
        hotel.description = description;
        hotel.city = city;
        hotel.country = country;

//        Http.MultipartFormData body = request().body().asMultipartFormData();
//        Http.MultipartFormData.FilePart filePart = body.getFile("profileImage");
//        if(filePart != null){
//            File file = filePart.getFile();
//            Image profileImage = Image.create(file,hotel.id,null,null);
//            hotel.profileImg = profileImage;
//        }

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
        //return redirect(routes.Application.index());
    }


    public List<Feature> listOfFeatures() {
        List<Feature> features = featureFinder.all();
        return features;
    }

    public Result showHotel(Integer id) {
        Hotel hotel1 = Hotel.findHotelById(id);
        if(hotel1 != null) {
            hotel1.update();
        }
        AppUser user = null;
        if (request().cookies().get("email") != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
            return ok(hotel.render(hotel1, Comment.userAlreadyCommentedThisHotel(request().cookies().get("email").value(), hotel1), user));
        } else {
            return ok(views.html.hotel.hotel.render(hotel1, true, user));
        }
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        return ok(updateHotel.render(hotel));
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

    public List<Hotel> listOfHotels() {
        List<Hotel> hotels = finder.all();
        return hotels;
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result showSellerHotels(Integer userId) {
        List<Hotel> hotels = finder.all();
        return ok(sellerPanel.render(hotels));

    }

    public Result search() {
        Form<Hotel> hotelForm1 = hotelForm.bindFromRequest();
        String category = hotelForm1.field("category").value();
        String searchWhat = hotelForm1.field("search").value();
        List<Hotel> hotels = new ArrayList<>();
        if (category.equals("name")) {
            hotels = Hotel.findHotelsByName(searchWhat);
        } else if(category.equals("country")) {
            hotels = Hotel.findHotelsByCountry(searchWhat);
        } else if(category.equals("city")) {
            hotels = Hotel.findHotelsByCity(searchWhat);
        } else if(category.equals("stars")) {
            try {
                if (searchWhat.length() > 1) {
                    flash("error-search", "Try searching by entering a number of stars from one to seven.");
                    return redirect(routes.Application.index());
                }
                Integer.parseInt(searchWhat);
            } catch (NumberFormatException e) {
                flash("error-search", "Try searching by entering a number of stars from one to seven.");
                return redirect(routes.Application.index());
            }
            hotels = Hotel.findHotelsByStars(searchWhat);
        } else if (category.equals("price")) {
            hotels = Hotel.findHotelsByPrice(searchWhat);
        }
        Logger.debug(hotels.size() + "");
//        else if(category.equals("price")){
//            List<Hotel> hotels = Room.findHotelByPrice(category);
//        }
        return ok(views.html.hotel.searchedhotels.render(hotels));
    }

    public Result advancedSearch() {
        return ok(views.html.hotel.advancedSearch.render(Hotel.finder.all()));
    }
}

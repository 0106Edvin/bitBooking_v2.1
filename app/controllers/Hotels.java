package controllers;

import com.avaje.ebean.Model;
import com.sun.org.apache.xpath.internal.operations.Bool;
import helpers.Authenticators;
import helpers.Constants;
import models.*;
import play.Logger;
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
import views.html.user.profilePage;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Form<Hotel> boundForm = hotelForm.bindFromRequest();
        Hotel hotel = boundForm.get();

        Integer sellerId = Integer.parseInt(boundForm.field("seller").value());

        hotel.sellerId = sellerId;
        hotel.showOnHomePage = Constants.SHOW_HOTEL_ON_HOMEPAGE;
        hotel.save();

        List<Feature> features = listOfFeatures();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.field(features.get(i).id.toString()).value();

            if (feature != null) {
                HotelFeature hotelFeature = new HotelFeature();
                hotelFeature.feature = features.get(i);
                hotelFeature.hotel = hotel;
                hotelFeature.setCreatedBy(user);
                hotelFeature.save();
            }
        }

        List<Hotel> hotels = finder.all();
        List<AppUser> users = userfinder.all();
        return ok(managerHotels.render(hotels, users));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateHotel(Integer id) {
        AppUser user = AppUser.getUserByEmail(session("email"));

        Hotel hotel = Hotel.findHotelById(id);
        Form<Hotel> hotelForm1 = hotelForm.bindFromRequest();

        String name = hotelForm1.field("name").value();
        String city = hotelForm1.field("city").value();
        String country = hotelForm1.field("country").value();
        String location = hotelForm1.field("location").value();
        String description = hotelForm1.field("description").value();

        List<Feature> features = listOfFeatures();
        Map<Integer, String> featurePrice = new HashMap<>();

        for (int i = 0; i < features.size(); i++) {
            String price = hotelForm1.field(features.get(i).id.toString()).value();

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
            return ok(hotel.render(hotel1, hasRights, alreadyCommented, user, rooms, features));
        } else {
            return ok(views.html.hotel.hotel.render(hotel1, false, true, user, rooms, features));
        }
    }

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
            System.out.println(e.getMessage());
        }

        List<Hotel> hotels = Hotel.searchHotels(firstDate, secondDate, searchWhat);
        return ok(views.html.hotel.searchedhotels.render(hotels));
    }

    public Result advancedSearch() {
        return ok(views.html.hotel.advancedSearch.render(Hotel.finder.all()));
    }

    public Result changeVisibility(Integer hotelId) {
        Hotel hotel = Hotel.findHotelById(hotelId);
        Boolean visibility = hotel.showOnHomePage;
        Hotel.setHotelVisibilityOnHomePage(hotel, !visibility);
        return redirect(routes.Users.showManagerHotels());
    }



}

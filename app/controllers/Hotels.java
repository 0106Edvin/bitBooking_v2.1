package controllers;

import com.avaje.ebean.Model;
import models.Feature;
import models.Hotel;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.hotel.createhotel;
import views.html.hotel.updateHotel;

import java.util.ArrayList;
import java.util.List;

public class Hotels extends Controller {

    private Form<Hotel> hotelForm = Form.form(Hotel.class);
    private Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);
//    public static Model.Finder<String, Room> roomFinder = new Model.Finder<String, Room>(Room.class);


    public Result createHotel() {
        List<Feature> features = Hotels.featureFinder.all();
        return ok(createhotel.render(features));
    }


    /*   Saving hotel to data base*/
    public Result saveHotel() {

        Form<Hotel> boundForm = hotelForm.bindFromRequest();
        Hotel hotel = boundForm.get();

        List<Feature> features = listOfFeatures();
        //Getting values from checkboxes
        List<String> checkBoxValues = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.bindFromRequest().field(features.get(i).name).value();

            if (feature != null) {
                checkBoxValues.add(feature);
            }
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

        hotel.save();
        return redirect(routes.Application.index());

    }

    public Result updateHotel(Integer id) {

        Hotel hotel = Hotel.findHotelById(id);
        Form<Hotel> hotelForm1 = hotelForm.bindFromRequest();

        String name = hotelForm1.bindFromRequest().field("name").value();
        String location = hotelForm1.bindFromRequest().field("location").value();
        String description = hotelForm1.bindFromRequest().field("description").value();


        hotel.name = name;
        hotel.location = location;
        hotel.description = description;


//        Http.MultipartFormData body = request().body().asMultipartFormData();
//        List<Http.MultipartFormData.FilePart> pictures = body.getFiles();
//
//        if (pictures != null) {
//            for (Http.MultipartFormData.FilePart picture : pictures) {
//                String fileName = picture.getFilename();
//                File file = picture.getFile();
//
//                try {
//                    FileUtils.moveFile(file, new File(Play.application().path() + "/public/images/" + fileName));
//                    Image image = new Image(fileName, hotel, null);
//                    Ebean.save(image);
//                } catch (IOException ex) {
//                    Logger.info("Could not move file. " + ex.getMessage());
//                    flash("error", "Could not move file.");
//                }
//            }
//        }

        hotel.update();

//        return ok(routes.Hotels.showHotel(hotel.id));
        return redirect(routes.Application.index());
    }


    public List<Feature> listOfFeatures() {
        List<Feature> features = featureFinder.all();
        return features;
    }

    public Result showHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
//        if (request().cookies().get("email") != null) {
////            return ok(hotel.render(hotel, Comment.userAlreadyCommentedThisHotel(request().cookies().get("email").value(), hotel)));
//        } else {
//

    return ok(views.html.hotel.hotel.render(hotel, true));
}

    public Result editHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        return ok(updateHotel.render(hotel));
    }

    /*This method allows hotel manager to delete hotels*/
    public Result deleteHotel(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        hotel.delete();

        return redirect(routes.Users.showManagerHotels());
    }

    /*This method allows admin to delete hotels*/

    public Result deleteHotelAdmin(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        hotel.delete();

        return redirect(routes.Users.showAdminHotels());
    }

    public List<Hotel> listOfHotels() {
        List<Hotel> hotels = finder.all();
        return hotels;
    }

//    public Result showRooms(Integer hotelId) {
//        List<Room> rooms = Room.finder.all();
//        Hotel hotel = Hotel.findHotelById(hotelId);
//        return ok(showRooms.render(rooms, hotel));
//    }
}

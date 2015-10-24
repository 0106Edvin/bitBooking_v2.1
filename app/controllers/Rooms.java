package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.Authenticators;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.hotel.hotel;
import views.html.room.createRoom;
import views.html.room.showRooms;
import views.html.room.updateRoom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/16/2015.
 */
public class Rooms extends Controller {

    public static final Form<Room> roomForm = Form.form(Room.class);
    public static Model.Finder<String, Room> finder = new Model.Finder<String, Room>(Room.class);
    public static Model.Finder<String, Feature> featureFinder = new Model.Finder<String, Feature>(Feature.class);

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result hotelReservations(Integer id) {
        Hotel hotel = Hotel.findHotelById(id);
        List<Room> rooms = hotel.rooms;
        AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));
        return ok(views.html.seller.hotelReservations.render(rooms,hotel,user));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result allReservations() {
        if(session("email") == null) {
            flash("error-search", "Try logging in to see this part of site.");
            return redirect(routes.Application.index());
        }
        AppUser user = AppUser.finder.where().eq("email", session("email").toLowerCase()).findUnique();
        List<Hotel> hotels = Hotel.finder.where().eq("seller_id", user.id).findList();
        if (hotels == null || hotels.size() == 0) {
            flash("info", "You have no hotels, try contacting our staff for more informations.");
            return redirect("/#mailPanel");
        }
        return ok(views.html.seller.allReservations.render(hotels));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result saveRoom(Integer hotelId) {
        Form<Room> boundForm = roomForm.bindFromRequest();
        Room room = boundForm.get();

        List<Feature> features = Feature.finder.all();
        //Getting values from checkboxes
        List<String> checkBoxValues = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.field(features.get(i).id.toString()).value();

            if (feature != null) {
                checkBoxValues.add(feature);
            }
        }

        List<Feature> featuresForRoom = new ArrayList<Feature>();

        for (int i = 0; i < checkBoxValues.size(); i++) {
            for (int j = 0; j < features.size(); j++) {
                if (features.get(j).id.toString().equals(checkBoxValues.get(i))) {
                    featuresForRoom.add(features.get(j));
                }
            }
        }

        room.features = featuresForRoom;


        if(!Room.checkRoomName(room.name, hotelId)) {
            flash("error-search", "You already have room with that name!");
            return redirect(routes.Rooms.createRoom(hotelId));
        }

        if (room.numberOfBeds <= 0) {
            flash("error", "Room can't have that number of beds!");
            redirect(routes.Rooms.createRoom(hotelId));
        }

        Hotel hotel = Hotel.findHotelById(hotelId);
        room.hotel = hotel;

        Ebean.save(room);
        return redirect(routes.Rooms.showRooms(hotel.id));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateRoom(Integer id) {

        Room room = Room.findRoomById(id);
        Form<Room> roomForm1 = roomForm.bindFromRequest();

        String name = roomForm1.field("name").value();
        String description = roomForm1.field("description").value();
        Integer numberOfBeds = Integer.parseInt(roomForm1.field("numberOfBeds").value());
        Integer roomType = Integer.parseInt(roomForm1.field("roomType").value());

        room.name = name;
        room.description = description;
        room.numberOfBeds = numberOfBeds;
        room.roomType = roomType;

        Http.MultipartFormData body1 = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> fileParts = body1.getFiles();
        if (fileParts != null) {
            for (Http.MultipartFormData.FilePart filePart1 : fileParts) {
                File file = filePart1.getFile();
                Image image = Image.create(file, null, null, null, room.id, null);
                room.images.add(image);
            }
        }


        room.update();

        return redirect(routes.Rooms.showRoom(id));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result deleteRoom(Integer id) {
        Room room = Room.findRoomById(id);

        Ebean.delete(room);
        return redirect(routes.Rooms.showRooms(room.hotel.id));
    }


    public Result showRoom(Integer id) {
        Room room = Room.findRoomById(id);
        AppUser user = null;
        if (session("userId") != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
        }
        return ok(views.html.room.room.render(room, user));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result createRoom(Integer hotelId) {
        List<Feature> features = Feature.finder.all();

        return ok(createRoom.render(features, hotelId));
    }

    public Result showRooms(Integer hotelId) {
        List<Room> rooms = Room.finder.all();
        Hotel hotel = Hotel.findHotelById(hotelId);
        AppUser user = null;
        if (session("userId") != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
        }

        return ok(showRooms.render(rooms, hotel, user));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editRoom(Integer id) {
        Room room = Room.findRoomById(id);

        List<Price> prices = Price.getRoomPrices(room);
        return ok(updateRoom.render(room, prices));
    }

}

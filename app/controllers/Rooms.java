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
import views.html.room.createRoom;
import views.html.room.showRooms;
import views.html.room.updateRoom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edvin Mulabdic on 9/16/2015.
 */
public class Rooms extends Controller {

    public static final Form<Room> roomForm = Form.form(Room.class);
    public static Model.Finder<String, Room> finder = new Model.Finder<String, Room>(Room.class);
    public static Model.Finder<String, Feature> featureFinder = new Model.Finder<String, Feature>(Feature.class);

    /**
     * Returnes view with all reservations for selected hotel.
     * Owner of the hotel is currently logged user (seller).
     * @param hotelId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result hotelReservations(Integer hotelId) {
        Hotel hotel = Hotel.findHotelById(hotelId);
        List<Room> rooms = hotel.rooms;
        AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));
        return ok(views.html.seller.hotelReservations.render(rooms, hotel, user));
    }

    /**
     * Returnes view with all reservations for currently logged user (seller).
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result allReservations() {
        if(session("email") == null) {
            flash("error-search", "Try logging in to see this part of site.");
            return redirect(routes.Application.index());
        }
        AppUser user = AppUser.finder.where().eq("email", session("email").toLowerCase()).findUnique();
        List<Hotel> hotels = Hotel.finder.where().eq("seller_id", user.id).findList();
        if (hotels == null || hotels.size() == 0) {
            flash("info", "You have no hotels, try contacting our staff for more information.");
            return redirect("/#mailPanel");
        }
        return ok(views.html.seller.allReservations.render(hotels));
    }

    /**
     * Saves room into the database.
     * Collects all data from the form, checks if room
     * with same name already exists in the database,
     * checks if number of beds is positive numbers,
     * processes features list, and calls room.save method.
     * Redirects user to the view with all rooms for selected hotel.
     * @param hotelId
     * @return
     */
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

        // Features are presented using checkboxes.
        // We need to collect all checked features,
        // find them by their id and add them to the
        // features_room entity.
        List<Feature> featuresForRoom = new ArrayList<Feature>();

        for (int i = 0; i < checkBoxValues.size(); i++) {
            for (int j = 0; j < features.size(); j++) {
                if (features.get(j).id.toString().equals(checkBoxValues.get(i))) {
                    featuresForRoom.add(features.get(j));
                }
            }
        }

        room.features = featuresForRoom;


        // According to project specifications, room name needs to be unique.
        // Checking if room with entered name already exits.
        if(!Room.checkRoomName(room.name, hotelId)) {
            flash("error-search", "You already have room with that name!");
            return redirect(routes.Rooms.createRoom(hotelId));
        }

        // Number of beds needs to be a positive number.
        // Checking if that is the case.
        if (room.numberOfBeds <= 0) {
            flash("error", "Room can't have that number of beds!");
            redirect(routes.Rooms.createRoom(hotelId));
        }

        // Finding the hotel by provided hotel id, and
        // adding that hotel to the room
        Hotel hotel = Hotel.findHotelById(hotelId);
        room.hotel = hotel;

        // Saving the room
        room.save();

        // Redirect to the view that displays all rooms for currently selected hotel.
        return redirect(routes.Rooms.showRooms(hotel.id));
    }

    /**
     * Updates currently selected room.
     * Checks if room with selected id exists, if it does,
     * collects data from the form and updates the room.
     * @param roomId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result updateRoom(Integer roomId) {

        Room room = Room.findRoomById(roomId);

        if (room != null) {
            Form<Room> roomForm1 = roomForm.bindFromRequest();

            String name = roomForm1.field("name").value();
            String description = roomForm1.field("description").value();
            Integer numberOfBeds = Integer.parseInt(roomForm1.field("numberOfBeds").value());
            Integer roomType = Integer.parseInt(roomForm1.field("roomType").value());
            Integer courseId = Integer.parseInt(roomForm1.field("course").value());

            room.name = name;
            room.description = description;
            room.numberOfBeds = numberOfBeds;
            room.roomType = roomType;

            Logger.debug(courseId + " COURSE ID");
            if (courseId != -1) {
                Logger.debug("aaaaa");
                room.course = Course.getCourseByCourseId(courseId);
            } else {
                room.course = null;
            }


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
        }

        return redirect(routes.Rooms.showRoom(roomId));
    }

    /**
     * Deletes the room with provided id.
     * Checks if room with selected id exists,
     * if it does, deletes it.
     * @param id
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result deleteRoom(Integer id) {
        Room room = Room.findRoomById(id);

        if (room != null) {
            Ebean.delete(room);
        }

        return redirect(routes.Rooms.showRooms(room.hotel.id));
    }

    /**
     * Returns the view with selected room data.
     * @param roomId
     * @return
     */
    public Result showRoom(Integer roomId) {
        Room room = Room.findRoomById(roomId);
        AppUser user = null;
        if (session("userId") != null && room != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
        }
        return ok(views.html.room.room.render(room, user));
    }

    /**
     * Retruns the view for creating new room.
     * @param hotelId
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result createRoom(Integer hotelId) {
        List<Feature> features = Feature.finder.all();

        return ok(createRoom.render(features, hotelId));
    }

    /**
     * Returns the view with list of all rooms for provided hotel.
     * @param hotelId
     * @return
     */
    public Result showRooms(Integer hotelId) {
        List<Room> rooms = Room.finder.all();
        Hotel hotel = Hotel.findHotelById(hotelId);
        AppUser user = null;
        if (session("userId") != null) {
            user = AppUser.findUserById(Integer.parseInt(session("userId")));
        }

        return ok(showRooms.render(rooms, hotel, user));
    }

    /**
     * Returns the view for editing room.
     * The form will be populated with existed data.
     * @param id
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editRoom(Integer id) {
        Room room = Room.findRoomById(id);

        if (room != null) {
            List<Price> prices = Price.getRoomPrices(room);
            return ok(updateRoom.render(room, prices));
        }

        return redirect(routes.Application.index());
    }

}

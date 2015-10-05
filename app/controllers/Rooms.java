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
        return ok(views.html.room.hotelReservations.render(rooms,hotel,user));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result saveRoom(Integer hotelId) {
        Form<Room> boundForm = roomForm.bindFromRequest();
        Room room = boundForm.get();

        if(!Room.checkRoomName(room.name, hotelId)) {
            flash("error", "You already have room with that name!");
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

        room.name = name;
        room.description = description;
        room.numberOfBeds = numberOfBeds;

        Http.MultipartFormData body1 = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> fileParts = body1.getFiles();
        if (fileParts != null) {
            for (Http.MultipartFormData.FilePart filePart1 : fileParts) {
                File file = filePart1.getFile();
                Image image = Image.create(file, null, null, null, room.id);
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
        return ok(updateRoom.render(room));
    }

}

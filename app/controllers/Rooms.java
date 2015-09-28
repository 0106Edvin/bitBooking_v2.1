package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import controllers.routes;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.room.*;
import views.html.seller.*;

import java.io.File;
import java.util.List;

/**
 * Created by User on 9/16/2015.
 */
public class Rooms extends Controller {

    public static final Form<Room> roomForm = Form.form(Room.class);
    public static Model.Finder<String, Room> finder = new Model.Finder<String, Room>(Room.class);
    public static Model.Finder<String, Feature> featureFinder = new Model.Finder<String, Feature>(Feature.class);

    public Result saveRoom(Integer hotelId) {

        Form<Room> boundForm = roomForm.bindFromRequest();
        Room room = boundForm.get();
        if(room.numberOfBeds <= 0){
            flash("error", "Room can't have that number of beds!");
            redirect(routes.Rooms.createRoom(hotelId));
        }
        Hotel hotel = Hotel.findHotelById(hotelId);

        room.hotel = hotel;

        Ebean.save(room);
        return redirect(routes.Rooms.showRooms(hotel.id));
    }

    public Result updateRoom(Integer id) {

        Room room = Room.findRoomById(id);
        Form<Room> roomForm1 = roomForm.bindFromRequest();

        String name = roomForm1.bindFromRequest().field("name").value();
        String description = roomForm1.bindFromRequest().field("description").value();
        Integer numberOfBeds = Integer.parseInt(roomForm1.bindFromRequest().field("numberOfBeds").value());

        room.name = name;
        room.description = description;
        room.numberOfBeds = numberOfBeds;

        Http.MultipartFormData body1 = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> fileParts = body1.getFiles();
        if(fileParts != null){
            for (Http.MultipartFormData.FilePart filePart1 : fileParts){
                File file = filePart1.getFile();
                Image image = Image.create(file,null,null,null,room.id);
                room.images.add(image);
            }
        }

        room.update();

       return redirect(routes.Rooms.showRoom(id));
    }
    public Result deleteRoom(Integer id){
        Room room = Room.findRoomById(id);

        Ebean.delete(room);
        return redirect(routes.Rooms.showRooms(room.hotel.id));
    }


    public Result showRoom (Integer id) {
        Room room = Room.findRoomById(id);
        return ok(views.html.room.room.render(room));
    }

    public Result createRoom(Integer hotelId){
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

    public Result editRoom(Integer id) {
        Room room = Room.findRoomById(id);
        return ok(updateRoom.render(room));
    }

}

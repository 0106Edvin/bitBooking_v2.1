package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import controllers.routes;
import models.Feature;
import models.Hotel;
import models.Room;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.room.*;
import views.html.seller.*;

import java.util.List;

/**
 * Created by User on 9/16/2015.
 */
public class Rooms extends Controller {

    public static final Form<Room> roomForm = Form.form(Room.class);
    public static Model.Finder<String, Room> finder = new Model.Finder<String, Room>(Room.class);
    public static Model.Finder<String, Feature> featureFinder = new Model.Finder<String, Feature>(Feature.class);
    public static Model.Finder<String, Hotel> hotelFinder = new Model.Finder<String, Hotel>(Hotel.class);


    public Result saveRoom(Integer hotelId) {

        Form<Room> boundForm = roomForm.bindFromRequest();
        Room room = boundForm.get();
        Hotel hotel = Hotel.findHotelById(hotelId);

        room.hotel = hotel;

        Ebean.save(room);
        return redirect(routes.Application.index());
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

        room.update();

       return redirect(routes.Prices.savePrice(id));
    }
    public Result deleteRoom(Integer id){
        Room room = Room.findRoomById(id);

        Ebean.delete(room);
        return redirect(routes.Application.index());
    }


    public Result showRoom (Integer id) {
        Room room = Room.findRoomById(id);
        return ok(views.html.room.room.render(room));
    }
    public Result showSellerHotels() {
        List<Hotel> hotels = hotelFinder.all();
        return ok(sellerPanel.render(hotels));

    }
    public Result createRoom(Integer hotelId){
            List<Feature> features = Feature.finder.all();

        return ok(createRoom.render(features, hotelId));
    }

}
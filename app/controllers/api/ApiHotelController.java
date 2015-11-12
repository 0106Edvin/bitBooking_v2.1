package controllers.api;

import models.AppUser;
import models.Hotel;
import models.Room;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


public class ApiHotelController extends Controller{

    /**
     * Get Hotel list
     * this method returns list of hotels to android app,
     * if there is valid user token in header of request.
     * @return - list of hotels as Json
     */
    public Result getHotelList() {

        String userToken = request().getHeader("User_Token");
        List<AppUser> users = AppUser.finder.all();
        Logger.info("------------" + userToken + "------------");
        for (AppUser user: users) {
            if (user.androidToken != null) {
                if(user.androidToken.toString().equals(userToken.toString())) {
                    List<Hotel> hotelList = Hotel.finder.all();
                    return ok(Json.toJson(hotelList));
                }
            }
        }
        return unauthorized();
    }

    /**
     * Get Hotel Rooms
     * this method returns list of rooms.
     * @return - list of hotel rooms as Json
     */
    public Result getHotelRooms(Integer hotelId) {

        String userToken = request().getHeader("User_Token");
        List<AppUser> users = AppUser.finder.all();

        for (AppUser user: users) {
            if (user.androidToken != null) {
                if (user.androidToken.equals(userToken)) {
                    List<Room> rooms = Hotel.findHotelById(hotelId).rooms;
                    return ok(Json.toJson(rooms));
                }
            }
        }
        return unauthorized();
    }

}

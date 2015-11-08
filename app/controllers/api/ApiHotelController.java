package controllers.api;

import models.Hotel;
import models.Price;
import models.Room;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by stvorenje on 10/26/15.
 */
public class ApiHotelController extends Controller{

    public Result getHotelList() {
        List<Hotel> hotelList = Hotel.finder.all();
        return ok(Json.toJson(hotelList));
    }

    public Result getHotelRooms(Integer hotelId) {
        List<Room> rooms = Hotel.findHotelById(hotelId).rooms;
        return ok(Json.toJson(rooms));
    }

    public Result getRoomPrices(Integer roomId) {
        List<Price> roomPrices = Room.findRoomById(roomId).prices;

        if(roomPrices.size() < 1) {
            return noContent();
        }
        return ok(Json.toJson(roomPrices));
    }

//    public Result getCost(Integer roomId, long checkedIn, long checkOut) {
//        Date myDate = checkIn;
//        for (Price price : room.prices) {
//            while (myDate.compareTo(checkOut) <= 0) {
//                if (myDate.compareTo(price.dateFrom) >= 0 && myDate.compareTo(price.dateTo) <= 0) {
//                    cost = price.cost;
//                } else {
//                    break;
//                }
//                myDate = org.apache.commons.lang.time.DateUtils.addDays(myDate, 1);
//            }
//        }
//        return cost;
//        return ok();
//    }

}

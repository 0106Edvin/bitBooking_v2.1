package controllers;

import models.Price;
import models.Room;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.addPrice;
import views.html.room.updateRoom;


/**
 * Created by User on 9/17/2015.
 */
public class Prices extends Controller {
    public static final Form<Price> priceForm = Form.form(Price.class);

    public Price getPrice(Integer id) {
        Price price = Price.findPriceById(id);
        return price;
    }

    public Result savePrice(Integer roomId) {
        Form<Price> boundForm = priceForm.bindFromRequest();
        Price price = boundForm.get();

        Room room = Room.findRoomById(roomId);

        price.room = room;
        price.save();
        return ok(updateRoom.render(room));
    }

    public Result insertPrice(Integer roomId){
        return ok(addPrice.render(roomId));
    }
}

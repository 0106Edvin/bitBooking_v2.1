package controllers;

import helpers.Authenticators;
import models.Price;
import models.Room;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.addPrice;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by User on 9/17/2015.
 */
public class Prices extends Controller {
    public static final Form<Price> priceForm = Form.form(Price.class);

    public Price getPrice(Integer id) {
        Price price = Price.findPriceById(id);
        return price;
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result savePrice(Integer roomId) {
        Form<Price> boundForm = priceForm.bindFromRequest();
        String cost = boundForm.field("cost").value();
        if(cost.equals("") || cost == null) {
            flash("error","You must set room price!");
            return redirect(routes.Rooms.editRoom(roomId));
        }
        String checkin = boundForm.field("checkIn").value();
        String[] checkInParts = checkin.split("-");
        String checkout = boundForm.field("checkOut").value();
        String[] checkOutParts = checkout.split("-");
        Price price = new Price();
        try {
            checkin = checkInParts[2] + "/" + checkInParts[1] + "/" + checkInParts[0];
            checkout = checkOutParts[2] +"/"+ checkOutParts[1]+"/"+checkOutParts[0];
        }catch (IndexOutOfBoundsException e){
            flash("error","Set date period and choose price for that period!");
            return redirect(routes.Rooms.editRoom(roomId));
        }
        Room room = Room.findRoomById(roomId);
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date firstDate = dtf.parse(checkin);
            Date secondDate = dtf.parse(checkout);
            if(firstDate.before(secondDate)) {
                price.dateFrom = firstDate;
                price.dateTo = secondDate;
            } else {
                flash("error","First date can't be after second date!");
                return redirect(routes.Rooms.editRoom(roomId));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        price.room = room;
        price.cost = new BigDecimal(Long.parseLong(cost));
        price.save();
        List<Price> prices = Price.getRoomPrices(room);
        return ok(views.html.room.updateRoom.render(room, prices));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result insertPrice(Integer roomId){
        return ok(addPrice.render(roomId));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result delete(Integer id){

        Price price = Price.findPriceById(id);
        price.delete();

        Room room = Room.findRoomById(price.room.id);
        List<Price> prices = Price.getRoomPrices(room);


        return ok(views.html.room.updateRoom.render(room, prices));
    }
}

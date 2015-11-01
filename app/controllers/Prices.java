package controllers;

import helpers.Authenticators;
import models.ErrorLogger;
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
        if (cost.equals("") || cost == null) {
            flash("error", "You must set room price!");
            return redirect(routes.Rooms.editRoom(roomId));
        }
        String checkin = boundForm.field("checkIn").value();
        String checkout = boundForm.field("checkOut").value();
        Price price = new Price();
        Room room = Room.findRoomById(roomId);
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date firstDate = dtf.parse(checkin);
            Date secondDate = dtf.parse(checkout);
            if (firstDate.before(secondDate)) {
                price.dateFrom = firstDate;
                price.dateTo = secondDate;
            } else {
                flash("error", "First date can't be after second date!");
                return redirect(routes.Rooms.editRoom(roomId));
            }
        } catch (ParseException e) {
            ErrorLogger.createNewErrorLogger("Failed to parse inputed dates when saving price.", e.getMessage());
            System.out.println(e.getMessage());
        }
        price.room = room;
        price.cost = new BigDecimal(Long.parseLong(cost));
        price.save();
        flash("success", " Price successfully added. ");
        List<Price> prices = Price.getRoomPrices(room);
        return ok(views.html.room.updateRoom.render(room, prices));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result insertPrice(Integer roomId) {
        return ok(addPrice.render(roomId));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result delete(Integer id) {

        Price price = Price.findPriceById(id);
        Room room = Room.findRoomById(price.room.id);
        price.delete();

        List<Price> prices = Price.getRoomPrices(room);
        return redirect(routes.Rooms.updateRoom(room.id));
        //return redirect(views.html.room.updateRoom.render(room, prices));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editPrice(Integer id) {
        Price price = Price.findPriceById(id);
        Room room = Room.findRoomById(price.room.id);

        Form<Price> boundForm = priceForm.bindFromRequest();
        // price in Price model => BigDecimal cost
        String cost = boundForm.field("cost").value();
        if (cost.equals("") || cost == null) {
            flash("missing-price", "Please, set room price value and then save.");
            // here I should render just modal .. this way msg is shown when u open modal again
            return redirect(routes.Rooms.updateRoom(room.id));
        }
        price.cost = new BigDecimal(Long.parseLong(cost));
        price.save();

        List<Price> prices = Price.getRoomPrices(room);
        return redirect(routes.Rooms.updateRoom(room.id));

    }
}

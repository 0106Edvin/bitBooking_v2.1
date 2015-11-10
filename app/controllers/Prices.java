package controllers;

import helpers.Authenticators;
import helpers.CommonHelperMethods;
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

    private static final Form<Price> priceForm = Form.form(Price.class);

    /**
     * Saves new price to database. Collects data from view via priceForm, checks strings if valid.
     * Converts date strings to <code>Date</code> type value, saves price with success message.
     * If something could not pass correctly error message is shown.
     *
     * @param roomId <code>Integer</code> type value of room id
     * @return redirect to editRoom if error occurs, redirect to updateRoom if price is saved
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result savePrice(Integer roomId) {
        Form<Price> boundForm = priceForm.bindFromRequest();
        Room room = Room.findRoomById(roomId);
        String cost = boundForm.field("cost").value();
        String checkin = boundForm.field("checkIn").value();
        String checkout = boundForm.field("checkOut").value();

        if (!CommonHelperMethods.validateInputString(cost)) {
            flash("error", "You must set room price!");
            return redirect(routes.Rooms.editRoom(roomId));
        }

        if (!CommonHelperMethods.validateInputString(checkin) || !CommonHelperMethods.validateInputString(checkout)) {
            flash("error", "You must select date for room price!");
            return redirect(routes.Rooms.editRoom(roomId));
        }

        Date firstDate = CommonHelperMethods.convertStringToDate(checkin);
        Date secondDate = CommonHelperMethods.convertStringToDate(checkout);

        if (firstDate != null && secondDate != null) {
            if (firstDate.after(secondDate)) {
                flash("error", "First date can't be after second date!");
                return redirect(routes.Rooms.editRoom(roomId));
            }
            if (Price.createNewPrice(room, firstDate, secondDate, cost)) {
                List<Price> prices = Price.getRoomPrices(room);
                flash("success", " Price successfully added. ");
                return ok(views.html.room.updateRoom.render(room, prices));
            }
        }
        flash("error", "Price could not be added.");
        return redirect(routes.Rooms.editRoom(roomId));
    }

    /**
     * Renders price secton of view.
     *
     * @param roomId <code>Integer</code> type value of room prices
     * @return ok and renders view with price
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result insertPrice(Integer roomId) {
        return ok(addPrice.render(roomId));
    }

    /**
     * Deleted selected price.
     *
     * @param id <code>Integer</code> type value of price id
     * @return redirect to updateRoom with appropriate flash message
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result delete(Integer id) {
        Price price = Price.findPriceById(id);
        Room room = Room.findRoomById(price.room.id);
        if (Price.deletePrice(price)) {
            List<Price> prices = Price.getRoomPrices(room);
            flash("error", "Price could not be deleted.");
            return redirect(routes.Rooms.updateRoom(room.id));
        }
        flash("info", "Price successfully deleted.");
        return redirect(routes.Rooms.updateRoom(room.id));
    }

    /**
     * Edit price with new cost value, cost is taken from priceForm, and price id is passed as method parameter.
     * If price is edited successfully info message is shown, otherwise error message is shown.
     *
     * @param id <code>Integer</code> type value of price id
     * @return redirec to updateRoom with appropriate flash message
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result editPrice(Integer id) {
        Price price = Price.findPriceById(id);
        Room room = Room.findRoomById(price.room.id);

        Form<Price> boundForm = priceForm.bindFromRequest();
        String cost = boundForm.field("cost").value();

        if (!CommonHelperMethods.validateInputString(cost)) {
            flash("missing-price", "Please, set room price value and then save.");
            return redirect(routes.Rooms.updateRoom(room.id));
        }

        if (Price.updatePrice(price, cost)) {
            List<Price> prices = Price.getRoomPrices(room);
            flash("info", "Price successfully updated.");
            return redirect(routes.Rooms.updateRoom(room.id));
        }
        flash("error", "Price could not be updated.");
        return redirect(routes.Rooms.updateRoom(room.id));
    }

}

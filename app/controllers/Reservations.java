package controllers;

import helpers.ReservationStatus;
import models.AppUser;
import models.Price;
import models.Reservation;
import models.Room;
import org.apache.commons.lang.time.DateUtils;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.LocalGregorianCalendar;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gordan on 9/29/15.
 */
public class Reservations extends Controller {

    private Form<Reservation> reservationForm = Form.form(Reservation.class);
    public Result saveReservation(Integer roomId) {
        AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));
        Form<Reservation> boundForm = reservationForm.bindFromRequest();
        String checkin = boundForm.bindFromRequest().field("checkIn").value();
        String[] checkInParts = checkin.split("-");
        String checkout = boundForm.bindFromRequest().field("checkOut").value();
        String[] checkOutParts = checkout.split("-");

        try {
            checkin = checkInParts[2] + "/" + checkInParts[1] + "/" + checkInParts[0];
            checkout = checkOutParts[2] +"/"+ checkOutParts[1]+"/"+checkOutParts[0];
        }catch (IndexOutOfBoundsException e){
            flash("error","Wrong date format!");
            return redirect(routes.Rooms.showRoom(roomId));
        }

        Room room = Room.findRoomById(roomId);
        Reservation reservation = new Reservation();
        reservation.room = room;
        reservation.user = user;
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date firstDate = dtf.parse(checkin);
            Date secondDate = dtf.parse(checkout);
            if(firstDate.before(secondDate)){
                reservation.checkIn = firstDate;
                reservation.checkOut = secondDate;
                reservation.cost =reservation.getCost();
            }else {
                flash("error","Check in date can't be after check out date!");
                return redirect(routes.Rooms.showRoom(roomId));
            }
        }catch (ParseException e){
            System.out.println(e.getMessage());
        }
        reservation.status = ReservationStatus.PENDING;
        reservation.save();
        return redirect(routes.Application.index());
    }
}

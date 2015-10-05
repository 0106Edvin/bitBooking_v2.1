package controllers;

import helpers.Authenticators;
import helpers.ReservationStatus;
import models.AppUser;
import models.Hotel;
import models.Reservation;
import models.Room;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by gordan on 9/29/15.
 */
public class Reservations extends Controller {

    private Form<Reservation> reservationForm = Form.form(Reservation.class);


    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result saveReservation(Integer roomId) {
        AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));
        Form<Reservation> boundForm = reservationForm.bindFromRequest();
        String checkin = boundForm.field("checkIn").value();
        String[] checkInParts = checkin.split("-");
        String checkout = boundForm.field("checkOut").value();
        String[] checkOutParts = checkout.split("-");

        try {
            checkin = checkInParts[2] + "/" + checkInParts[1] + "/" + checkInParts[0];
            checkout = checkOutParts[2] +"/"+ checkOutParts[1]+"/"+checkOutParts[0];
        } catch (IndexOutOfBoundsException e){
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
            if(firstDate.before(secondDate)) {
                reservation.checkIn = firstDate;
                reservation.checkOut = secondDate;
                reservation.cost =reservation.getCost();
            } else {
                flash("error","Check in date can't be after check out date!");
                return redirect(routes.Rooms.showRoom(roomId));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        reservation.status = ReservationStatus.PENDING;
        reservation.save();
        return redirect(routes.Reservations.showBuyerReservations(user.id));
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result setStatus(Integer id) {
        Form<Reservation> boundForm = reservationForm.bindFromRequest();
        Reservation reservation = Reservation.findReservationById(id);

        String status = boundForm.field("status").value();

        if (status.equals(ReservationStatus.PENDING.toString())) {
            reservation.status = ReservationStatus.PENDING;

        } else if (status.equals(ReservationStatus.APPROVED.toString())) {
           reservation.status = ReservationStatus.APPROVED;

        } else if (status.equals(ReservationStatus.DECLINED.toString())) {
            reservation.status = ReservationStatus.DECLINED;
        }
        reservation.update();

        return redirect(routes.Rooms.hotelReservations(reservation.room.hotel.id));
    }

    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result showBuyerReservations(Integer userId) {
        List<Reservation> reservationList = Reservation.findReservationByUserId(userId);
        for (Reservation reservation : reservationList) {
            if (reservation != null) {
                Room room = reservation.room;
                Hotel hotel = room.hotel;
                AppUser user = AppUser.findUserById(userId);

                return ok(views.html.user.buyerReservations.render(room, hotel, reservationList, user));
            }
        }
        flash("info", "You have no reservations.");
        return redirect(routes.Application.index());
    }

}

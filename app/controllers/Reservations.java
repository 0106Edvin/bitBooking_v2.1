package controllers;

import helpers.Authenticators;
import helpers.ReservationStatus;
import helpers.SessionsAndCookies;
import models.AppUser;
import models.Hotel;
import models.Reservation;
import models.Room;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        String checkout = boundForm.field("checkOut").value();

        Room room = Room.findRoomById(roomId);
        Reservation reservation = new Reservation();
        reservation.room = room;
        reservation.user = user;
        reservation.setCreatedBy(user.firstname, user.lastname);

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
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
        Form<Reservation> boundForm = reservationForm.bindFromRequest();
        Reservation reservation = Reservation.findReservationById(id);
        Room room = Reservation.findRoomByReservation(reservation);

        String status = boundForm.field("status").value();

        if (status.equals(ReservationStatus.PENDING.toString())) {
            reservation.status = ReservationStatus.PENDING;

        } else if (status.equals(ReservationStatus.APPROVED.toString())) {
            reservation.status = ReservationStatus.APPROVED;
            reservation.notification = ReservationStatus.NEW_NOTIFICATION;
            if(room.roomType > 0){
                room.roomType = room.roomType -1;
            }else{
                reservation.status = ReservationStatus.PENDING;
                flash("error", "All rooms of this type are booked");
            }

        } else if (status.equals(ReservationStatus.DECLINED.toString())) {
            reservation.status = ReservationStatus.DECLINED;
            reservation.notification = ReservationStatus.NEW_NOTIFICATION;
        }else if (status.equals(ReservationStatus.COMPLETED.toString())){
            reservation.status = ReservationStatus.COMPLETED;
            room.roomType = room.roomType + 1;
        }
        room.update();
        reservation.setUpdatedBy(temp.firstname, temp.lastname);
        reservation.update();

        return redirect(routes.Rooms.hotelReservations(reservation.room.hotel.id));
    }

    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result showBuyerReservations(Integer userId) {
        AppUser user = AppUser.findUserById(userId);
        List<Reservation> reservationList = Reservation.findReservationByUserId(userId);
        Room room = null;
        Hotel hotel = null;
        for (Reservation reservation : reservationList) {
            if (reservation == null) {
                flash("info", "You have no reservations.");
                return redirect(routes.Application.index());
            }
            reservation.notification = ReservationStatus.READ_NOTIFICATION;
            reservation.setUpdatedBy(user.firstname, user.lastname);
            reservation.update();
            room = reservation.room;
            hotel = room.hotel;
        }
        return ok(views.html.user.buyerReservations.render(room, hotel, reservationList, user));
    }

    /**
     * Checks price for selected period and returns value as String.
     * Ajax calls this method when date on reservation is selected.
     *
     * @return
     */
    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result getPrice() {
        AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));
        DynamicForm form = Form.form().bindFromRequest();
        String checkin = form.field("date").value();
        String checkout = form.field("date2").value();
        String roomId = form.field("room").value();
        Room room = Room.findRoomById(Integer.parseInt(roomId));
        Reservation reservation = new Reservation();
        reservation.room = room;
        reservation.user = user;

        BigDecimal price = null;
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date firstDate = dtf.parse(checkin);
            Date secondDate = dtf.parse(checkout);
            reservation.checkIn = firstDate;
            reservation.checkOut = secondDate;
            price = reservation.getCost();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return ok(price.toString());
    }

}
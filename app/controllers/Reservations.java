package controllers;

import com.avaje.ebean.Model;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import helpers.Authenticators;
import helpers.ReservationStatus;
import helpers.SessionsAndCookies;
import models.AppUser;
import models.Hotel;
import models.Reservation;
import models.Room;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gordan on 9/29/15.
 */
public class Reservations extends Controller {

    private static PaymentExecution paymentExecution;

    private static Form<Reservation> reservationForm = Form.form(Reservation.class);



    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result payPal(Integer roomId) {
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
            if (firstDate.before(secondDate)) {
                reservation.checkIn = firstDate;
                reservation.checkOut = secondDate;
                reservation.cost = reservation.getCost();
            } else {
                flash("error", "Check in date can't be after check out date!");
                return redirect(routes.Rooms.showRoom(roomId));
            }


            // Configuration
            String clientid = Play.application().configuration().getString("clientId");
            String secret = Play.application().configuration().getString("clientSecret");

            String token = new OAuthTokenCredential(clientid, secret).getAccessToken();

            Map<String, String> config = new HashMap<>();
            config.put("mode", "sandbox");

            APIContext context = new APIContext(token);
            context.setConfigurationMap(config);

            // Process cart/payment information

            double price = reservation.cost.doubleValue();

            String priceString = String.format("%1.2f", price);

            String desc = "Costumer name: " + reservation.createdBy + "\n" +
                    "Reservation for hotel: " + room.hotel.name + "\n " +
                    "Check-in: " + reservation.checkIn + "\n" +
                    "Check-out" + reservation.checkOut + "\n" +
                    "Amount: " + priceString;
            // Configure payment
            Amount amount = new Amount();
            amount.setTotal(priceString);
            amount.setCurrency("USD");

            List<Transaction> transactionList = new ArrayList<>();
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription(desc);
            transactionList.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setPayer(payer);
            payment.setIntent("sale");
            payment.setTransactions(transactionList);



            RedirectUrls redirects = new RedirectUrls();
            redirects.setCancelUrl("http://localhost:9000/rejectPayment");
            redirects.setReturnUrl("http://localhost:9000/paypal/success");

            payment.setRedirectUrls(redirects);

            Payment madePayments = payment.create(context);
            String id = madePayments.getId();
            reservation.payment_id = id;


            reservation.status = ReservationStatus.PENDING;
            reservation.save();

            Iterator<Links> it = madePayments.getLinks().iterator();
            while (it.hasNext()) {
                Links link = it.next();
                if (link.getRel().equals("approval_url")) {
                    return redirect(link.getHref());
                }
            }

            Payment newPayment = payment.execute(context, paymentExecution );
            Logger.info("new PAYMENT "  + newPayment);

        } catch (PayPalRESTException e) {
            Logger.warn("PayPal Exception");
            e.printStackTrace();
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return redirect("/");
    }

    public Result paypalSuccess() {

        APIContext context;
        PaymentExecution paymentExecution;
        Payment payment;
        DynamicForm form = Form.form().bindFromRequest();
        String paymentId = form.data().get("paymentId");
        String payerID = form.data().get("PayerID");
        String clientId = Play.application().configuration().getString("clientId");
        String secret = Play.application().configuration().getString("clientSecret");
        try {
            String accessToken = new OAuthTokenCredential(clientId,
                    secret).getAccessToken();
            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");
            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);
            payment = Payment.get(accessToken, paymentId);
            paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerID);

            //Executes a payment
            Payment response = payment.execute(context, paymentExecution);
            String saleId = response.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();

            Reservation reservation = Reservation.findByPaymentId(paymentId);
            reservation.status = ReservationStatus.APPROVED;
            reservation.sale_id = saleId;
            reservation.update();

            Room room = Room.findRoomById(reservation.room.id);
            room.roomType -= 1;
            room.update();
            flash("info");

        } catch (Exception e) {
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return redirect("/");
        }



        return ok(views.html.user.successfulPayment.render());
    }


    public static void executeRefund(String paymentId) {
        APIContext context;
        Model.Finder<String, Reservation> finder = new Model.Finder<String, Reservation>(Reservation.class);
        double totalPrice;
        try {
            String clientid = Play.application().configuration().getString("clientId");
            String secret = Play.application().configuration().getString("clientSecret");

            String accessToken = new OAuthTokenCredential(clientid, secret).getAccessToken();

            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");

            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);


            Reservation reservation = Reservation.findByPaymentId(paymentId);
            String saleId = reservation.sale_id;
            Logger.info("PAYMENT ID   " + paymentId);
            Sale sale = new Sale();
            Refund refund = new Refund();

                    totalPrice = reservation.cost.doubleValue();
                    String totalPriceString = String.format("%1.2f", totalPrice);
                    sale.setId(saleId);
                    Logger.info("SALE ID " + sale.getId());
                    Amount amount = new Amount();
                    amount.setCurrency("USD");
                    amount.setTotal(totalPriceString);
                    refund.setAmount(amount);
                    sale.refund(context, refund);
                    Logger.info("SALE REFUND " + sale.refund(context, refund));

                    reservation.isRefunded = true;
                    reservation.save();

                    Room room = Room.findRoomById(reservation.room.id);
                    room.roomType += 1;
                    room.update();

              

           Logger.info("Refounded done");
        } catch (PayPalRESTException e) {
            //flash("error", Messages.get("error.msg.02"));
            Logger.error("Error at purchaseProcessing: " + e.getMessage());

        }
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result setStatus(Integer reservationId) {
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
        Form<Reservation> boundForm = reservationForm.bindFromRequest();
        Reservation reservation = Reservation.findReservationById(reservationId);
        Room room = Reservation.findRoomByReservation(reservation);



        String status = boundForm.field("status").value();

        if (status.equals(ReservationStatus.APPROVED.toString())) {
            reservation.status = ReservationStatus.APPROVED;
            reservation.notification = ReservationStatus.NEW_NOTIFICATION;
            if(room.roomType > 0){
                room.roomType = room.roomType - 1;
            } else {
                flash("error", "All rooms of this type are booked");
            }

        } else if (status.equals(ReservationStatus.COMPLETED.toString())) {

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
        List<Reservation> reservationList = Reservation.finder.where().eq("user_id", userId).orderBy("create_date desc").findList();
        if (reservationList == null  || reservationList.size() == 0) {
            flash("info", "You have no reservations.");
            return redirect(routes.Application.index());
        }
        Room room = null;
        Hotel hotel = null;
        for (Reservation reservation : reservationList) {
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


    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result setStatusByUser(Integer resId) {
        DynamicForm form = Form.form().bindFromRequest();
        String reservationId = form.field("value").value();
        if(reservationId != null && reservationId.length() != 0) {
            Integer id = Integer.parseInt(reservationId);
            AppUser user = SessionsAndCookies.getCurrentUser(ctx());
            Form<Reservation> boundForm = reservationForm.bindFromRequest();

            Reservation reservation = Reservation.findReservationById(id);
            String paymentId = reservation.payment_id;

            Room room = Reservation.findRoomByReservation(reservation);
            reservation.status = ReservationStatus.CANCELED;
            executeRefund(paymentId);
            room.update();
            reservation.setUpdatedBy(user.firstname, user.lastname);
            reservation.update();

            List<Reservation> reservationList = Reservation.finder.where().eq("user_id", id).orderBy("create_date asc").findList();
            Hotel hotel = room.hotel;
            return ok(views.html.user.buyerReservations.render(room, hotel, reservationList, user));
        }
        return badRequest();

    }

}
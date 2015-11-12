package controllers;

import com.avaje.ebean.Model;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import helpers.*;
import models.*;
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
 * Created by Edvin Mulabdic on 9/29/15.
 */
public class Reservations extends Controller {

    private static PaymentExecution paymentExecution;
    private static Form<Reservation> reservationForm = Form.form(Reservation.class);


    /*Pay pal*/
    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result payPal(Integer roomId) {
        /*Taking information about reservation*/
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


            /*Configuration of pay pal*/
            String clientid = ConfigProvider.CLIENT_ID;
            String secret = ConfigProvider.CLIENT_SECRET;

            String token = new OAuthTokenCredential(clientid, secret).getAccessToken();

            Map<String, String> config = new HashMap<>();
            config.put("mode", "sandbox");

            APIContext context = new APIContext(token);
            context.setConfigurationMap(config);

            /* Process payment information */
            double price = reservation.cost.doubleValue();

            String priceString = String.format("%1.2f", price);
            String desc = "Costumer name: " + reservation.createdBy + "\n" +
                    "Reservation for hotel: " + room.hotel.name + "\n " +
                    "Check-in: " + reservation.checkIn + "\n" +
                    "Check-out" + reservation.checkOut + "\n" +
                    "Amount: " + priceString;

            /*Configure payment*/
            Amount amount = new Amount();
            amount.setTotal(priceString);
            amount.setCurrency("USD");

            /*Configure transaction*/
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


            /*Redirect links , if transaction is completed user will be redirected to execute payment
            * else, user will be notified that transaction is rejected*/
            RedirectUrls redirects = new RedirectUrls();
            redirects.setCancelUrl("http://localhost:9000/rejectPayment");
            redirects.setReturnUrl("http://localhost:9000/paypal/success");

            payment.setRedirectUrls(redirects);

            /*Creating payment, setting reservation status on pending until user pay reservation*/
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
            /*Executing payment*/
            Payment newPayment = payment.execute(context, paymentExecution );
            Logger.info("new PAYMENT "  + newPayment);

        } catch (PayPalRESTException e) {
            ErrorLogger.createNewErrorLogger("Failed execute PayPal.", e.getMessage());
            Logger.warn("PayPal Exception");
            e.printStackTrace();
        } catch (ParseException ex) {
            ErrorLogger.createNewErrorLogger("Failed to parse inputed dates in reservation.", ex.getMessage());
            System.out.println(ex.getMessage());
        }
        return redirect("/");
    }

    /*Executnig payment*/
    public Result paypalSuccess() {

        APIContext context;
        PaymentExecution paymentExecution;
        Payment payment;
        DynamicForm form = Form.form().bindFromRequest();
        String paymentId = form.data().get("paymentId");
        String payerID = form.data().get("PayerID");
        String clientId = ConfigProvider.CLIENT_ID;
        String secret = ConfigProvider.CLIENT_SECRET;
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
            /*Setting reservation status on approved, and update reservaton*/
            Reservation reservation = Reservation.findByPaymentId(paymentId);
            reservation.status = ReservationStatus.APPROVED;
            reservation.sale_id = saleId;
            reservation.update();
            /*Decreasing number of free romms of this type*/
            Room room = Room.findRoomById(reservation.room.id);
            room.roomType -= 1;
            room.update();
            flash("info");

            /*Sending a mail to user with his reservation*/
            AppUser user = AppUser.findUserById(Integer.parseInt(session("userId")));

            String message = String
                    .format("<html><body><strong> %s %s %s <br> %s <p> %s </p></strong> %s %s  <br> %s %s <br> %s %s %s <br> %s %s <br> %s %s <strong><p> %s <br> %s <br> %s </p></strong> <img src='%s'></body></html>",
                            "Hello ", user.firstname, ",",
                            "Your reservation has been successfully booked.",
                            "Reservation details:",
                            "FROM: ", CommonHelperMethods.getDateAsString(reservation.checkIn).toString(),
                            "TO: ", CommonHelperMethods.getDateAsString(reservation.checkOut).toString(),
                            "PRICE: ", reservation.cost, "$",
                            "HOTEL: ", room.hotel.name,
                            "CITY: ", room.hotel.city,
                            "Thank you for using our services. We wish you pleasant stay in our hotel.",
                            "Sincerely yours,",
                            "bitBooking team.",
                            ConfigProvider.LOGO);

            MailHelper.send(user.email, message, Constants.SUCCESSFUL_RESERVATION, null, null, null);

        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to receive PayPal succes.", e.getMessage());
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return redirect("/");
        }



        return ok(views.html.user.successfulPayment.render());
    }

    /*Allowing buyer to cancel reservation and refund money*/
    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public static void executeRefund(String paymentId) {
        APIContext context;
        Model.Finder<String, Reservation> finder = new Model.Finder<String, Reservation>(Reservation.class);
        double totalPrice;
        try {
            String clientid = ConfigProvider.CLIENT_ID;
            String secret = ConfigProvider.CLIENT_SECRET;

            String accessToken = new OAuthTokenCredential(clientid, secret).getAccessToken();

            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");

            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);

            /*Finding reservation in database by payment id, and executing refund*/
            Reservation reservation = Reservation.findByPaymentId(paymentId);
            String saleId = reservation.sale_id;
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
            /*Increasing number of available rooms*/
            Room room = Room.findRoomById(reservation.room.id);
            room.roomType += 1;
            room.update();

        } catch (PayPalRESTException e) {
            ErrorLogger.createNewErrorLogger("Failed to execute PayPal refund.", e.getMessage());
            Logger.error("Error at purchaseProcessing: " + e.getMessage());        }
    }

    /*Shows buyer his reservations */
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
            ErrorLogger.createNewErrorLogger("Failed to parse inputed dates in reservations (getPrice()).", e.getMessage());
            System.out.println(e.getMessage());
        }
        return ok(price.toString());
    }

    /*Allows buyer to cancel reservations*/
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
            //calling a method to execute refund
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
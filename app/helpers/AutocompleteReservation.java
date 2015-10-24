package helpers;

import models.Reservation;

/**
 * Created by boris on 10/18/15.
 */
public class AutocompleteReservation {

    private static final int ONE_MINUTE = 60000;
    private static final int ONE_HOUR = 3600000;
    private static final int ONE_DAY = 86400000;

    /**
     * Starts a thread every hour, thread calls method that completes
     * reservation if reservationCheckoutDate passed currentDate.
     */
    public static void completeReservations() {

        Runnable checkDates = () -> {

            while (!Thread.interrupted()) {
                try {
                    Reservation.checkReservationExpiration();
                    Thread.sleep(ONE_MINUTE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(checkDates).start();
    }

}

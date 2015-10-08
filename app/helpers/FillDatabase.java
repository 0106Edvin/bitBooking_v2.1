package helpers;

import models.AppUser;
import models.Hotel;

import java.util.Random;

/**
 * Created by boris.tomic on 05/10/15.
 */
public class FillDatabase {

    /*
        AppUser string arrays
     */
    private static final String[] FIRST_NAME_TEAM = {"Ajla", "Alen", "Amra", "Edvin", "Boris"};
    private static final String[] LAST_NAME_TEAM = {"El Tabari", "Bumbulovic", "Sabic", "Mulabdic", "Tomic"};
    private static final String[] EMAIL_TEAM = {"ajla.eltabari@bitcamp.ba", "alen.bumbulovic@bitcamp.ba",
            "amra.sabic@bitcamp.ba", "edvin.mulabdic@bitcamp.ba", "boris.tomic@bitcamp.ba"};
    private static final String[] PASSWORD_TEAM = {"Ajla123", "Alen123", "Amra123", "Edvin123", "Boris123"};

    private static final String[] FIRST_NAME_HOTEL_MANAGERS = {"Adis", "Adnan", "Ajdin", "Dinko"};
    private static final String[] LAST_NAME_HOTEL_MANAGERS = {"Cehajic", "Lapendic", "Brkic", "Hodzic"};
    private static final String[] EMAIL_HOTEL_MANAGERS = {"adis.cehajic@bitcamp.ba", "adnan.lapendic@bitcamp.ba",
            "ajdin.brkic@bitcamp.ba", "dinko.hodzic@bitcamp.ba"};

    private static final String[] FIRST_NAME_SELLERS = {"Edin", "Emina", "Gordan", "Hajrudin", "Kerim"};
    private static final String[] LAST_NAME_SELLERS = {"Pilavdzic", "Arapcic", "Masic", "Sehic", "Dragolj"};
    private static final String[] EMAIL_SELLERS = {"edin.pilavdzic@bitcamp.ba", "emina.arapcic@bitcamp.ba",
            "gordan.masic@bitcamp.ba", "hajrudin.sehic@bitcamp.ba", "kerim.dragolj@bitcamp.ba"};

    private static final String[] FIRST_NAME_BUYERS = {"Kristina", "Mladen", "Narena", "Ognjen", "Semir",
            "Tomislav", "Zeljko", "Medina", "Nidal", "Enver", "Becir", "Senadin"};
    private static final String[] LAST_NAME_BUYERS = {"Pupavac", "Teofilovic",
            "Ibrisimovic", "Cetkovic", "Sahman", "Trifunovic", "Miljevic", "Banjic", "Salkic", "Memic", "Omerbasic", "Botic"};
    private static final String[] EMAIL_BUYERS = {"kristina.pupavac@bitcamp.ba", "mladen.teofilovic@bitcamp.ba",
            "narena.ibrisimovic@bitcamp.ba", "ognjen.cetkovic@bitcamp.ba", "semir.sahman@bitcamp.ba",
            "tomislav.trifunovic@bitcamp.ba", "zeljko.miljevic@bitcamp.ba", "medina.banjic@bitcamp.ba",
            "nidal.salkic@bitcamp.ba", "enver.memic@bitcamp.ba", "becir.omerbasic@bitcamp.ba", "senadin.botic@bitcamp.ba"};

    private static final String[] NAME_HOTEL = { "Bristol", "Mirela", "Hayat", "Holiday Inn", "Courtyard by Marriott",
            "Europe", "President", "Courtyard by Marriott Belgrade", "Hotel Moskva", "Belgrade Art Hotel",
            "Hotel Dubrovnik", "Esplanade Zagreb Hotel", "Sheraton Zagreb Hotel", "The Westin Zagreb" };

    //private static final String[] ROOM

    /**
     * Creates a specific <code>AppUser</code>.
     * Input is taken from array of Strings depending which <code>AppUser</code>
     * is being created.
     * Teammembers have unique passwords, other passwords are default.
     * <p>
     *     Managers - Manager123
     * <p>
     *     Sellers - Seller123
     * <p>
     *     Buyers - Buyer123
     *
     */
    public static void createUsers() {
        for(int i = 0; i < FIRST_NAME_TEAM.length; i++) {
            AppUser teammate = new AppUser();
            teammate.email = EMAIL_TEAM[i];
            teammate.password = PASSWORD_TEAM[i];
            teammate.hashPass();
            teammate.firstname = FIRST_NAME_TEAM[i];
            teammate.lastname = LAST_NAME_TEAM[i];
            teammate.userAccessLevel = UserAccessLevel.ADMIN;
            teammate.phoneNumber = "123456789";
            teammate.save();
        }

        for(int i = 0; i < FIRST_NAME_HOTEL_MANAGERS.length; i++) {
            AppUser manager = new AppUser();
            manager.email = EMAIL_HOTEL_MANAGERS[i];
            manager.password = "Manager123";
            manager.hashPass();
            manager.firstname = FIRST_NAME_HOTEL_MANAGERS[i];
            manager.lastname = LAST_NAME_HOTEL_MANAGERS[i];
            manager.userAccessLevel = UserAccessLevel.HOTEL_MANAGER;
            manager.phoneNumber = "123456789";
            manager.save();
        }

        for(int i = 0; i < FIRST_NAME_SELLERS.length; i++) {
            AppUser manager = new AppUser();
            manager.email = EMAIL_SELLERS[i];
            manager.password = "Seller123";
            manager.hashPass();
            manager.firstname = FIRST_NAME_SELLERS[i];
            manager.lastname = LAST_NAME_SELLERS[i];
            manager.userAccessLevel = UserAccessLevel.SELLER;
            manager.phoneNumber = "123456789";
            manager.save();
        }

        for(int i = 0; i < FIRST_NAME_BUYERS.length; i++) {
            AppUser manager = new AppUser();
            manager.email = EMAIL_BUYERS[i];
            manager.password = "Buyer123";
            manager.hashPass();
            manager.firstname = FIRST_NAME_BUYERS[i];
            manager.lastname = LAST_NAME_BUYERS[i];
            manager.userAccessLevel = UserAccessLevel.BUYER;
            manager.phoneNumber = "123456789";
            manager.save();
        }

    }

    public static void createHotels() {
        Random rand = new Random();
        for (int i = 0; i < NAME_HOTEL.length; i++) {
            Hotel hotel = new Hotel();
            hotel.name = NAME_HOTEL[i];
            hotel.location = "Default adresa";
            hotel.description = "Auto generated hotel";
            hotel.stars = rand.nextInt(5) + 1;
            if (i < 7) {
                hotel.city = "Sarajevo";
                hotel.country = "Bosna i Hercegovina";
                hotel.coordinateX = "43.8534183";
                hotel.coordinateY = "18.37808399999";
                hotel.sellerId = 10;
            } else if (i >= 7 && i < 10) {
                hotel.city = "Beograd";
                hotel.country = "Srbija";
                hotel.coordinateX = "44.80188569365082";
                hotel.coordinateY = "20.45552998387666";
                hotel.sellerId = 11;
            } else if (i >= 10) {
                hotel.city = "Zagreb";
                hotel.country = "Hrvatska";
                hotel.coordinateX = "45.8116424954304";
                hotel.coordinateY = "15.963151749013377";
                hotel.sellerId = 12;
            }
            hotel.save();
        }
    }



}

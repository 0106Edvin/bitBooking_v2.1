package controllers;

import com.avaje.ebean.Model;
import models.AppUser;
import models.Hotel;
import models.HotelVisit;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ajla.eltabari on 29/10/15.
 */
public class Recommendations_HotelVisits extends Controller {

    //public static List<Hotel> recommendedHotels;
    public static Model.Finder<String, HotelVisit> finder = new Model.Finder<>(HotelVisit.class);

    List<HotelVisit> allHotelVisits = finder.findList();

    HashMap<Integer, List<AppUser>> usersThatClickedOnHotelMap;
    HashMap<Integer, List<Hotel>> hotelsThatAreClickedByUserMap;

    /**
     * Populates the map that contains hotel id as key and
     * list of users that already clicked on that hotel as value.
     */
    public void populateUsersThatClickedOnHotelMap() {
        usersThatClickedOnHotelMap = new HashMap<>();
        for (HotelVisit hv : allHotelVisits) {
            if (!usersThatClickedOnHotelMap.containsKey(hv.hotel.id)) {
                usersThatClickedOnHotelMap.put(hv.hotel.id, new ArrayList<>());
            }

            usersThatClickedOnHotelMap.get(hv.hotel.id).add(hv.user);
        }
    }

    /**
     * Populates the map that contains user id as key and
     * list of hotels that user has already clicked as value.
     */
    public void populateHotelsThatAreClickedByUserMap() {
        hotelsThatAreClickedByUserMap = new HashMap<>();
        for (HotelVisit hv : allHotelVisits) {
            if (!hotelsThatAreClickedByUserMap.containsKey(hv.user.id)) {
                hotelsThatAreClickedByUserMap.put(hv.user.id, new ArrayList<>());
            }

            hotelsThatAreClickedByUserMap.get(hv.user.id).add(hv.hotel);
        }
    }





    /**
     * If you do not know anything about current user
     * You should use this lost for recommendations.
     * @return
     */
    public static List<Hotel> returnMostPopularHotels() {
        List<Hotel> mostPopularHotels = new ArrayList<>();

        List<HotelVisit> mostPopularVisites =
                finder.orderBy("visits_no, visits_no desc")
                        .setMaxRows(6)
                        .findList();

        for(HotelVisit hv : mostPopularVisites) {
            mostPopularHotels.add(hv.hotel);
        }
        return mostPopularHotels;
    }

}

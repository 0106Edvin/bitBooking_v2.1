package controllers;

import com.avaje.ebean.Model;
import helpers.Constants;
import models.AppUser;
import models.Hotel;
import models.HotelVisit;
import play.Logger;
import play.mvc.Controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ajla.eltabari on 29/10/15.
 */
public class Recommendations_HotelVisits extends Controller {

    //public static List<Hotel> recommendedHotels;
    public static Model.Finder<String, HotelVisit> finder = new Model.Finder<>(HotelVisit.class);

    private static List<HotelVisit> allHotelVisits = finder.findList();

    private static HashMap<Integer, List<AppUser>> usersThatClickedOnHotelMap;
    private static HashMap<Integer, List<Hotel>> hotelsThatAreClickedByUserMap;

    /**
     * Populates the map that contains hotel id as key and
     * list of users that already clicked on that hotel as value.
     */
    private static void populateUsersThatClickedOnHotelMap() {
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
    private static void populateHotelsThatAreClickedByUserMap() {
        hotelsThatAreClickedByUserMap = new HashMap<>();
        for (HotelVisit hv : allHotelVisits) {
            if (!hotelsThatAreClickedByUserMap.containsKey(hv.user.id)) {
                hotelsThatAreClickedByUserMap.put(hv.user.id, new ArrayList<>());
            }

            hotelsThatAreClickedByUserMap.get(hv.user.id).add(hv.hotel);
        }
    }

    /**
     * For provided user, checks and makes the list of all his/hers visits.
     * Using that list, checks for other users that also visited those hotels.
     * After that, once again checks for all hotels that those users visited.
     * Makes the list, and returns random six hotels from this list, without
     * repeating.
     *
     * Shoould be used for logged users, because we have required information.
     * @param userId
     * @return
     */
    public static List<Hotel> getRecommendedHotelsForLoggedUsers(Integer userId) {

        if (hotelsThatAreClickedByUserMap == null || usersThatClickedOnHotelMap == null) {
            return returnMostPopularHotels();
        } else {
            List<Hotel> visitedHotelsByLoggedUser = hotelsThatAreClickedByUserMap.get(userId);
            List<Hotel> visitedHotelsByOtherUsers = new ArrayList<>();

            List<AppUser> usersWithSimilarInterests = new ArrayList<>();

            for (int i = 0; i < visitedHotelsByLoggedUser.size(); i++) {
                usersWithSimilarInterests = usersThatClickedOnHotelMap.get(visitedHotelsByLoggedUser.get(i));
            }

            for (int i = 0; i < usersWithSimilarInterests.size(); i++) {
                visitedHotelsByOtherUsers.addAll(hotelsThatAreClickedByUserMap.get(usersWithSimilarInterests.get(i)));
            }

            List<Hotel> listToDisplay = new ArrayList<>();
            List<Integer> indexes = new ArrayList<>();
            do {
                int index = (int)(Math.random() * visitedHotelsByOtherUsers.size() - 1);
                if (!indexes.contains(index)) {
                    indexes.add(index);
                    listToDisplay.add(visitedHotelsByOtherUsers.get(index));
                }
            } while (indexes.size() != Constants.RECOMMENDATIONS_NO);

            return listToDisplay;
        }
    }


    /**
     * Checks all user visits, finds most popular hotels,
     * orders them by number of visits desc, and returns
     * 6 random hotels from the most popular hotels list,
     * without repeating.
     *
     * Should be used for unlogged users, to recommend them
     * six currently most popular hotels on the site.
     * @return
     */
    public static List<Hotel> returnMostPopularHotels() {
        List<Hotel> mostPopularHotels = new ArrayList<>();
        List<Hotel> listToDisplay = new ArrayList<>();

        List<HotelVisit> allVisits = finder.all();

        List<HotelVisit> mostPopularVisits =
                finder.orderBy("visits_no, visits_no desc")
                        .setMaxRows(allVisits.size())
                        .findList();

        for(HotelVisit hv : mostPopularVisits) {
            if (!mostPopularHotels.contains(hv.hotel)) {
                mostPopularHotels.add(hv.hotel);
            }
        }

        if (mostPopularHotels.size() > 0) {
            int showLimit = 0;

            showLimit = (mostPopularHotels.size() <= Constants.RECOMMENDATIONS_NO) ? mostPopularHotels.size() - 1 : Constants.RECOMMENDATIONS_NO;

            List<Integer> indexes = new ArrayList<>();
            Logger.debug(allVisits.size() + "  size");

            do {
                int index = (int) (Math.random() * (allVisits.size() - 1));
                if (!indexes.contains(index)) {
                    indexes.add(index);
                    listToDisplay.add(mostPopularHotels.get(index));
                    Logger.debug(mostPopularHotels.get(index).toString());
                }

            } while (indexes.size() != showLimit);
        }

        return listToDisplay;

    }

}

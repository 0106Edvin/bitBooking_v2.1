package models;

import com.avaje.ebean.Model;
import helpers.CommonHelperMethods;
import helpers.Constants;
import helpers.ReservationStatus;
import helpers.UserAccessLevel;
import play.Logger;
import scala.App;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by boris on 10/10/15.
 */
@Entity
@Table(name = "site_stats")
public class SiteStats extends Model {

    public static final Integer INCREMENT_BY_ONE = 1;

    public static Finder<Integer, SiteStats> finder = new Finder<>(SiteStats.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false)
    public Integer id;
    @Column(name = "ip_address", unique = true, updatable = false, length = 40)
    public String ipAddress;
    @Column(name = "overall_visits")
    public Integer overallVisits = 1;
    @Column(name = "page_visited", length = 5, updatable = false)
    public String pageVisited;
    @Column(name = "hotel_id")
    public Integer hotelId;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    /**
     * Empty constructor for Ebean use.
     */
    public SiteStats() {
        // leave empty
    }

    /**
     * Finds number of unique page visits of site index page,
     * uniqueness parameter is ip address.
     *
     * @return <code>Integer</code> type value of number of page visits
     */
    public static Integer getUniquePageVisits() {
        return SiteStats.finder.findRowCount();
    }

    /**
     * Finds all users registered on BITBooking
     *
     * @return <code>Integer</code> type value of total number of users
     */
    public static Integer getTotalUsers() {
        return AppUser.finder.findRowCount();
    }

    /**
     * Goes tru all stats and calculates overall visits to index page.
     *
     * @return <code>Integer</code> type value of overall visits to index page,
     * if not stats are found 0 is returned.
     */
    public static Integer getTotalOfPageVisits() {
        List<SiteStats> stats = finder.all();
        Integer total = 0;
        for (SiteStats stat : stats) {
            total += stat.overallVisits;
        }
        return total;
    }

    /**
     * Finds number of all active users on site, active users are those who passed
     * email validation.
     *
     * @return <code>Integer</code> type value of active users on site
     */
    public static Integer getNumberOfActiveUsers() {
        return AppUser.finder.where().eq("validated", true).findRowCount();
    }

    /**
     * Finds number of all inactive users on site, inactive users are those who did not passed
     * email validation.
     *
     * @return <code>Integer</code> type value of inactive users on site
     */
    public static Integer getNumberOfInactiveUsers() {
        return AppUser.finder.where().eq("validated", false).findRowCount();
    }

    /**
     * Finds number of users by role entered.
     *
     * @param role <code>Integer</code> type value of user role
     * @return <code>Integer</code> type value of number of users by entered role
     */
    public static Integer getNumberOfUsersByRole(Integer role) {
        return AppUser.finder.where().eq("user_access_level", role).findRowCount();
    }

    /**
     * Finds all hotels operated by seller.
     *
     * @param seller <code>Integer</code> type value of seller id
     * @return <code>List</code> type value of sellers <code>Hotel</code>
     */
    public static List<Hotel> getManagersHotels(Integer seller) {
        return Hotel.finder.where().eq("seller_id", seller).findList();
    }

    /**
     * Finds number of visits in given <code>Hotel</code> visits are added if
     * reservation status is completed.
     *
     * @param hotel <code>Hotel</code> type object
     * @return <code>Integer</code> type value of number of visits in inputed hotel
     */
    public static Integer getNumberOfVisitsPerHotel(Hotel hotel) {
        Integer visits = 0;
        List<Room> rooms = hotel.rooms;
        for (Room room : rooms) {
            List<Reservation> reservations = room.reservations;
            for (Reservation res : reservations) {
                if (res.status.equals(ReservationStatus.COMPLETED)) {
                    visits++;
                }
            }
        }
        return visits;
    }

    /**
     * Finds number of visits in given <code>Room</code> visits are added if
     * reservation status is completed.
     *
     * @param room <code>Hotel</code> type object
     * @return <code>Integer</code> type value of number of visits in inputed room
     */
    public static Integer getNumberOfVisitsPerRoom(Room room) {
        Integer visits = 0;
        List<Reservation> reservations = room.reservations;
        for (Reservation res : reservations) {
            if (res.status.equals(ReservationStatus.COMPLETED)) {
                visits++;
            }
        }
        return visits;
    }

    /**
     * Sorts list of rooms, most visited is first.
     *
     * @param hRooms <code>List</code> type value of <code>Room</code>
     * @return <code>Room</code> type value with most visits
     */
    public static Room getMostVisitedRoom(List<Room> hRooms) {
        Collections.sort(hRooms, new Comparator<Room>() {
            public int compare(Room o1, Room o2) {
                return o2.reservations.size() - (o1.reservations.size());
            }
        });
        return hRooms.get(0);
    }

    /**
     * Number of features in given hotel
     *
     * @param hotel <code>Hotel</code> type object
     * @return <code>Integer</code> type value of number of features in inputed hotel
     */
    public static Integer getNumberOfHotelFeatures(Hotel hotel) {
        return hotel.features.size();
    }

    /**
     * List of countries in <code>Hotel</code> operated by seller.
     * If countries repeat they are not included in list.
     *
     * @param seller <code>Integer</code> type value seller id
     * @return <code>List</code> type value of country name string
     */
    public static List<String> getHotelCountries(Integer seller) {
        List<String> countries = new ArrayList<>();
        List<Hotel> hotels = getManagersHotels(seller);
        for (Hotel hotel : hotels) {
            if (!countries.contains(hotel.country)) {
                countries.add(hotel.country);
            }
        }
        return countries;
    }

    /**
     * +
     * Number of hotels in countrie.
     *
     * @param seller  <code>Integer</code> type value of seller id
     * @param country <code>String</code> type value of country name
     * @return <code>Integer</code> type value of hotels in inputed country
     */
    public static Integer getNumberOfHotelsByCountry(Integer seller, String country) {
        return Hotel.finder.where().eq("seller_id", seller).eq("country", country).findRowCount();
    }

    /**
     * Calculates total amount of money hotel earned.
     *
     * @param hotel <code>Hotel</code> type object
     * @return <code>BigDecimal</code> type value of total amount
     */
    public static BigDecimal getTotalAmountPerHotel(Hotel hotel) {
        BigDecimal total = new BigDecimal(0);
        List<Room> rooms = hotel.rooms;
        for (Room room : rooms) {
            List<Reservation> reservations = room.reservations;
            for (Reservation res : reservations) {
                if (res.status.equals(ReservationStatus.COMPLETED)) {
                    total = total.add(res.getCost());
                }
            }
        }
        return total;
    }

    /**
     * Method used to set createdBy <code>String</code> type value with creator name and surname.
     * Use this method if AppUser is not <code>null</code>. Otherwise set createdBy field with
     * some String directly.
     *
     * @param firstName <code>String</code> type value of user first name
     * @param lastName  <code>String</code> type value of user last name
     */
    public void setCreatedBy(String firstName, String lastName) {
        this.createdBy = firstName + " " + lastName;
    }

    /**
     * Method used to set updatedBy <code>String</code> type value with creator name and surname.
     * Use this method if AppUser is not <code>null</code>. Otherwise set updatedBy field with
     * some String directly.
     *
     * @param firstName <code>String</code> type value of user first name
     * @param lastName  <code>String</code> type value of user last name
     */
    public void setUpdatedBy(String firstName, String lastName) {
        this.updatedBy = firstName + " " + lastName;
    }

    /**
     * Overrided update method, added overallVisits increment by one
     * when method is called, and updateDate is initialized.
     */
    @Override
    public void update() {
        overallVisits += INCREMENT_BY_ONE;
        updateDate = new Date();
        super.update();
    }


}


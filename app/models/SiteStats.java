package models;

import com.avaje.ebean.Model;
import helpers.Constants;
import helpers.ReservationStatus;
import helpers.UserAccessLevel;
import scala.App;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     *
     * @return
     */
    public static Integer getUniquePageVisits() {
        return SiteStats.finder.findRowCount();
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
     * Users overall
     *
     * @return
     */
    public static Integer getNumberOfUsers() {
        return AppUser.finder.findRowCount();
    }

    /**
     *
     * @param role
     * @return
     */
    public static Integer getNumberOfUsersByRole(Integer role) {
        return AppUser.finder.where().eq("user_access_level", role).findRowCount();
    }

    public static List<Hotel> getManagersHotels(Integer seller) {
        return Hotel.finder.where().eq("seller_id", seller).findList();
    }

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

    public static Integer getNumberOfHotelFeatures(Hotel hotel) {
        return hotel.features.size();
    }

    public static List<String> getHotelCountries(Integer seller) {
        List<String> countries = new ArrayList<>();
        List<Hotel> hotels = getManagersHotels(seller);
        for (Hotel hotel : hotels) {
            if(!countries.contains(hotel.country)) {
                countries.add(hotel.country);
            }
        }
        return countries;
    }

    public static Integer getNumberOfHotelsByCountry(Integer seller, String country) {
        return Hotel.finder.where().eq("seller_id", seller).eq("country", country).findRowCount();
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


package models;

import com.avaje.ebean.Model;
import helpers.Constants;
import play.data.Form;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Edvin Mulabdic on 9/6/2015.
 */
@Entity
public class Hotel extends Model {
    public static Finder<String, Hotel> finder = new Finder<>(Hotel.class);
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);

    @Id
    public Integer id;
    public String name;
    public String location;

    @Column(columnDefinition = "TEXT")
    public String description;

    public String city;
    public String country;
    public String coordinateX;
    public String coordinateY;
    public Integer sellerId;
    private Double rating = Constants.INITIAL_RATING;

    @Column(name = "page_visits")
    public Integer hotelPageVisits = 0;
    @Column(name = "stars", length = 1)
    public Integer stars;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    public List<Image> images;

    @OneToMany(mappedBy = "hotel")
    public List<Room> rooms;

    @OneToMany(mappedBy = "hotel")
    public List<Comment> comments;

    @Column
    public Boolean showOnHomePage;

    @OneToMany
    public HotelVisit hotelVisit;

    /**
     * Default empty constructor for Ebean use
     */
    public Hotel() {
    };

    public Hotel(Integer id, String name, String location, String description, String city, String country, List<Feature> features, List<Comment> comments, String coordinateX, String coordinateY, Integer stars, List<Room> rooms, Integer sellerId, List<Image> images, Double rating, Boolean showOnHomePage, HotelVisit hotelVisit) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.city = city;
        this.country = country;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.sellerId = sellerId;
        this.images = images;
        this.rooms = rooms;
        this.comments = comments;
        this.stars = stars;
        this.rating = Constants.INITIAL_RATING;
        this.showOnHomePage = showOnHomePage;
        this.hotelVisit = hotelVisit;
    }

    //method that finds hotel by id
    public static Hotel findHotelById(Integer id) {
        Hotel hotel = finder.where().eq("id", id).findUnique();

        return hotel;
    }

    public static List<Hotel> getAllHotels() {
        return finder.all();
    }

    public static List<Hotel> searchHotels(Date first, Date second, String term) {

        List<Hotel> finalHotels = new ArrayList<>();
        int checkNumbers = finder.where().betweenProperties("rooms.reservations.checkIn", "rooms.reservations.checkOut", first).betweenProperties("rooms.reservations.checkIn", "rooms.reservations.checkOut", second).gt("rooms.roomType", 0).findRowCount();

        List<Hotel> hotels = null;

        if (checkNumbers == 0) {
            hotels = finder.where().gt("rooms.roomType", 0).orderBy("rating desc").orderBy().desc("rating").orderBy().asc("name").findList();
        } else {
            hotels = finder.where().betweenProperties("rooms.reservations.checkIn", "rooms.reservations.checkOut", first).betweenProperties("rooms.reservations.checkIn", "rooms.reservations.checkOut", second).gt("rooms.roomType", 0).orderBy().desc("rating").orderBy().asc("name").findList();
        }
        String column = "";

        Map<String, Integer> map = new HashMap<>();
        map.put("name", Hotel.findHotelsByName(term));
        map.put("country", Hotel.findHotelsByCountry(term));
        map.put("city", Hotel.findHotelsByCity(term));
        map.put("stars", Hotel.findHotelsByStars(term));
        map.put("rooms.prices.cost", Hotel.findHotelsByPrice(term));
        map.put("comments.rating", Hotel.findHotelsByRating(term));

        int maxValueInMap = (Collections.max(map.values()));

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
                column = entry.getKey();
            }
        }

        List<Hotel> searchedHotels = finder.where().ilike(column, "%" + term + "%").orderBy().desc("rating").orderBy().asc("name").findList();

        for (Hotel h1 : hotels) {
            for (Hotel h2 : searchedHotels) {
                if (h1.equals(h2)) {
                    finalHotels.add(h2);
                }
            }
        }
        return finalHotels;
    }

    private static int findHotelsByName(String term) {
        return finder.where().ilike("name", "%" + term + "%").findRowCount();
    }

    private static int findHotelsByCountry(String term) {
        return finder.where().ilike("country", "%" + term + "%").findRowCount();
    }

    private static int findHotelsByCity(String term) {
        return finder.where().ilike("city", "%" + term + "%").findRowCount();
    }

    private static int findHotelsByStars(String term) {
        return finder.where().ilike("stars", term).findRowCount();
    }

    private static int findHotelsByPrice(String term) {
        return finder.where().ilike("rooms.prices.cost", term).findRowCount();
    }

    private static int findHotelsByRating(String term) {
        return finder.where().ilike("comments.rating", term).findRowCount();
    }

    /**
     * Method used for rendering list of hotels when seller creates promotion.
     *
     * @param seller <code>AppUser</code> type value of seller
     * @return <code>List</code> of hotels operated by inputed seller ordered by hotel name ascending
     */
    public static List<Hotel> getHotelsBySellerAndSortedByNameAscending(AppUser seller) {
        return finder.where().eq("seller_id", seller.id).orderBy("name asc").findList();
    }

    /**
     * Changes seller of specific hotel
     *
     * @param hotelId     <code>Integer</code> type value of hotel id
     * @param sellerEmail <code>String</code> type value of seller email
     * @return <code>boolean</code> type value true if successfully updated, false if not
     */
    public static boolean changeSeller(Integer hotelId, String sellerEmail) {
        AppUser seller = AppUser.getUserByEmail(sellerEmail);
        Hotel hotel = Hotel.findHotelById(hotelId);
        if (hotel != null && seller != null) {
            try {
                hotel.sellerId = seller.id;
                hotel.update();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to change seller of hotel.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return (id.toString() + " " + name + " " + location);
    }

    public Double getRating() {
        rating = Constants.INITIAL_RATING;
        if (comments != null && comments.size() > 0) {
            for (int i = 0; i < comments.size(); i++) {
                rating += comments.get(i).rating;
            }
            rating = rating / comments.size();
        }
        DecimalFormat format = new DecimalFormat(Constants.INITIAL_RATING.toString());
        rating = Double.valueOf(format.format(rating));
        return rating;
    }


    public static AppUser findUserByHotel(Hotel hotel) {
        Integer userId = hotel.sellerId;
        AppUser user = AppUser.findUserById(userId);
        return user;
    }

    public static AppUser findUserByHotelId(Integer hotelId) {
        Hotel h = findHotelById(hotelId);
        Integer userId = h.sellerId;
        AppUser user = AppUser.findUserById(userId);
        return user;
    }

    /**
     * Sets hotel visibility on homepage.
     * Only hotel managers should be able to call this method.
     *
     * @param hotel
     * @param visibility
     */
    public static void setHotelVisibilityOnHomePage(Hotel hotel, Boolean visibility) {
        hotel.showOnHomePage = visibility;
        hotel.save();
    }

    /**
     * Returns only hotels marked to be visible on the homepage.
     *
     * @return
     */
    public static List<Hotel> hotelsForHomepage() {
        List<Hotel> hotels = finder.where().eq("showOnHomePage", true).orderBy("rating desc").findList();
        return hotels;
    }

    @Override
    public void update() {
        rating = getRating();
        updateDate = new Date();
        hotelPageVisits += SiteStats.INCREMENT_BY_ONE;
        super.update();
    }

    /**
     * Returns the number of hotels shown on homepage
     *
     * @return
     */

    public static void saveHotel(AppUser user) {
        Form<Hotel> hotelForm = Form.form(Hotel.class);
        Form<Hotel> boundForm = hotelForm.bindFromRequest();
        Hotel hotel = boundForm.get();

        Integer sellerId = Integer.parseInt(boundForm.field("seller").value());

        hotel.sellerId = sellerId;
        hotel.showOnHomePage = Constants.SHOW_HOTEL_ON_HOMEPAGE;
        hotel.save();
        Message.sendEmailToSeller(sellerId, hotel);


        List<Feature> features = featureFinder.all();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.field(features.get(i).id.toString()).value();


            if (feature != null) {
                HotelFeature hotelFeature = new HotelFeature();
                hotelFeature.feature = features.get(i);
                hotelFeature.hotel = hotel;
                hotelFeature.setCreatedBy(user);
                hotelFeature.save();
            }
        }
    }


}
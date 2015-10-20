package models;

import com.avaje.ebean.Model;
import helpers.Constants;
import play.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Edvin Mulabdic on 9/6/2015.
 */

@Entity
public class Hotel extends Model {
    public static Finder<String, Hotel> finder = new Finder<>(Hotel.class);

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
    private Double rating;

    @Column(name = "page_visits")
    public Integer hotelPageVisits = 0;
    @Column(name="stars", length = 1)
    public Integer stars;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @ManyToMany
    public List<Feature> features;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="hotel")
    public List<Image> images;

    @OneToMany(mappedBy="hotel")
    public List<Room> rooms;

    @OneToMany(mappedBy="hotel")
    public List<Comment> comments;

    /**
     * Default empty constructor for Ebean use
     */
    public Hotel() {};

    public Hotel(Integer id, String name, String location, String description, String city, String country, List<Feature> features, List<Comment> comments, String coordinateX, String coordinateY, Integer stars, List<Room> rooms, Integer sellerId, List<Image> images,Double rating) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.city = city;
        this.country = country;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.sellerId = sellerId;
        this.features = features;
        this.images = images;
        this.rooms = rooms;
        this.comments = comments;
        this.stars = stars;
        this.rating = Constants.INITIAL_RATING;
    }

    //method that finds hotel by id
    public static Hotel findHotelById(Integer id) {
        Hotel hotel = finder.where().eq("id", id).findUnique();

        return hotel;
    }

    public static List<Hotel> searchHotels(String field, String term) {

        if(field != null && term != null) {
            if ("name".equals(field)) {
                return Hotel.findHotelsByName(term);
            } else if ("country".equals(field)) {
                return Hotel.findHotelsByCountry(term);
            } else if ("city".equals(field)) {
                return Hotel.findHotelsByCity(term);
            } else if ("stars".equals(field)) {
                return Hotel.findHotelsByStars(term);
            } else if ("price".equals(field)) {
                return Hotel.findHotelsByPrice(term);
            } else if ("rating".equals(field)) {
                return Hotel.findHotelsByRating(term);
            }
        }
        return null;
    }

    private static List<Hotel> findHotelsByName(String term){
        return finder.where().ilike("name", "%" + term + "%").findList();
    }

    private static List<Hotel> findHotelsByCountry(String term){
        return finder.where().ilike("country", "%" + term + "%").findList();
    }

    private static List<Hotel> findHotelsByCity(String name){
        return finder.where().ilike("city", "%" + name + "%").findList();
    }

    private static List<Hotel> findHotelsByStars(String name){
        return finder.where().ilike("stars", name).findList();
    }

    private static List<Hotel> findHotelsByPrice(String name) {
        return finder.where().ilike("rooms.prices.cost", "%" + name + "%").findList();
    }

    private static List<Hotel> findHotelsByRating(String name) {
        return finder.where().ilike("comments.rating", name).findList();
    }

    @Override
    public String toString() {
        return (id.toString() + " " + name + " " + location);
    }

    public Double getRating() {
        rating = Constants.INITIAL_RATING;
        if(comments != null && comments.size() > 0) {
            for (int i = 0; i < comments.size(); i++) {
                rating += comments.get(i).rating;
            }
            rating = rating / comments.size();
        }
        DecimalFormat format = new DecimalFormat(Constants.INITIAL_RATING.toString());
        rating = Double.valueOf(format.format(rating));
        return rating;
    }
    public static AppUser findUserByHotel (Hotel hotel){
        Integer userId = hotel.sellerId;
        AppUser user = AppUser.findUserById(userId);
        return user;
    }

    @Override
    public void update() {
        updateDate = new Date();
        hotelPageVisits += SiteStats.INCREMENT_BY_ONE;
        super.update();
    }
}
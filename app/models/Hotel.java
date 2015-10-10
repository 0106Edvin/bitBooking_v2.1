package models;

import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import helpers.Constants;

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

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    @OneToMany
    public List<Room> rooms;

    @OneToMany
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

    public static List<Hotel> findHotelsByName(String name){
        return finder.where().contains("name", name).findList();
    }
    public static List<Hotel> findHotelsByCountry(String name){
        return finder.where().contains("country",name).findList();
    }
    public static List<Hotel> findHotelsByCity(String name){
        return finder.where().contains("city", name).findList();
    }

    public static List<Hotel> findHotelsByStars(String name){
        return finder.where().contains("stars", name).findList();
    }

    public static List<Hotel> findHotelsByPrice(String name) {
        List<Hotel> foundHotels = new ArrayList<>();
        List<Hotel> hotels = new ArrayList<>();
        hotels = finder.all();
        try {
            for (Hotel h : hotels) {
                for (int i = 0; i < h.rooms.size(); i++) {
                    if (h.rooms.get(i).prices.contains(BigDecimal.valueOf(Double.parseDouble(name)))) {
                        foundHotels.add(h);
                    }
                }
            }
        } catch (NumberFormatException e) {
            Logger.error("Could't parse given string", e.getCause());
        }
        return foundHotels;
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

}
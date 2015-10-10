package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by ajla.eltabari on 05/10/15.
 */
@Entity
public class Restaurant extends Model {
    public static Finder<String, Restaurant> finder = new Finder<>(Restaurant.class);

    @Id
    public Integer id;
    public String name;
    public String restauranType;
    public Integer capacity;
    public String workingHours;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @OneToOne
    public Hotel hotel;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date timestamp;

    public Restaurant(Integer id, String name, String restauranType, Integer capacity, String workingHours, String description, Hotel hotel, List<Image> images, Date timestamp) {
        this.id = id;
        this.name = name;
        this.restauranType = restauranType;
        this.capacity = capacity;
        this.workingHours = workingHours;
        this.description = description;
        this.hotel = hotel;
        this.images = images;
        this.timestamp = timestamp;
    }

    //method that finds restaurant by hotel_id
    public static Restaurant findRestaurantByHotelId(Integer hotelId) {
        Restaurant restaurant = finder.where().eq("hotel_id", hotelId).findUnique();

        return restaurant;
    }

    //method that finds restaurant by id
    public static Restaurant findRestaurantById(Integer restaurantId) {
        Restaurant restaurant = finder.where().eq("id", restaurantId).findUnique();

        return restaurant;
    }

    /**
     * Checks if restaurant with provided hotelId exists in database.
     *
     * @param hotelId
     * @returns true if restaurant exists, and false if it doesn't.
     */
    public static Boolean existsInDB(Integer hotelId) {
        Restaurant restaurant = finder.where().eq("hotel_id", hotelId).findUnique();
        return (restaurant == null) ? false : true;
    }
}

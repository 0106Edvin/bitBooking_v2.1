package models;

import com.avaje.ebean.Model;

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
    public String type;

    @Column(columnDefinition = "TEXT")
    public String description;

    @OneToOne
    public Hotel hotel;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    public String timestamp;

    public Restaurant(Integer id, String name, String type, String description, Hotel hotel, List<Image> images) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.hotel = hotel;
        this.images = images;
        this.timestamp = new Date().toString();
    }

    //method that finds restaurant by hotel_id
    public static Restaurant findRestaurantByHotelId(Integer hotelId) {
        Restaurant restaurant = finder.where().eq("hotel_id", hotelId).findUnique();

        return restaurant;
    }
}

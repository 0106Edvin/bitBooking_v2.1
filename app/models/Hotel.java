package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
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
    public String description;
    public String city;
    public String country;
    public String coordinateX;
    public String coordinateY;
    public Integer sellerId;

    @ManyToMany
    public List<Feature> features;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    @OneToMany
    public List<Room> rooms;

    @OneToMany
    public List<Comment> comments;

    public Hotel(Integer id, String name, String location, String description, String city, String country, List<Feature> features, List<Comment> comments, String coordinateX, String coordinateY, List<Room> rooms, Integer sellerId, List<Image> images) {

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


    }

    //method that finds hotel by id
    public static Hotel findHotelById(Integer id) {
        Hotel hotel = finder.where().eq("id", id).findUnique();

        return hotel;
    }

    @Override
    public String toString() {
        return (id.toString() + " " + name + " " + location);
    }

}
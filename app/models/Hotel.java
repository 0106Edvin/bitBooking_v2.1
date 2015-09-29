package models;

import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private Double rating;

    @ManyToMany
    public List<Feature> features;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    @OneToMany
    public List<Room> rooms;

    @OneToMany
    public List<Comment> comments;


    public Hotel(Integer id, String name, String location, String description, String city, String country, List<Feature> features, List<Comment> comments, String coordinateX, String coordinateY, List<Room> rooms, Integer sellerId, List<Image> images,Double rating) {

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
        this.rating = 0.0;
    }

    //method that finds hotel by id
    public static Hotel findHotelById(Integer id) {
        Hotel hotel = finder.where().eq("id", id).findUnique();

        return hotel;
    }

    public static List<Hotel> findHotelsByName(String name){
        return finder.where().eq("name",name).findList();
    }
    public static List<Hotel> findHotelsByCountry(String name){
        return finder.where().eq("country",name).findList();
    }
    public static List<Hotel> findHotelsByCity(String name){
        return finder.where().eq("city",name).findList();
    }

    @Override
    public String toString() {
        return (id.toString() + " " + name + " " + location);
    }

    public Double getRating(){
        rating = 0.0;
        if(comments != null && comments.size() > 0) {
            Logger.info("Hellooooooooooooooooooooooooooooooo");

            for (int i = 0; i < comments.size(); i++) {
                rating += comments.get(i).rating;
            }
            rating = rating / comments.size();
        }
        DecimalFormat format = new DecimalFormat("0.0");
        rating = Double.valueOf(format.format(rating));
        return rating;
    }
}
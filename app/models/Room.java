package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.List;

/**
 * Created by User on 9/16/2015.
 */
@Entity
public class Room extends Model {
    public static Finder<String, Room> finder = new Finder<String, Room>(Room.class);

    @Id
    public Integer id;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Digits(integer=3, fraction=0)
    @Constraints.Min(1)
    @Constraints.Max(30)
    @Constraints.Required
    public Integer numberOfBeds;

    public String name;

    @ManyToMany
    public List<Feature> features;

    @ManyToOne
    public Hotel hotel;

    @OneToMany
    public List<Price> prices;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;

    @OneToMany
    public List<Reservation> reservations;

    public Room(){}


    public Room(Integer id, String description, String name, List<Feature> features, Hotel hotel, Integer numberOfBeds, List<Price> prices , List<Image>images, List<Reservation> reservations){
        this.id = id;
        this.description= description;
        this.features = features;
        this.name = name;
        this.hotel = hotel;
        this.numberOfBeds= numberOfBeds;
        this.prices = prices;
        this.images = images;
        this.reservations = reservations;
    }
    public static Room findRoomById(Integer id) {
        Room room = finder.where().eq("id", id).findUnique();
        return room;
    }
}

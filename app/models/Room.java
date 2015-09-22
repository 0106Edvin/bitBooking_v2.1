package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by User on 9/16/2015.
 */
@Entity
public class Room extends Model {
    public static Finder<String, Room> finder = new Finder<String, Room>(Room.class);

    @Id
    public Integer id;
    public String description;
    public Integer numberOfBeds;
    public String name;
    @ManyToMany
    public List<Feature> features;

    @ManyToOne
    public Hotel hotel;

//    @OneToMany
//    public List<Price> prices;

    public Room(){

    }
    public Room(Integer id, String description, String name, List<Feature> features, Hotel hotel, Integer numberOfBeds){//, List<Price> prices
        this.id = id;
        this.description= description;
        this.features = features;
        this.name = name;
        this.hotel = hotel;
        this.numberOfBeds= numberOfBeds;
//        this.prices = prices;
    }
    public static Room findRoomById(Integer id) {
        Room room = finder.where().eq("id", id).findUnique();

        return room;

    }
}
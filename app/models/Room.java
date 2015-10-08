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

    @Digits(integer = 3, fraction = 0)
    @Constraints.Min(1)
    @Constraints.Max(30)
    @Constraints.Required
    public Integer numberOfBeds;

    public String name;

    public Integer roomType;

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

    public Room() {
    }


    public Room(Integer id, String description, String name,Integer roomType,List<Feature> features, Hotel hotel, Integer numberOfBeds, List<Price> prices , List<Image>images, List<Reservation> reservations){

        this.id = id;
        this.description = description;
        this.features = features;
        this.name = name;
        this.hotel = hotel;
        this.numberOfBeds = numberOfBeds;
        this.prices = prices;
        this.images = images;
        this.reservations = reservations;
        this.roomType= roomType;
    }

    public static Room findRoomById(Integer id) {
        Room room = finder.where().eq("id", id).findUnique();
        return room;
    }

    /**
     * Method checks if <code>Hotel</code> have <code>Room</code> with name
     * that already exists. If it doesn't returns true, if does returns false.
     *
     * @param roomName <code>String</code> type value of <code>Room</code> name
     * @param hotelId  <code>Integer</code> type value of <code>Hotel</code> id
     * @return true if there is no same room name in hotel, false if room name already exists
     */
    public static boolean checkRoomName(String roomName, Integer hotelId) {
        List<Room> rooms = finder.where().eq("hotel_id", hotelId).findList();
        for (Room room : rooms) {
            if (room.name.equals(roomName)) {
                return false;
            }
        }
        return true;
    }

}

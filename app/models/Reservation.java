package models;

import com.avaje.ebean.Model;
import helpers.ReservationStatus;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by gordan on 9/29/15.
 */
@Entity
public class Reservation extends Model {

    public static Finder<String, Reservation> finder = new Finder<String, Reservation>(Reservation.class);

    @Id
    public Integer id;

    public Double cost;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date checkIn;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date checkOut;

    public Integer status;

    @ManyToOne
    public Room room;

    @ManyToOne
    public AppUser user;

    public Reservation(Integer id, Double cost, Date checkIn, Date checkOut, Room room, AppUser user) {
        this.id = id;
        this.cost = cost;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = ReservationStatus.PENDING;
        this.room = room;
        this.user = user;
    }

    @Override
    public String toString(){
        return String.format("%s has reserved %s room from %s till %s for %s",user.firstname,room.name,checkIn,checkOut,cost);
    }

    public Reservation findReservationById(Integer id){
        Reservation reservation = finder.where().eq("id",id).findUnique();
        return reservation;
    }
}

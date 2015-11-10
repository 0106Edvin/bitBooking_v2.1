package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ajla on 10/28/15.
 */
@Entity
public class HotelVisit extends Model {

    public static Finder<String, HotelVisit> finder = new Finder<>(HotelVisit.class);

    @Id
    public Integer id;
    @ManyToOne
    @JsonBackReference
    public Hotel hotel;
    @ManyToOne
    @JsonBackReference
    public AppUser user;
    @Column
    public Integer visitsNo;

    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();


    public HotelVisit(Hotel hotel, AppUser user, Integer visitsNo) {
        this.hotel = hotel;
        this.user = user;
        this.visitsNo = visitsNo;
    }

    public static void createOrUpdateVisit(Hotel hotel, AppUser user) {
        HotelVisit visit = finder.where().eq("hotel_id", hotel.id).where().eq("user_id", user.id).findUnique();
        if (visit == null) {
            HotelVisit newVisit = new HotelVisit(hotel, user, 1);
            newVisit.save();
        } else {
            visit.visitsNo += 1;
            visit.save();
        }
    }
}

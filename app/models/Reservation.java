package models;

import com.avaje.ebean.Model;
import helpers.ReservationStatus;
import helpers.SessionsAndCookies;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by gordan on 9/29/15.
 */
@Entity
public class Reservation extends Model {

    public static Finder<String, Reservation> finder = new Finder<String, Reservation>(Reservation.class);

    @Id
    public Integer id;

    public BigDecimal cost;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date checkIn;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date checkOut;

    public Integer status;

    @Column(name = "notification", length = 1)
    public Integer notification;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @ManyToOne
    public Room room;

    @ManyToOne
    public AppUser user;


    public Reservation(){}

    public Reservation(Integer id, BigDecimal cost, Date checkIn, Date checkOut, Room room, AppUser user, Date timeOfReservation) {
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

    public static Reservation findReservationById(Integer id){
        Reservation reservation = finder.where().eq("id",id).findUnique();
        return reservation;
    }
    
    public static List<Reservation> findReservationByUserId(Integer id){
        List<Reservation> reservationList = finder.where().eq("user_id", id).findList();
        return reservationList;
    }

    public static Room findRoomByReservation(Reservation reservation){
        Room room = reservation.room;
        return room;
    }

    public BigDecimal getCost() {
        cost = new BigDecimal(0);
        Date myDate = checkIn;
        for(Price price: room.prices) {
            while (myDate.compareTo(checkOut) <= 0 ) {
                if (myDate.compareTo(price.dateFrom) >= 0 && myDate.compareTo(price.dateTo) <= 0) {
                    cost = cost.add(price.cost);
                } else {
                    break;
                }
                myDate = org.apache.commons.lang.time.DateUtils.addDays(myDate, 1);
            }
        }
        return cost;
    }


    public void setCreatedBy(String firstName, String lastName) {
        this.createdBy = firstName + " " + lastName;
    }

    public void setUpdatedBy(String firstName, String lastName) {
        this.updatedBy = firstName + " " + lastName;
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

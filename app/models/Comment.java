package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;


@Entity
public class Comment extends Model {
    public static Finder<String, Comment> finder = new Finder<String, Comment>(Comment.class);
    /*
     *Comment atributes
     */
    @Id
    public Integer id;
    @ManyToOne
    public AppUser user;

    @ManyToOne
    public Hotel hotel;

    public String title;
    public String content;
    public Double rating;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();



    /*
     *Default constructor
     */
    public Comment(Integer id, AppUser user, Hotel hotel, String title, String content, Double rating) {
        this.id = id;
        this.user = user;
        this.hotel = hotel;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public static Comment findCommentById (Integer id){
        Comment comment = finder.where().eq("id", id).findUnique();
        return comment;
    }

    public static boolean userAlreadyCommentedThisHotel(String email, Hotel hotel) {
        AppUser user = AppUser.getUserByEmail(email);
        Comment comment = finder.where().eq("user_id", user.id).where().eq("hotel_id", hotel.id).findUnique();
        if (comment != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean userHasRightsToCommentThisHotel(String email, Hotel hotel) {
        AppUser user = AppUser.getUserByEmail(email);

        List<Reservation> reservations = Reservation.findReservationsByHotelAndUserIds(hotel.id, user);

        return  (reservations.size() > 0) ? true : false;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", user_id=" + user +
                ", hotel_id=" + hotel +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                '}';
    }
}

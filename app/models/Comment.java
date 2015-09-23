package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


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
    public Integer rating;

    /*
     *Default constructor
     */
    public Comment(Integer id, AppUser user, Hotel hotel, String title, String content, Integer rating) {
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

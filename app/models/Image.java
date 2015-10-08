package models;


import com.avaje.ebean.Model;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import play.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import play.Play;

@Entity
public class Image extends Model {

    @Id
    public Integer id;

    public String public_id;

    public String image_url;

    public String secret_image_url;

    @ManyToOne
    public Hotel hotel;

    @ManyToOne
    public Room room;

    @OneToOne(mappedBy = "profileImg")
    public AppUser user;

    @OneToOne
    public Feature feature;

    @ManyToOne
    public Restaurant restaurant;


    public static Cloudinary cloudinary;


    public static Finder<Integer, Image> find = new Finder<Integer, Image>(Image.class);

    public static Image createImage(String public_id, String image_url, String secret_image_url, Hotel hotel, Room room, Feature feature, Restaurant restaurant, AppUser user) {
        Image i = new Image();
        i.public_id = public_id;
        i.image_url = image_url;
        i.secret_image_url = secret_image_url;
        i.hotel = hotel;
        i.room = room;
        i.user = user;
        i.feature = feature;
        i.restaurant = restaurant;
        i.save();
        return i;
    }

    public static Image create(String public_id, String image_url, String secret_image_url) {
        Image i = new Image();
        i.public_id = public_id;
        i.image_url = image_url;
        i.secret_image_url = secret_image_url;
        i.save();
        return i;
    }

    public static Image create(File image,Integer hotelId, Integer userId, Integer featureId, Integer roomId, Integer restaurantId) {
        Map result;
        try {
            result = cloudinary.uploader().upload(image, null);
            return create(result, hotelId, userId, featureId, roomId, restaurantId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image create(Map uploadResult, Integer hotelId, Integer userId, Integer featureId, Integer roomId, Integer restaurantId) {
        Image i = new Image();

        i.public_id = (String) uploadResult.get("public_id");
        Logger.debug(i.public_id);
        i.image_url = (String) uploadResult.get("url");
        Logger.debug(i.image_url);
        i.secret_image_url = (String) uploadResult.get("secure_url");
        Logger.debug(i.secret_image_url);
        if(hotelId != null) {
            i.hotel = Hotel.findHotelById(hotelId);
        } else if(userId != null) {
            i.user = AppUser.findUserById(userId);
        } else if (featureId != null) {
            i.feature = Feature.findFeatureById(featureId);
        } else if (roomId != null) {
            i.room = Room.findRoomById(roomId);
        } else if (restaurantId != null) {
            i.restaurant = Restaurant.findRestaurantById(restaurantId);
        }
        i.save();
        return i;
    }

    public static List<Image> all() {
        return find.all();
    }


    public String getSize(int width, int height) {
        try {
            String url;

            url = cloudinary.url().format("jpg")
                    .transformation(new Transformation().width(width).height(height)).generate(public_id);

            return url;
        }catch (NullPointerException e){
            return "null";
        }


    }

    public String getThumbnail(){
        String url = cloudinary.url().format("png")
                .transformation(
                        new Transformation().width(150).height(150).crop("thumb").gravity("face").radius("max")
                )
                .generate(public_id);
        return url;
    }

    public void deleteImage() {

        try {
            cloudinary.uploader().destroy(public_id, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
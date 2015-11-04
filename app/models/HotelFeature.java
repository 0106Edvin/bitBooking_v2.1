package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import helpers.Constants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by boris on 10/29/15.
 */
@Entity
@Table(name = "hotel_feature")
public class HotelFeature extends Model {

    public static Finder<Integer, HotelFeature> finder = new Finder<>(HotelFeature.class);

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    @Column(name = "is_free")
    public Boolean isFree = Constants.FEATURE_FREE;
    @Column(name = "price")
    public String price;
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    @JsonBackReference
    public Hotel hotel;
    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "id")
    @JsonBackReference
    public Feature feature;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    /**
     * Default constructor for Ebean use
     */
    public HotelFeature() {
        // leave empty
    }

    /**
     * Used to save feature when seller adds them to hotel.
     *
     * @param id
     * @return
     */
    public static List<HotelFeature> getFeaturesByHotelId(Integer id) {
        return finder.where().eq("hotel.id", id).findList();
    }

    /**
     * Used when updating hotel, and possibly feature price
     *
     * @param hotelId
     * @param featureId
     * @return
     */
    public static HotelFeature getHotelFeatureByHotelIdAndFeatureId(Integer hotelId, Integer featureId) {
        return finder.where().eq("hotel.id", hotelId).eq("feature.id", featureId).findUnique();
    }

    /**
     * Method is used in SiteStats
     *
     * @param hotel
     * @return
     */
    public static int getFeaturesByHotel(Hotel hotel) {
        return finder.where().eq("hotel", hotel).findRowCount();
    }

    /**
     * Sets created by string in format name surname of sender
     *
     * @param user <code>AppUser</code> type value of sender
     */
    public void setCreatedBy(AppUser user) {
        createdBy = user.firstname + " " + user.lastname;
    }

    /**
     * Sets updated by string in format name surname of sender
     *
     * @param user <code>AppUser</code> type value of sender
     */
    public void setUpdatedBy(AppUser user) {
        updatedBy = user.firstname + " " + user.lastname;
    }

    @Override
    public void update() {
        updateDate = new Date();
        super.update();
    }
}

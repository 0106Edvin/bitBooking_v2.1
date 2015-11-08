package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Feature extends Model {
    public static Finder<String, Feature> finder = new Finder<>(Feature.class);

    @Id
    public Integer id;
    @Column(unique = true)
    public String name;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @OneToOne
    @JsonBackReference
    public Image icon;

    @ManyToMany(mappedBy = "features")
    @JsonBackReference
    public List<Room> rooms;

    /**
     * Empty constructor for Ebean use
     */
    public Feature() {
        //leave empty
    }

    public Feature(String name) {
        this.name = name;
    }

    public Feature(Integer id, String name,Image icon, List<Hotel> hotels){
        this.id = id;
        this.name = name;
        this.icon = icon;

    }
    //finding createFeature by id
    public static Feature findFeatureById(Integer id) {
        Feature feature = finder.where().eq("id", id).findUnique();

        return feature;
    }

}

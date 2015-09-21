package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Feature extends Model {
    public static Finder<String, Feature> finder = new Finder<>(Feature.class);

    @Id
    public Integer id;
    public String name;

    @ManyToMany(mappedBy = "features")
    public List<Hotel> hotels;

    public Feature(Integer id, String name, List<Hotel> hotels){
        this.id = id;
        this.name = name;
        this.hotels = new LinkedList<Hotel>(hotels);
        for(Hotel h: hotels){
            h.features.add(this);
        }
    }
    //finding createFeature by id
    public static Feature findFeatureById(Integer id) {
        Feature feature = finder.where().eq("id", id).findUnique();

        return feature;
    }

    public String toString() {
        return id + " " + name;
    }

}

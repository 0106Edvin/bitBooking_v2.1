package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by ajla.eltabari on 05/10/15.
 */
public class Restaurant extends Model {
    @Id
    public Integer id;
    public String name;
    public String type;

    @Column(columnDefinition = "TEXT")
    public String description;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;



}

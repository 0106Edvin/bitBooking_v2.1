package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by ajla on 11/12/15.
 */
@Entity
public class Course extends Model {

    public static Finder<String, Course> finder = new Finder<String, Course>(Course.class);

    @Id
    public Integer id;

    public String name;
    public String description;
    public Double price;
    public Integer quantity;

    public Course(String name, String description, Double price, Integer quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public static List<Course> getAvailableCourses() {
        return finder.where().gt("quantity", 0).findList();
    }

    public static Course getCourseByCourseId(Integer courseId) {
        return finder.where().eq("id", courseId).findUnique();
    }

}

package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by boris on 10/10/15.
 */
@Entity
@Table(name = "site_stats")
public class SiteStats extends Model {

    public static final String INDEX_PAGE = "index";
    public static final String HOTEL_PAGE = "hotel";

    private static final Integer INCREMENT_BY_ONE = 1;

    public static Finder<Integer, SiteStats> finder = new Finder<>(SiteStats.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false)
    public Integer id;
    @Column(name = "ip_address", unique = true, updatable = false, length = 40)
    public String ipAddress;
    @Column(name = "overall_visits")
    public Integer overallVisits = 1;
    @Column(name = "page_visited", length = 5, updatable = false)
    public String pageVisited;
    @Column(name = "hotel_id")
    public Integer hotelId;
    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    /**
     * Empty constructor for Ebean use.
     */
    public SiteStats() {
        // leave empty
    }

    /**
     * Goes tru all stats and calculates overall visits to index page.
     *
     * @return <code>Integer</code> type value of overall visits to index page,
     * if not stats are found 0 is returned.
     */
    public Integer getTotalOfPageVisits() {
        List<SiteStats> stats = finder.all();
        Integer total = 0;
        for (SiteStats stat : stats) {
            total += stat.overallVisits;
        }
        return total;
    }

    /**
     * Method used to set createdBy <code>String</code> type value with creator name and surname.
     * Use this method if AppUser is not <code>null</code>. Otherwise set createdBy field with
     * some String directly.
     *
     * @param firstName <code>String</code> type value of user first name
     * @param lastName  <code>String</code> type value of user last name
     */
    public void setCreatedBy(String firstName, String lastName) {
        this.createdBy = firstName + " " + lastName;
    }

    /**
     * Method used to set updatedBy <code>String</code> type value with creator name and surname.
     * Use this method if AppUser is not <code>null</code>. Otherwise set updatedBy field with
     * some String directly.
     *
     * @param firstName <code>String</code> type value of user first name
     * @param lastName  <code>String</code> type value of user last name
     */
    public void setUpdatedBy(String firstName, String lastName) {
        this.updatedBy = firstName + " " + lastName;
    }

    /**
     * Overrided update method, added overallVisits increment by one
     * when method is called, and updateDate is initialized.
     */
    @Override
    public void update() {
        overallVisits += INCREMENT_BY_ONE;
        updateDate = new Date();
        super.update();
    }


}

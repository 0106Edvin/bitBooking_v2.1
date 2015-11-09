package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.data.Form;
import play.data.format.Formats;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Alen Bumbulovic on 9/17/2015.
 */
@Entity
public class Price extends Model {
    public static final Form<Price> priceForm = Form.form(Price.class);
    public static Finder<String, Price> finder = new Finder<String, Price>(Price.class);

    @Id
    public Integer id;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date dateFrom;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date dateTo;

    public BigDecimal cost;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();

    @ManyToOne
    @JsonBackReference
    public Room room;

    public Price() {
    }

    public Price(Integer id, Date dateFrom, Date dateTo, BigDecimal cost, Room room) {
        this.id = id;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.cost = cost;
        this.room = room;
    }

    /**
     * Creates new price for selected room. Date for price is validated before being passed to this method.
     *
     * @param room       <code>Room</code> type value of selected room
     * @param firstDate  <code>Date</code> type value of starting date for price
     * @param secondDate <code>Date</code> type value of ending date for price
     * @param cost       <code>String</code> type value of cost, string is converted to BigDecimal by getCostFromString method
     * @return <code>boolean</code> type value true if price is successfully added, false if not
     */
    public static boolean createNewPrice(Room room, Date firstDate, Date secondDate, String cost) {
        Price price = new Price();
        if (room != null) {
            price.dateFrom = firstDate;
            price.dateTo = secondDate;
            price.room = room;
            price.cost = getCostFromString(cost);
            try {
                price.save();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to save new price.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes inputted price.
     *
     * @param price <code>Price</code> type value that is being deleted
     * @return <code>boolean</code> type value true if price is successfully deleted, false if not
     */
    public static boolean deletePrice(Price price) {
        try {
            price.delete();
            return true;
        } catch (PersistenceException e) {
            ErrorLogger.createNewErrorLogger("Failed to delete price.", e.getMessage());
            return false;
        }
    }

    /**
     * Updates price with new cost value.
     *
     * @param price <code>Price</code> type value of price being updated
     * @param cost  <code>String</code> type value of new cost, string is converted to BigDecimal by getCostFromString method
     * @return boolean type value true if price is updated correctly, false if not
     */
    public static boolean updatePrice(Price price, String cost) {
        try {
            price.cost = getCostFromString(cost);
            price.update();
            return true;
        } catch (PersistenceException e) {
            ErrorLogger.createNewErrorLogger("Failed to update price.", e.getMessage());
            return false;
        }
    }

    /**
     * Converts inputed string in BigDecimal value of cost.
     *
     * @param cost <code>String</code> type value of price cost
     * @return <code>BigDecimal</code> type value of cost if parsed correctly, <code>null</code> in not
     */
    private static BigDecimal getCostFromString(String cost) {
        try {
            return new BigDecimal(Long.parseLong(cost));
        } catch (NumberFormatException e) {
            ErrorLogger.createNewErrorLogger("Failed to parse inputed price.", e.getMessage());
            return null;
        }
    }

    public static Price findPriceById(Integer id) {
        return finder.where().eq("id", id).findUnique();
    }

    public String toString() {
        return dateFrom + " " + dateTo + " " + cost.toString();
    }

    public static List<Price> getRoomPrices(Room room) {
        return finder.where().eq("room", room).findList();
    }

}
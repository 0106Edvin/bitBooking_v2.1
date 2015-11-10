package models;

/**
 * Model HotelForNavigator is created for the purpose of
 * connecting with bitNavigator. They need list of hotels
 * as Json, but they don't need all information about the hotel.
 *
 * That is why we created this model, that contains only
 * necessary information about the hotel.
 * 
 * Created by ajla on 11/11/15.
 */
public class HotelForNavigator {

    public String name;
    public String description;
    public String coordinateX;
    public String coordinateY;

    public HotelForNavigator(String name,  String coordinateX, String coordinateY, String description) {
        this.name = name;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.description = description;
    }

    @Override
    public String toString() {
        return "HotelForNavigator{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coordinateX='" + coordinateX + '\'' +
                ", coordinateY='" + coordinateY + '\'' +
                '}';
    }
}

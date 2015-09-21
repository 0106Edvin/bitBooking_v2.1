package controllers;

import com.avaje.ebean.Model;
import models.Feature;
import models.Hotel;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.hotel.createhotel;

import java.util.ArrayList;
import java.util.List;

public class Hotels extends Controller {

    private static final Form<Hotel> hotelForm = Form.form(Hotel.class);
    private static Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);
    public static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);
//    public static Model.Finder<String, Room> roomFinder = new Model.Finder<String, Room>(Room.class);

    /*   Saving hotel to data base*/
    public Result saveHotel() {

        Form<Hotel> boundForm = hotelForm.bindFromRequest();
        Hotel hotel = boundForm.get();

        List<Feature> features = listOfFeatures();
        //Getting values from checkboxes
        List<String> checkBoxValues = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            String feature = boundForm.bindFromRequest().field(features.get(i).name).value();

            if (feature != null) {
                checkBoxValues.add(feature);
            }

            Logger.debug(checkBoxValues.toString());
        }

        List<Feature> featuresForHotel = new ArrayList<Feature>();

        for (int i = 0; i < checkBoxValues.size(); i++) {
            for (int j = 0; j < features.size(); j++) {
                if (features.get(j).name.equals(checkBoxValues.get(i))) {
                    featuresForHotel.add(features.get(j));
                }
            }
        }



        hotel.features = featuresForHotel;

        hotel.save();
        return redirect(routes.Application.index());

    }
    public Result createHotel(){
        List<Feature> features = Hotels.featureFinder.all();
        return ok(createhotel.render(features));
    }

    public List<Feature> listOfFeatures() {
        List<Feature> features = featureFinder.all();
        return features;
    }

}
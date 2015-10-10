package controllers;

import com.avaje.ebean.Model;
import controllers.routes;
import models.Feature;
import models.Image;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.feature.createFeature;
import helpers.*;

import java.io.File;

/**
 * Created by ajla.eltabari on 09/09/15.
 */
public class ApiFeatures extends Controller {

    private static final String token = "rockITNekiPravoKOmplikovanTokenKOjiCEUmrijetiZaca";
    private Form<Feature> featureForm = Form.form(Feature.class);

    public Result createFeature(){
        return ok(createFeature.render());
    }

    private static Model.Finder<String, Feature> finder = new Model.Finder<>(Feature.class);

    public Result saveFeature(String token1) {
        if(ApiTokenHelper.isValid(token)) {
            Form<Feature> boundForm = featureForm.bindFromRequest();

            Feature feature = null;

            try {
                feature = boundForm.get();
                feature.save();

                feature.save();

                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart filePart = body.getFile("image");
                if (filePart != null) {
                    File file = filePart.getFile();
                    Image icon1 = Image.create(file, null, null, feature.id, null, null);
                    icon1.save();
                    feature.icon = icon1;

                }
                feature.update();

                return ok(Json.toJson(feature));

            } catch (Exception e) {
                flash("error", "Feature with same name already exists in our database, please try again!");
                return ok(createFeature.render());
            }
        } else {
            String tokenErr = "403: Unauthorized access.";
            return ok(Json.toJson(tokenErr));
        }
    }



    public Result deleteFeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        feature.delete();

        return redirect(routes.Users.showAdminFeatures());
    }

    public Result editfeature(Integer id) {
        Feature feature = Feature.findFeatureById(id);
        return ok(Json.toJson(feature));
    }

    public Result updateFeature(Integer id){
        Form<Feature> boundForm = featureForm.bindFromRequest();
        Feature feature = Feature.findFeatureById(id);

        String name = boundForm.bindFromRequest().field("name").value();

        feature.name = name;

        feature.update();

        return ok(Json.toJson(feature));
    }



}

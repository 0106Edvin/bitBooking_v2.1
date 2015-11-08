package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
import models.ErrorLogger;
import models.Feature;

import models.Image;
import play.Logger;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.feature.createFeature;
import views.html.feature.updateFeature;
import views.html.admin.adminFeatures;

import java.io.File;
import java.util.List;

/**
 * Created by ajla.eltabari on 09/09/15.
 */
public class Features extends Controller {

    private Form<Feature> featureForm = Form.form(Feature.class);
    private static Model.Finder<String, Feature> finder = new Model.Finder<>(Feature.class);

    public Result createFeature(){
        return ok(createFeature.render());
    }



    /*This method saves features to database*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result saveFeature() {
        Form<Feature> boundForm = featureForm.bindFromRequest();
        String isFree = boundForm.field("isFree").value();

        Feature feature = null;

        try {
            feature = boundForm.get();
            feature.save();

            /* Allows admin to add pictures for features*/
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart filePart = body.getFile("image");
            if(filePart != null){
                File file = filePart.getFile();
                Image icon1 = Image.create(file, null, null, feature.id, null, null);
                icon1.save();
                feature.icon = icon1;

            }
            feature.update();
            return redirect(routes.Users.showAdminFeatures());
        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to save new feature.", e.getMessage());
            return ok(createFeature.render());
        }



    }
    /* This method allows admin to delete features from database */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteFeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        feature.delete();

        return redirect(routes.Users.showAdminFeatures());
    }

    /* This method redirect to edit view for feature  */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result editfeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        return ok(updateFeature.render(feature));
    }


    /* This method allows admin to update features from database */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateFeature(Integer id){
        Form<Feature> boundForm = featureForm.bindFromRequest();
        Feature feature = Feature.findFeatureById(id);

        String name = boundForm.field("name").value();

        feature.name = name;

        feature.update();

        return redirect(routes.Users.showAdminFeatures());
    }

}

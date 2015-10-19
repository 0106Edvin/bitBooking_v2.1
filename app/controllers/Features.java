package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
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

    public Result createFeature(){
        return ok(createFeature.render());
    }

    private static Model.Finder<String, Feature> finder = new Model.Finder<>(Feature.class);



    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result saveFeature() {
        Form<Feature> boundForm = featureForm.bindFromRequest();
        String isFree = boundForm.field("isFree").value();

        Feature feature = null;

        try {
            feature = boundForm.get();
            if (isFree == null) {
                feature.isFree = false;
            } else {
                feature.isFree = true;
            }

            feature.save();

            feature.save();

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
            //flash("error", "Feature with same name already exists in our database, please try again!");
            return ok(createFeature.render());
        }



    }

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteFeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        feature.delete();

        return redirect(routes.Users.showAdminFeatures());
    }

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result editfeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        return ok(updateFeature.render(feature));
    }

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

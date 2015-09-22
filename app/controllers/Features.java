package controllers;

import models.Feature;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.feature.createFeature;
import views.html.feature.updateFeature;

/**
 * Created by ajla.eltabari on 09/09/15.
 */
public class Features extends Controller {

    private Form<Feature> featureForm = Form.form(Feature.class);

    public Result createFeature(){
        return ok(createFeature.render());
    }


    public Result saveFeature() {
        Form<Feature> boundForm = featureForm.bindFromRequest();

        Feature feature = boundForm.get();

        feature.save();
        return redirect(routes.Application.index());
    }



    public Result deleteFeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        feature.delete();

        return redirect(routes.Application.index());
    }


    public Result editfeature(Integer id){
        Feature feature = Feature.findFeatureById(id);
        return ok(updateFeature.render(feature));
    }

    public Result updateFeature(Integer id){
        Form<Feature> boundForm = featureForm.bindFromRequest();
        Feature feature = Feature.findFeatureById(id);

        String name = boundForm.bindFromRequest().field("name").value();

        feature.name = name;

        feature.update();

        return redirect(routes.Application.index());
    }



}

import com.cloudinary.Cloudinary;
import helpers.FillDatabase;
import models.AppUser;
import models.Hotel;
import models.Image;
import play.GlobalSettings;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;


/**
 * Created by ajla.eltabari on 22/09/15.
 */
public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<Result>pure(notFound(notFound.render()));
    }

    @Override
    public F.Promise<Result> onBadRequest(RequestHeader request, String error) {
        return F.Promise.<Result>pure(badRequest(notFound.render()));
    }

    @Override
    public void onStart(Application application) {
      Image.cloudinary = new Cloudinary("cloudinary://" + Play.application().configuration().getString("cloudinary.string"));

        if(AppUser.finder.findRowCount() == 0) {
            FillDatabase.createUsers();
        }
        if (Hotel.finder.findRowCount() == 0) {
            FillDatabase.createHotels();
        }
    }

//    @Override
//    public <T extends EssentialFilter> Class<T>[] filters() {
//        Class[] filters = {CSRFFilter.class};
//        return filters;
//
//    }
}


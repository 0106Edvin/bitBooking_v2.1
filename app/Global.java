import com.cloudinary.Cloudinary;
import helpers.AutocompleteReservation;
import helpers.FillDatabase;
import models.AppUser;
import models.Hotel;
import models.Image;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import views.html.notFound;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;


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

        /**
         * Calls a thread that will check reservations every hour.
         * If reservation checkoutDate passed currentDate they will
         * be set as completed.
         */
        AutocompleteReservation.completeReservations();
        Image.cloudinary = new Cloudinary("cloudinary://" + Play.application().configuration().getString("cloudinary.string"));

    }
}


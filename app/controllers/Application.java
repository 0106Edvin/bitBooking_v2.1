package controllers;

import models.Hotel;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.list;

import java.util.List;

public class Application extends Controller {

    public Result index() {
//        return ok(index.render());
        List<Hotel> hotels = Hotel.finder.all();
        return ok(list.render(hotels));
    }

}

package controllers;

import helpers.Authenticators;
import models.AppUser;
import models.Hotel;
import models.Message;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

/**
 * Created by boris on 10/24/15.
 */
public class MessageController extends Controller {

    @Security.Authenticated(Authenticators.BuyerFilter.class)
    public Result saveMessage(Integer hotelId) {
        AppUser user = AppUser.getUserByEmail(session("email"));

        DynamicForm form = Form.form().bindFromRequest();
        String subject = form.field("subject").value();
        String content = form.field("content").value();

        if (Message.createNewMessage(subject, content, hotelId, user)) {
            flash("info", "Message successfully sent.");
            return redirect(routes.Hotels.showHotel(hotelId));
        }
        flash("error-search", "Could not send message!");
        return redirect(routes.Application.index());
    }

    public Result allMessages() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        List<Message> messages = Message.finder.all();
        return ok(views.html.user.messages.render(messages, user));
    }
}

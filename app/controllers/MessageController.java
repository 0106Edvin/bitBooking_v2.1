package controllers;

import helpers.Authenticators;
import helpers.Constants;
import models.AppUser;
import models.Hotel;
import models.Message;
import play.Logger;
import play.Play;
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
        if (user == null) {
            flash("error-search", "Log in to send message.");
            return redirect(routes.Application.index());
        }

        DynamicForm form = Form.form().bindFromRequest();
        String subject = form.field("subject").value();
        String content = form.field("content").value();

        if (Message.createNewMessageFromHotelPage(subject, content, hotelId, user)) {
            flash("info", "Message successfully sent.");
            return redirect(routes.Hotels.showHotel(hotelId));
        }
        flash("error-search", "Could not send message!");
        return redirect(routes.Application.index());
    }

    public Result replyMessage(Integer userId) {
        Logger.debug("entered");
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to reply to message.");
            return redirect(routes.Application.index());
        }

        DynamicForm form = Form.form().bindFromRequest();
        String subject = form.field("subject").value();
        String content = form.field("content").value();

        if (Message.createReplyMessage(subject, content, userId, user)) {
            flash("info", "Message successfully sent.");
            return redirect(routes.MessageController.allMessages());
        }
        flash("error-search", "Could not send message!");
        return redirect(routes.Application.index());
    }

    public Result allMessages() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to see messages.");
            return redirect(routes.Application.index());
        }
        List<Message> messages = Message.finder.orderBy("id desc").findList();
        return ok(views.html.user.messages.render(messages, user));
    }

    public Result readMessage(Integer messageId) {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to see message.");
            return redirect(routes.Application.index());
        }
        Message message = Message.finder.byId(messageId);
        if (Message.clearanceToRead(message, user)) {
            return ok(views.html.user.readMessage.render(message));
        }
        return badRequest("You don't have right permission!" +
                "\n\nYour ip is recorded! " +  request().remoteAddress().toString());
    }

    public Result notification() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        Integer number = Message.numberOfNewMessages(user);
        return ok(String.valueOf(number));
    }

    public Result deleteMessageFromInbox(Integer messageId) {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to delete message.");
            return redirect(routes.Application.index());
        }
        if (Message.deleteMessageFromInbox(messageId, user)) {
            flash("info", "Message successfully deleted.");
            return redirect(routes.MessageController.allMessages());
        }
        return internalServerError();
    }

    public Result deleteMessageFromOutbox(Integer messageId) {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to delete message.");
            return redirect(routes.Application.index());
        }
        if (Message.deleteMessageFromOutbox(messageId, user)) {
            flash("info", "Message successfully deleted.");
            return redirect(routes.MessageController.allMessages());
        }
        return internalServerError();
    }
}

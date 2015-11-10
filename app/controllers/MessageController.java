package controllers;

import helpers.Authenticators;
import models.AppUser;
import models.ErrorLogger;
import models.Message;
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

    /**
     * Saves new message when buyer engages seller from hotel page.
     * If user is missing from session it's redirected to index page.
     * Otherwise inputed fields are collected by <code>DynamicForm</code> and
     * sent to <code>Message</code> model method that creates new message.
     * If successfull page is reloaded, otherwise user is redirected to index page
     * with appropriate error message.
     *
     * @param hotelId <code>Integer</code> type value of hotel id, used to find seller from that hotel
     * @return redirect to index if error occurs, redirect to hotel page
     */
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

    /**
     * Used when replying to a message via modal. If user is not logged in it,s redirected to index.
     * Otherwise value from input fields is collected by <code>DynamicForm</code> and data is
     * sent to <code>Message</code> createReplyMessage method, that saves message. If successfull
     * user is redirected to it's inbox, if not it's redirected to index page.
     *
     * @param userId <code>Integer</code> type value of sender's id
     * @return redirect to index if error occurs, redirect to all messages page
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result replyMessage(Integer userId) {
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

    /**
     * Used to render view that shows inbox and outbox with all messages, messages are sorted descending.
     * If user is not logged in it's redirected to index page, otherwise messages are shown.
     *
     * @return redirect to index if error occurs, or ok and render all message page
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result allMessages() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        if (user == null) {
            flash("error-search", "Log in to see messages.");
            return redirect(routes.Application.index());
        }
        List<Message> messages = Message.finder.orderBy("id desc").findList();
        return ok(views.html.user.messages.render(messages, user));
    }

    /**
     * Used to render single message selected from inbox or outbox.
     * If user is not logged in it's redirected to index page.
     * Otherwise message is found by id and sent to method that checks clearance.
     * If user wrote message, or it's sent to him/her it can be opened otherwise
     * a bad request is send.
     *
     * @param messageId <code>Integer</code> type value of message id
     * @return redirect to index if error occurs or bad request if tring to read message by entering
     * it's id to browser address, ok is returned and message rendered
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
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
        ErrorLogger.createNewErrorLogger("POSSIBLE SECURITY BREACH. User tried to read message " + messageId, "IP: " + request().remoteAddress().toString());
        return badRequest("You don't have right permission!" +
                "\n\nYour ip is recorded! " + request().remoteAddress().toString());
    }

    /**
     * Returns number of new unread messages to ajax request.
     *
     * @return ok with nmber of new unread messages
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result notification() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        Integer number = Message.numberOfNewMessages(user);
        return ok(String.valueOf(number));
    }

    /**
     * Checks if user is logged in, if not it's redirected to index page.
     * Othewrwise message is deleted from inbox only in this user's inbox.
     * If message can't be deleted internal server error is returned.
     *
     * @param messageId <code>Integer</code> type value of message id
     * @return redirect to index if error occurs or internalServerError if message cant be deleted,
     * redirect to all messages page if deleted successfully
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
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

    /**
     * Checks if user is logged in, if not it's redirected to index page.
     * Othewrwise message is deleted from inbox only in this user's outbox.
     * If message can't be deleted internal server error is returned.
     *
     * @param messageId <code>Integer</code> type value of message id
     * @return redirect to index if error occurs or internalServerError if message cant be deleted,
     * redirect to all messages page if deleted successfully
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
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
package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.*;
import models.*;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.adminFeatures;
import views.html.admin.adminHotels;
import views.html.admin.adminPanel;
import views.html.admin.adminUsers;
import views.html.hotel.createhotel;
import views.html.manager.managerHotels;
import views.html.user.*;

import java.io.File;
import java.util.List;
import java.util.UUID;


/**
 * Created by ajla on 9/20/15.
 */
public class Users extends Controller {
    private static final Form<AppUser> userForm = Form.form(AppUser.class);

    //    private static List<Hotel> hotels = Hotel.finder.all();
    private static Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);

    private static List<AppUser> users = AppUser.finder.all();
    private static Model.Finder<String, AppUser> userFinder = new Model.Finder<>(AppUser.class);

    private static List<Feature> features = Feature.finder.all();
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);


    /**
     * Renders register form for registering new user to site.
     *
     * @return
     */
    public Result registerUser() {
        return ok(register.render(userForm));
    }

    /**
     * Tries to save new user to database. Takes values from input fields, validates them
     * and assignees them to AppUser parameters.
     *
     * @param userType <code>String</code> type value of userType, can be seller or regular buyer
     * @return
     */
    public Result saveUser(String userType) {
        Form<AppUser> boundForm = userForm.bindFromRequest();

        String pass1 = boundForm.field("password").value();
        String pass2 = boundForm.field("passwordretype").value();
        String name = boundForm.field("firstname").value();
        String lastname = boundForm.field("lastname").value();
        String phone = boundForm.field("phoneNumber").value();
        String email = boundForm.field("email").value();

        Result r = CommonHelperMethods.validateUser(pass1, pass2, name, lastname, phone, email, boundForm);
        if (r != null) {
            return r;
        }

        AppUser user = boundForm.get();

        if (AppUser.saveNewUser(user, userType)) {
            if (Constants.USER_BUYER.equals(userType)) {
                String host = Play.application().configuration().getString("url") + "validate/" + user.token;
                MailHelper.send(user.email, host, Constants.REGISTER, null, null, null);

                flash("registration-msg", "Thank you for joining us. You need to verify your email address. Check your email for verification link.");
                return ok(login.render(userForm));
            }
            flash("registration-msg", "Thank you for joining us.");
            return ok(login.render(userForm));
        }
        flash("error", "Email already exists in our database, please try again!");
        return ok(register.render(boundForm));
    }

    /**
     * Collects user info from the login form, calls method for authentication,
     * and if it is successful, logs the user in. Stores the data in the cookies
     * and redirect user to the corresponding profile page.
     *
     * @return
     */
    public Result login() {
        Form<AppUser> boundForm = userForm.bindFromRequest();
        String email = boundForm.field("email").value();
        String password = boundForm.field("password").value();

        AppUser user = AppUser.authenticate(email, password);

        if (user != null && !user.validated) {
            flash("login-error", "You need to verify your email first. Check your email, please.");
            return badRequest(login.render(userForm));
        } else if (user == null) {
            flash("login-error", "Incorrect email or password! Try again.");
            return badRequest(login.render(userForm));
        } else if (user.userAccessLevel == UserAccessLevel.ADMIN) {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
            return ok(adminPanel.render());
        }
        SessionsAndCookies.setUserSessionSata(user);
        SessionsAndCookies.setCookies(user);
        return redirect(routes.Application.index());
    }

    /**
     * Renders user profile for editing.
     *
     * @param email <code>String</code> type value of user email
     * @return
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result editUser(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        return ok(profilePage.render(user));
    }

    /**
     * Logs user out from application, deleted all coockies and session data
     *
     * @return
     */
    public Result logOut() {
        SessionsAndCookies.clearCookies();
        SessionsAndCookies.clearUserSessionData();
        return redirect(routes.Application.index());
    }

    /**
     * Renders hotel list for admin to see
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminHotels() {
        List<Hotel> hotels = Hotel.getAllHotels();
        return ok(adminHotels.render(hotels));
    }

    /**
     * Shows the list of users to admin
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminUsers() {
        List<AppUser> users = AppUser.getAllUsers();
        return ok(adminUsers.render(users));
    }

    /**
     * Shows list of features to admin
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminFeatures() {
        List<Feature> features = Feature.getAllFeatures();
        return ok(adminFeatures.render(features));

    }

    /**
     * Shows list of hotels to hotel manager
     *
     * @return
     */
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result showManagerHotels() {
        List<Hotel> hotels = Hotel.getAllHotels();
        List<AppUser> users = AppUser.getAllUsers();
        return ok(managerHotels.render(hotels, users));
    }

    /**
     * Renders admin panel
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminPanel() {
        return ok(adminPanel.render());
    }

    /**
     * Deletes specific user from database. Method is called by ajax function.
     *
     * @param email <code>String</code> type value of user email
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteUser(String email) {
        if (AppUser.deleteUser(email)) {
            return ok();
        }
        return internalServerError();
    }

    /**
     * Tries to update user profile with new data, and add user personal picture
     *
     * @param email
     * @return
     */
    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result updateUser(String email) {
        Form<AppUser> boundForm = userForm.bindFromRequest();

        AppUser currentUser = AppUser.getUserByEmail(session("email"));

        //getting the values from the fields
        String pass1 = boundForm.field("password").value();
        String pass2 = boundForm.field("passwordretype").value();
        String name = boundForm.field("firstname").value();
        String lastname = boundForm.field("lastname").value();
        String phone = boundForm.field("phoneNumber").value();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("image");
        Image profileImage = null;
        if (filePart != null) {
            File file = filePart.getFile();
            profileImage = Image.create(file, null, currentUser.id, null, null, null);
        }

        Result r = CommonHelperMethods.validateUser(pass1, pass2, name, lastname, phone, email, boundForm);
        if (r != null) {
            return r;
        }

        boolean isUpdated = AppUser.updateUserProfile(currentUser, pass1, name, lastname, phone, profileImage);

        if (isUpdated) {
            flash("success", "Your data was updated");
            return redirect(routes.Users.updateUser(currentUser.email));
        }
        flash("error-search", "Failed to update user profile.");
        return ok(profilePage.render(currentUser));
    }

    /**
     * Renders view for creating hotel by hotel manager
     *
     * @return
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result getSellers() {
        List<AppUser> users = AppUser.getUsersByUserTypeId(5);
        List<Feature> features = Feature.getAllFeatures();
        return ok(createhotel.render(features, users));
    }

    /**
     * Tries to change user role, only admin can change role
     *
     * @param email <code>String</code> type value of user email
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result setRole(String email) {
        Form<AppUser> boundForm = userForm.bindFromRequest();
        String userType = boundForm.field("usertype").value();
        if (AppUser.changeUserRole(email, userType)) {
            flash("info", "Role successfully changed.");
            return redirect(routes.Users.showAdminUsers());
        }
        flash("error-search", "Could not change user role.");
        return redirect(routes.Users.showAdminUsers());
    }

    /**
     * Checks if seller have any approved reservations.
     *
     * @return <code>Integer</code> type value of number of new notification
     * is sent to ajax function. Notification is shown as badge in main view.
     */
    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result reservationApprovedNotification() {
        AppUser temp = SessionsAndCookies.getCurrentUser(ctx());
        Integer notification = Reservation.getNumberOfPayedReservations(temp.id);
        return ok(String.valueOf(notification));
    }

    /**
     * Validates user email after registration.
     *
     * @param token <code>String</code> type value of unique access token
     * @return
     */
    public Result emailValidation(String token) {
        try {
            AppUser user = AppUser.findUserByToken(token);
            if (token == null) {
                return redirect(routes.Application.index());
            }
            if (AppUser.validateUser(user)) {
                flash("registration-msg", "Thank you for joining us. Your email has been verified. Please continue by logging in and enjoy searching some of the best places in the world.");
                return ok(login.render(userForm));
            } else {
                return redirect(routes.Application.index());
            }
        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to validate user registration via email.", e.getMessage());
            return redirect(routes.Application.index());
        }
    }

    /**
     * Changes seller of certain hotel, hotel manager can access this method
     *
     * @param hotelId <code>Integer</code> type value of hotel id
     * @return
     */
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result changeSeller(Integer hotelId) {
        DynamicForm boundForm = Form.form().bindFromRequest();
        String email = boundForm.field("selleremail").value();

        if (Hotel.changeSeller(hotelId, email)) {
            List<Hotel> hotels = Hotel.getAllHotels();
            List<AppUser> users = AppUser.getAllUsers();
            flash("seller-changed", "Seller was successfully updated.");
            return ok(managerHotels.render(hotels, users));
        }
        flash("error-search", "Could not change seller of given hotel.");
        return redirect(routes.Application.index());
    }

    /**
     * Redirects to page for sending request for changing password
     *
     * @return
     */
    public Result askForPasswordChange() {
        return ok(askForPasswordChange.render());
    }


    /**
     * Generates unique token for changin password
     * and sends an email to the user to confirm request
     *
     * @return
     */
    public Result sendChangePasswordRequest() {
        Form<String> form = Form.form(String.class);
        Form<String> boundForm = form.bindFromRequest();
        String email = boundForm.field("email").value();
        if (AppUser.activateForgottenPassword(email)) {
            flash("change-pass-msg", "Link to your personal page for changing password is sent to your email address.");
            return ok(askForPasswordChange.render());
        }
        flash("error", "User with provided email address does not exist.");
        return ok(askForPasswordChange.render());
    }

    /**
     * Checks if user with provided token exists in the database
     * and if it does redirects to form for entering a new password.
     * <p>
     * If it doesn't, returns a bad request warning.
     *
     * @param forgottenPasswordToken
     * @return
     */
    public Result forgotYourPassword(String forgottenPasswordToken) {
        try {
            AppUser user = AppUser.findUserByForgottenPasswordToken(forgottenPasswordToken);
            return ok(forgottenPassword.render(forgottenPasswordToken));
        } catch (Exception e) {
            ErrorLogger.createNewErrorLogger("Failed to find user with provided token for password reset.", e.getMessage());
            return badRequest();
        }
    }

    /**
     * Checks one more time if user with provided token exists in the database,
     * collects data about new password from the form, and saves a new password
     * <p>
     * Clears the forgotten password token field in the database
     *
     * @param forgottenPasswordToken
     * @return
     */
    public Result changePassword(String forgottenPasswordToken) {
        Form<String> form = Form.form(String.class);
        Form<String> boundForm = form.bindFromRequest();

        String pass = boundForm.field("newpassword").value();

        AppUser user = AppUser.findUserByForgottenPasswordToken(forgottenPasswordToken);

        if (user == null) {
            flash("pass-changed-error", "Password hasn't been changed.");
            return redirect(routes.Application.index());
        } else {
            user.updatePassword(user, pass);
            flash("pass-changed-success", "Password has been successfully changed.");
            return redirect(routes.Application.index());
        }
    }

    /**
     * Cancels password change request if user didn't want to change it.
     * Clears the forgotten password token field in the database
     *
     * @return
     */
    public Result cancelPasswordChangeRequest(String forgottenPasswordToken) {
        AppUser user = AppUser.findUserByForgottenPasswordToken(forgottenPasswordToken);

        if (user == null) {
            flash("pass-changed-error", "Your cancellation link is invalid.");
            return redirect(routes.Application.index());
        } else {
            user.clearChangePasswordToken(user);
            flash("pass-changed-success", "Your change password request was successfully cancelled.");
            return redirect(routes.Application.index());
        }
    }

    /**
     * Renders view for seller registration, invitation is set to expired.
     *
     * @param token
     * @return
     */
    public Result registerSeller(String token) {
        Invitation.resetInvitation(token);
        return ok(views.html.user.registerSeller.render(userForm));
    }

    /**
     * Saves new Invitation to database and sends email with registration link only for seller.
     *
     * @return
     */
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result sendInvitation() {
        AppUser user = AppUser.getUserByEmail(session("email"));
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.field("email").value();
        String title = form.field("subject").value();
        String content = form.field("content").value();

        boolean created = Invitation.createNewInvitation(email, title, content, user);

        if (created) {
            flash("info", "Invitation successfully sent.");
            return redirect(routes.Users.seeAllSellers());
        }
        flash("error-search", "Invitation could not be sent.");
        return redirect(routes.Users.seeAllSellers());
    }

    /**
     * Renders all AppUsers that are sellers, orders them by first name.
     *
     * @return
     */
    public Result seeAllSellers() {
        List<AppUser> sellers = AppUser.getAllSellersOrderedByFirstname();
        return ok(views.html.manager.seeAllSellers.render(sellers));
    }


}
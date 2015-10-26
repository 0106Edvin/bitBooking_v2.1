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

    private static List<Hotel> hotels = Hotel.finder.all();
    private static Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);

    private static List<AppUser> users = AppUser.finder.all();
    private static Model.Finder<String, AppUser> userFinder = new Model.Finder<>(AppUser.class);

    private static List<Feature> features = Feature.finder.all();
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);


    /*opening register form */
    public Result registerUser() {
        return ok(register.render(userForm));
    }

    /*insert registered user into the database*/

    public Result saveUser(String userType) {
        Form<AppUser> boundForm = userForm.bindFromRequest();

        //getting the values from the fields
        String pass1 = boundForm.field("password").value();
        String pass2 = boundForm.field("passwordretype").value();
        String name = boundForm.field("firstname").value();
        String lastname = boundForm.field("lastname").value();
        String phone = boundForm.field("phoneNumber").value();


        //validation of the form

        if (!pass1.equals(pass2)) {
            flash("error", "Passwords don't match!");
            return ok(register.render(boundForm));

        } else if (pass1.length() < Constants.MIN_PASSWORD_LENGTH || pass2.length() < Constants.MIN_PASSWORD_LENGTH) {
            flash("error", "Password must be at least 6 characters long!");
            return ok(register.render(boundForm));

        } else if ((!name.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*")) || (!lastname.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*"))) {
            flash("error", "Name and last name must contain letters only!");
            return ok(register.render(boundForm));

        } else if (name.length() < Constants.MIN_NAME_LENGTH || lastname.length() < Constants.MIN_NAME_LENGTH) {
            flash("error", "Name and last name must be at least 2 letters long!");
            return ok(register.render(boundForm));

        } else if (phone.length() > Constants.MAX_PHONE_NUMBER_LENGTH || phone.length() < Constants.MIN_PHONE_NUMBER_LENGTH) {
            flash("error", "Phone number must be at least 9 and can't be more than 15 digits long!");
            return ok(register.render(boundForm));

        } else if (phone.matches("^[a-zA-Z]+$")) {
            flash("error", "Phone number must contain digits only!");
            return ok(register.render(boundForm));

        } else {
            AppUser user;
            try {
                user = boundForm.get();
                user.hashPass();

                user.token = UUID.randomUUID().toString();
                if (Constants.USER_SELLER.equals(userType)) {
                    user.userAccessLevel = UserAccessLevel.SELLER;
                    user.validated = Constants.VALIDATED_USER;
                }
                user.save();

                if (Constants.USER_BUYER.equals(userType)) {
                    // Sending Email To user
                    String host = Play.application().configuration().getString("url") + "validate/" + user.token;
                    MailHelper.send(user.email, host, Constants.REGISTER, null, null, null);

                    flash("registration-msg", "Thank you for joining us. You need to verify your email address. Check your email for verification link.");
                }
                flash("registration-msg", "Thank you for joining us.");
                return ok(login.render(userForm));
            } catch (Exception e) {
                flash("error", "Email already exists in our database, please try again!");
                return ok(register.render(boundForm));
            }
        }
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
        } else {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(Authenticators.isUserLogged.class)
    public Result editUser(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        return ok(profilePage.render(user));
    }

    public Result logOut() {
        SessionsAndCookies.clearCookies();
        SessionsAndCookies.clearUserSessionData();
        return redirect(routes.Application.index());
    }

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminHotels() {
        List<Hotel> hotels = finder.all();
        return ok(adminHotels.render(hotels));
    }

    /*shows the list of users to admin*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminUsers() {
        List<AppUser> users = userFinder.all();
        return ok(adminUsers.render(users));
    }

    /*shows the list of features to admin*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminFeatures() {
        List<Feature> features = featureFinder.all();
        return ok(adminFeatures.render(features));

    }

    /*shows the list of hotels to hotel manager*/
    @Security.Authenticated(Authenticators.HotelManagerFilter.class)
    public Result showManagerHotels() {
        List<Hotel> hotels = finder.all();
        List<AppUser> users = AppUser.finder.all();
        return ok(managerHotels.render(hotels, users));
    }

    /*shows the list of hotels to hotel manager*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminPanel() {
        return ok(adminPanel.render());
    }

    /*This method allows admin to delete user*/
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteUser(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        Ebean.delete(user);
        return redirect(routes.Users.showAdminUsers());
    }

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
        if (filePart != null) {
            File file = filePart.getFile();
            Image profileImage = Image.create(file, null, currentUser.id, null, null, null);
            currentUser.profileImg = profileImage;
        }

        if (!pass1.equals(pass2)) {
            flash("error", "Passwords don't match");
            return ok(profilePage.render(currentUser));

        } else if ((!name.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*")) || (!lastname.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*"))) {
            flash("error", "Name and last name must contain letters only");
            return ok(profilePage.render(currentUser));

        } else if (name.length() < 2 || lastname.length() < 2) {
            flash("error", "Name and last name must be at least 2 letters long");
            return ok(profilePage.render(currentUser));

        } else if (phone.length() > Constants.MAX_PHONE_NUMBER_LENGTH || phone.length() < Constants.MIN_PHONE_NUMBER_LENGTH) {
            flash("error", "Phone number must be at least 9 and can't be more than 15 digits long!");
            return ok(profilePage.render(currentUser));

        } else if (phone.matches("^[a-zA-Z]+$")) {
            flash("error", "Phone number must contain digits only");
            return ok(profilePage.render(currentUser));

        } else {

            try {
                currentUser.firstname = name;
                currentUser.lastname = lastname;
                if (pass1 != null && !pass1.equals("") && pass1.charAt(0) != ' ') {
                    currentUser.password = pass1;
                    currentUser.hashPass();
                }
                currentUser.phoneNumber = phone;

                currentUser.update();

                flash("success", "Your data was updated");
                return redirect(routes.Users.updateUser(currentUser.email));

            } catch (Exception e) {
                flash("error", "You didn't fill the form correctly, please try again\n" + e.getMessage());
                return ok(profilePage.render(currentUser));
            }
        }
    }

    @Security.Authenticated(Authenticators.SellerFilter.class)
    public Result getSellers() {
        List<AppUser> users = AppUser.getUsersByUserTypeId(5);
        List<Feature> features = Feature.finder.all();
        return ok(createhotel.render(features, users));
    }

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result setRole(String email) {
        Form<AppUser> boundForm = userForm.bindFromRequest();

        AppUser user = AppUser.getUserByEmail(email);
        String userType = boundForm.field("usertype").value();

        if (userType.equals("buyer")) {
            user.userAccessLevel = UserAccessLevel.BUYER;

        } else if (userType.equals("seller")) {
            user.userAccessLevel = UserAccessLevel.SELLER;

        } else if (userType.equals("hotelmanager")) {
            user.userAccessLevel = UserAccessLevel.HOTEL_MANAGER;
        }
        user.update();

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

    public Result emailValidation(String token) {
        try {
            AppUser user = AppUser.findUserByToken(token);
            if (token == null) {
                return redirect(routes.Application.index());
            }
            if (AppUser.validateUser(user)) {
                flash("registration-msg", "Thank you for joining us. Your email has been verified. Please continue by logging in and enjoy searching some of the best places in the world.");
                return badRequest(login.render(userForm));
            } else {
                return redirect(routes.Application.index());
            }
        } catch (Exception e) {
            return redirect(routes.Application.index());
        }
    }

    public Result changeSeller(Integer hotelId) {
        Form<AppUser> userForm = Form.form(AppUser.class);
        Form<AppUser> boundForm = userForm.bindFromRequest();

        String email = boundForm.field("selleremail").value();

        AppUser seller = AppUser.getUserByEmail(email);
        Hotel hotel = Hotel.findHotelById(hotelId);

        hotel.sellerId = seller.id;
        hotel.update();

        List<Hotel> hotels = finder.all();
        List<AppUser> users = AppUser.finder.all();

        flash("seller-changed", "Seller was successfully updated.");
        return ok(managerHotels.render(hotels, users));
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
        try {
            Form<String> form = Form.form(String.class);
            Form<String> boundForm = form.bindFromRequest();

            String email = boundForm.field("email").value();

            AppUser user1 = AppUser.getUserByEmail(email);

            user1.forgottenPassToken = UUID.randomUUID().toString();
            user1.save();

            // Sending Email To user
            String host = Play.application().configuration().getString("url") + "user/forgotyourpassword/" + user1.forgottenPassToken;
            String cancelRequest = Play.application().configuration().getString("url") + "user/cancelpasswordchangerequest/" + user1.forgottenPassToken;
            MailHelper.send(user1.email, host, Constants.CHANGE_PASSWORD, cancelRequest, null, null);

            flash("change-pass-msg", "Link to your personal page for changing password is sent to your email address.");
            return badRequest(askForPasswordChange.render());
        } catch (Exception e) {
            flash("error", "User with provided email address does not exist.");
            return ok(askForPasswordChange.render());
        }
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

    public Result registerSeller(String token) {
        Invitation invitation = Invitation.finder.where().eq("token", token).findUnique();
        if (invitation != null) {
            invitation.isActive = Constants.INVITATION_EXPIRED;
            invitation.update();
        }

        return ok(views.html.user.registerSeller.render(userForm));
    }

    /**
     * Saves new Invitation to database and sends email with registration link only for seller.
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

        Logger.debug(email + " " + title + " " + content);
        return ok("bla");
    }

    /**
     * Renders all AppUsers that are sellers, orders them by first name.
     *
     * @return
     */
    public Result seeAllSellers() {
        List<AppUser> sellers = AppUser.finder.where().eq("user_access_level", UserAccessLevel.SELLER).orderBy("firstname asc").findList();
        return ok(views.html.manager.seeAllSellers.render(sellers));
    }


}
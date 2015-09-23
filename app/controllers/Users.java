package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.SessionsAndCookies;
import helpers.UserAccessLevel;
import models.AppUser;
import models.Feature;
import models.Hotel;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.list;
import views.html.user.*;
import views.html.manager.*;
import views.html.admin.*;
import views.html.hotel.*;
import helpers.Authenticators;


import java.util.List;


/**
 * Created by ajla on 9/20/15.
 */
public class Users extends Controller {
    private static final Form<AppUser> userForm = Form.form(AppUser.class);

    private static List<Hotel> hotels = Hotel.finder.all();
    private static Model.Finder<String, Hotel> finder = new Model.Finder<>(Hotel.class);

    private static List<AppUser> users = AppUser.finder.all();
    private static Model.Finder<String, AppUser> userFinder = new Model.Finder<>( AppUser.class);

    private static List<Feature> features = Feature.finder.all();
    private static Model.Finder<String, Feature> featureFinder = new Model.Finder<>(Feature.class);


    /*opening register form */
    public Result registerUser()
    {
        return ok(register.render(userForm));
    }

    /*insert registered user into the database*/

    public Result saveUser() {
        Form<AppUser> boundForm = userForm.bindFromRequest();

        //getting the values from the fields
        String pass1 = boundForm.bindFromRequest().field("password").value();
        String pass2 = boundForm.bindFromRequest().field("passwordretype").value();
        String name = boundForm.bindFromRequest().field("firstname").value();
        String lastname = boundForm.bindFromRequest().field("lastname").value();
        String phone = boundForm.bindFromRequest().field("phoneNumber").value();


        //validation of the form

        if (!pass1.equals(pass2)) {
            flash("error", "Passwords don't match!");
            return ok(register.render(boundForm));

        } else if (pass1.length() < 6 || pass2.length() < 6) {
            flash("error", "Password must be at least 6 characters long!");
            return ok(register.render(boundForm));

        } else if ((!name.matches("^[a-zA-Z\\s]*$")) || (!lastname.matches("^[a-zA-Z\\s]*$"))) {
            flash("error", "Name and last name must contain letters only!");
            return ok(register.render(boundForm));

        } else if (name.length() < 2 || lastname.length() < 2 ) {
            flash("error", "Name and last name must be at least 2 letters long!");
            return ok(register.render(boundForm));

        } else if (phone.length() > 15) {
            flash("error", "Phone number can't be more than 15 digits long!");
            return ok(register.render(boundForm));

        } else if (phone.matches("^[a-zA-Z]+$")) {
            flash("error", "Phone number must contain digits only!");
            return ok(register.render(boundForm));

        } else {
            AppUser user;
            try {
                user = boundForm.get();
                user.hashPass();
                user.save();
                return redirect(routes.Application.index());
            } catch (Exception e) {
                flash("error", "Email allready exists in our database, please try again!");
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

      String email = boundForm.bindFromRequest().field("email").value();
      String password = boundForm.bindFromRequest().field("password").value();

      AppUser user = AppUser.authenticate(email, password);

        if (user == null) {
            flash("error", "Incorrect email or password! Please try again!");
            return badRequest(list.render(hotels));
        } else if (user.userAccessLevel == UserAccessLevel.ADMIN) {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
            return ok(adminPanel.render());
        } else if (user.userAccessLevel == UserAccessLevel.HOTEL_MANAGER) {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
            return ok(managerHotels.render(hotels));
        } else if (user.userAccessLevel == UserAccessLevel.SELLER) {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
            return TODO;
        } else if (user.userAccessLevel == UserAccessLevel.BUYER) {
            SessionsAndCookies.setUserSessionSata(user);
            SessionsAndCookies.setCookies(user);
              return redirect(routes.Users.editUser(user.email));
        } else {
            return redirect(routes.Application.index());
        }
   }


//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result editUser(String email) {
        AppUser user = AppUser.getUserByEmail(email);
        return ok(profilePage.render(user));
    }

    public Result logOut() {
        SessionsAndCookies.clearCookies();
        return redirect(routes.Application.index());
    }

//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminHotels() {
       List<Hotel> hotels = finder.all();
       return ok(adminHotels.render(hotels));

    }

   /*shows the list of users to admin*/
//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminUsers() {
       List<AppUser> users = userFinder.all();
       return ok(adminUsers.render(users));
    }
    /*shows the list of features to admin*/
//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result showAdminFeatures() {
       List<Feature> features = featureFinder.all();
       return ok(adminFeatures.render(features));

    }

   /*shows the list of hotels to hotel manager*/
//   @Security.Authenticated(Authenticators.HotelManagerFilter.class)
   public Result showManagerHotels() {
       List<Hotel> hotels = finder.all();
       return ok(managerHotels.render(hotels));
   }

    /*This method allows admin to delete user*/
//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteUser(String email) {
       AppUser user = AppUser.getUserByEmail(email);
       Ebean.delete(user);
       return redirect(routes.Users.showAdminUsers());
    }


//    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateUser(String email) {
       Form<AppUser> boundForm = userForm.bindFromRequest();
       AppUser user = AppUser.getUserByEmail(email);
       //getting the values from the fields
       String pass1 = boundForm.bindFromRequest().field("password").value();
       String pass2 = boundForm.bindFromRequest().field("passwordretype").value();
       String name = boundForm.bindFromRequest().field("firstname").value();
       String lastname = boundForm.bindFromRequest().field("lastname").value();
       String phone = boundForm.bindFromRequest().field("phoneNumber").value();

       if (!pass1.equals(pass2)) {
           flash("error", "Passwords don't match");
           return ok(profilePage.render(user));

       } else if ((!name.matches("^[a-zA-Z\\s]*$")) || (!lastname.matches("^[a-zA-Z\\s]*$"))) {
           flash("error", "Name and last name must contain letters only");
           return ok(profilePage.render(user));

       } else if (name.length() < 2 || lastname.length() < 2) {
           flash("error", "Name and last name must be at least 2 letters long");
           return ok(profilePage.render(user));

       } else if (phone.length() > 15) {
           flash("error", "Phone number can't be more than 15 digits long");
           return ok(profilePage.render(user));

       } else if (phone.matches("^[a-zA-Z]+$")) {
           flash("error", "Phone number must contain digits only");
           return ok(profilePage.render(user));

       } else {

           try {
               user.firstname = name;
               user.lastname = lastname;
               user.password = pass1;
               user.hashPass();
               user.phoneNumber = phone;


               user.update();

               flash("success", "Your data was updated");
               return redirect(routes.Users.updateUser(user.email));

           }catch(Exception e){
               flash("error", "You didn't fill the form corectly, please try again");
               return ok(profilePage.render(user));
           }
       }
   }
//   @Security.Authenticated(Authenticators.SellerFilter.class)
   public Result getSellers() {
       List<AppUser> users = AppUser.getUsersByUserTypeId(5);
       List<Feature> features = Feature.finder.all();
       return ok(createhotel.render(features, users));
   }

//   @Security.Authenticated(Authenticators.AdminFilter.class)
   public Result setRole(String email) {
     Form<AppUser> boundForm = userForm.bindFromRequest();

     AppUser user = AppUser.getUserByEmail(email);
     String userType = boundForm.bindFromRequest().field("usertype").value();

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

}

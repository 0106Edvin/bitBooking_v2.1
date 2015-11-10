package helpers;

import models.AppUser;
import models.ErrorLogger;
import play.data.Form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import static play.mvc.Controller.flash;
import static play.mvc.Results.ok;

/**
 * This helper class contains common methods.
 * Those methods aren't related to system functionalities,
 * and yet, they can be used in everywhere int the project.
 * Created by ajla.eltabari on 02/10/15.
 */
public class CommonHelperMethods {

    /**
     * Returns provided date as a String value
     * @param date
     * @return
     */
    public static String getDateAsString(Date date){

        if (date == null) {
            return "Unknown date";
        }
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        String myDate = dtf.format(date);
        return myDate;
    }

    /**
     * Formats only current time into <code>String</code>.
     * Example of formatted date 19:27:32 Wed, 14 Oct 2015
     *
     * @return <code>String</code> type value of formatted date
     */
    public static String getCurrentDateFormated() {
        return new SimpleDateFormat("HH:mm:ss EEE, dd MMM yyyy").format(new Date());
    }

    /**
     * Converts inputed string into Date type value, with format day/month/year
     *
     * @param date <code>String</code> type value of date
     * @return <code>Date</code> if string is parsed correctly, <code>null</code> if it not
     */
    public static Date convertStringToDate(String date) {
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dtf.parse(date);
        } catch (ParseException e) {
            ErrorLogger.createNewErrorLogger("Failed to parse inputed date.", e.getMessage());
            return null;
        }
    }

    /**
     * Checks if given email is valid by matching it to regex pattern.
     *
     * @param email <code>String</code> value of email
     * @return <code>boolean</code> type value true if email is correct, false if not
     */
    public static boolean validateEmail(String email) {
        final Pattern pattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");
        if (!pattern.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    /**
     * Simple validation for any sting, can't be empty.
     *
     * @param input <code>String</code> type value
     * @return <code>boolean</code> true if string is not empty or null, false if it is
     */
    public static boolean validateInputString(String input) {
        if (input == null || "".equals(input.trim())) {
            return false;
        }
        return true;
    }


    public static play.mvc.Result validateUser(String pass1, String pass2, String name, String lastname, String phone, String email, Form<AppUser> boundForm) {
        if (!pass1.equals(pass2)) {
            flash("error", "Passwords don't match!");
            return ok(views.html.user.register.render(boundForm));

        } else if (pass1.length() < Constants.MIN_PASSWORD_LENGTH || pass2.length() < Constants.MIN_PASSWORD_LENGTH) {
            flash("error", "Password must be at least 6 characters long!");
            return ok(views.html.user.register.render(boundForm));

        } else if ((!name.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*")) || (!lastname.matches("[a-zA-Z]+(\\s+[a-zA-Z]+)*"))) {
            flash("error", "Name and last name must contain letters only!");
            return ok(views.html.user.register.render(boundForm));

        } else if (name.length() < Constants.MIN_NAME_LENGTH || lastname.length() < Constants.MIN_NAME_LENGTH) {
            flash("error", "Name and last name must be at least 2 letters long!");
            return ok(views.html.user.register.render(boundForm));

        } else if (phone.length() > Constants.MAX_PHONE_NUMBER_LENGTH || phone.length() < Constants.MIN_PHONE_NUMBER_LENGTH) {
            flash("error", "Phone number must be at least 9 and can't be more than 15 digits long!");
            return ok(views.html.user.register.render(boundForm));

        } else if (phone.matches("^[a-zA-Z]+$")) {
            flash("error", "Phone number must contain digits only!");
            return ok(views.html.user.register.render(boundForm));

        } else if (!validateEmail(email)) {
            flash("error", "Email is wrong!");
            return ok(views.html.user.register.render(boundForm));

        }
        return null;
    }


}

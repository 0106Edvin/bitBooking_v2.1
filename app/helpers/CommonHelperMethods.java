package helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * This helper class contains common methods.
 * Those methods aren't related to system functionalities,
 * and yet, they can be used in everywhere int the project.
 * Created by ajla.eltabari on 02/10/15.
 */
public class CommonHelperMethods {

    /**
     * Returnes provided date as a String value
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
}

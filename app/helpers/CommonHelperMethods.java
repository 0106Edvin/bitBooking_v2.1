package helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}

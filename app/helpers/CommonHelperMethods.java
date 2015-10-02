package helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ajla.eltabari on 02/10/15.
 */
public class CommonHelperMethods {

    public static String getDateAsString(Date date){
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        String myDate = dtf.format(date);
        return myDate;
    }
}

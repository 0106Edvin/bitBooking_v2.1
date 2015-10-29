package helpers;

/**
 * Created by ajla.eltabari on 21/09/15.
 */

public class Constants {

    public static final Integer MIN_PASSWORD_LENGTH = 6;
    public static final Integer MIN_NAME_LENGTH = 2;
    public static final Integer MIN_PHONE_NUMBER_LENGTH = 9;
    public static final Integer MAX_PHONE_NUMBER_LENGTH = 15;
    public static final String EMAIL = "bitBooking@gmail.com";
    public static final Double INITIAL_RATING = 0.0;
    public static final Boolean SHOW_HOTEL_ON_HOMEPAGE = false;

    // Values for different email templates
    public static final Integer CHANGE_PASSWORD = 0;
    public static final Integer REGISTER = 1;
    public static final Integer REGISTER_SELLER = 2;
    public static final Integer SUCCESSFUL_RESERVATION = 3;

    // Values for message status
    public static final Boolean MESSAGE_NEW = true;
    public static final Boolean MESSAGE_READ = false;
    public static final Boolean MESSAGE_ACTIVE = true;
    public static final Boolean MESSAGE_DELETED = false;

    public static final Boolean INVITATION_ACTIVE = true;
    public static final Boolean INVITATION_EXPIRED = false;
    public static final String USER_SELLER = "seller";
    public static final String USER_BUYER = "buyer";

    public static final Boolean VALIDATED_USER = true;
    public static final Boolean NOT_VALIDATED_USER = false;

    // Homepage - number of hotels by page
    public static final Integer PAGE_SIZE = 8;

    public static final Boolean FEATURE_FREE = true;
    public static final Boolean FEATURE_NOT_FREE = false;
}

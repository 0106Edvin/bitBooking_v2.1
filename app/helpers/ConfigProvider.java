package helpers;

import play.Play;

/**
 * Helper class that provided additional security by hiding
 * property names from configuration file.
 *
 * Created by ajla on 11/8/15.
 */
public class ConfigProvider {

    // Configurations for email
    public static final String SMTP_HOST = Play.application().configuration().getString("smtp.host");
    public static final String SMTP_PORT = Play.application().configuration().getString("smtp.port");
    public static final String SMTP_PORT1 = Play.application().configuration().getString("smtp.port1");
    public static final String SMTP_SSL = Play.application().configuration().getString("smtp.ssl");
    public static final String SMTP_USER = Play.application().configuration().getString("mail.smtp.user");
    public static final String SMTP_PASS = Play.application().configuration().getString("mail.smtp.pass");
    public static final String MAIL_TLS = Play.application().configuration().getString("mail.tls");
    public static final String MAIL_POP_BEFORE_SMTP = Play.application().configuration().getString("mail.popbeforesmtp");
    public static final String MAIL_FROM = Play.application().configuration().getString("mailFrom");
    public static final String MAIL_FROM_PASS = Play.application().configuration().getString("mailFromPass");

    // Cloudinary configurations
    public static final String CLOUDINARY = Play.application().configuration().getString("cloudinary.string");
    // Link to the cloudinary link for our logo
    public static final String LOGO = Play.application().configuration().getString("logo");

    // Google Recapthca configurations
    public static final String RECAPTCHA_KEY = Play.application().configuration().getString("recaptchaKey");
    public static final String RECAPTCHA_VERIFICATION_URL = Play.application().configuration().getString("recaptchaVerificationUrl");

    // Token for APIs
    public static final String TOKEN = Play.application().configuration().getString("token");

    // Application configurations
    public static final String APPLICATION_HOST = Play.application().configuration().getString("application.host");
    public static final String APPLICATION_URL = Play.application().configuration().getString("url");

    // PayPal configurations
    public static final String CLIENT_ID = Play.application().configuration().getString("clientId");
    public static final String CLIENT_SECRET = Play.application().configuration().getString("clientSecret");

    // Unsubscribe configurations
    public static final String UNSUBSCRIBE = Play.application().configuration().getString("unsubscribe");
    public static final String BIT_CLASSROOM_KEY = Play.application().configuration().getString("classroomKey");
    public static final String BIT_CLASSROOM_URL = Play.application().configuration().getString("classroomUrl");
}

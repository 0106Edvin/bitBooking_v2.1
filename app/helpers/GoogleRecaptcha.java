package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Play;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.ws.*;
import helpers.Constants;
import play.libs.F.Function;
import play.libs.F.Promise;
import views.html.*;
import javax.inject.Inject;

/**
 * This helper class contains methods related to Google recaptcha functionality.
 *
 * Created by ajla.eltabari on 01/10/15.
 */

public class GoogleRecaptcha {

    /**
     * Verifies entered string for Google recaptcha.
     * Returns true if verified, and false if not.
     *
     * @param ws
     * @param recaptcha
     * @return
     */
    public static Boolean verifyGoogleRecaptcha(WSClient ws, String recaptcha) {

        WSRequest request = ws.url(ConfigProvider.RECAPTCHA_VERIFICATION_URL);
        request.setQueryParameter("secret", ConfigProvider.RECAPTCHA_KEY);
        request.setQueryParameter("response", recaptcha);


        F.Promise<JsonNode> responsePromise = request.get().map(response -> {
            return response.asJson();
        });

        JsonNode node = responsePromise.get(3000);
        String verified = node.findPath("success").asText();

        return verified.equals("true") ? true : false;
    }
}

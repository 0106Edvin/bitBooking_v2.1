package helpers;

import com.squareup.okhttp.*;
import play.Logger;

import java.io.IOException;

/**
 * Created by ajla on 11/13/15.
 */
public class ServiceRequest {
    /**
     * Sends request on inputed URL in the form of JSON object and expects response in the form of JSON object.
     *
     * @param url - The URL on which is request needs to be sent.
     * @param callback - The response that is received.
     */
    public static void get(String url, Callback callback){
        request(url, null, callback, false);
    }

    /**
     * Sends request on inputed URL in the form of JSON object and expects response in the form of JSON object.
     *
     * @param url - The URL on which is request needs to be sent.
     * @param json - The JSON object that contain data.
     * @param callback - The response that is received.
     */
    public static void post(String url, String json, Callback callback){
        request(url, json, callback, true);
    }

    private static void request(String url, String json, Callback callback, boolean isPost){

        MediaType JSON = MediaType.parse("application/json");

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder();

        if(isPost == true) {
            RequestBody requestBody = RequestBody.create(JSON,json);
            requestBuilder.post(requestBody);
        } else {
            requestBuilder.get();
        }

        Request request = requestBuilder
                .url(url).addHeader("Accept", "application/json")
                .addHeader("secret_key", ConfigProvider.BIT_CLASSROOM_KEY).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * Checks if the received response from bitClassroom or bitTracking web application is correct or incorrect.
     *
     * @return New response that that can be successful or unsuccessful.
     */
    public static Callback checkRequest(){
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Logger.error("Error while sending request to bitClassroom.");
            }
            @Override
            public void onResponse(Response response) throws IOException {
                String responseJSON = response.body().string();
                Logger.info("-------" + responseJSON + "------");
            }
        };
    }
}
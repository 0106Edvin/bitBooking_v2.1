package controllers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.AppUser;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import javax.inject.Inject;
import views.html.weather;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ajla.eltabari on 29/09/15.
 */
public class ApiCommunicatior extends Controller {

//    @Inject WSClient ws;
//    public F.Promise<Result> test() {
//        WSRequest request = ws.url("https://community-open-weather-map.p.mashape.com/find");
//        request.setHeader("X-Mashape-Key", "IR6xgWjD53mshTavK1B0iVLFbpAjp1QIN3pjsne1YfhEs3wLFc");
//        request.setHeader("Accept", "text/plain").setMethod("GET");
//        F.Promise<Result> responsePromise = request.get().map(response -> {
//            return ok(response.getBody());
//        });
//
//        return responsePromise;
//    }

    public Result test() {
        return ok(weather.render());
    }


    private static final Form<String> form = Form.form(String.class);

//    @Inject WSClient ws;
//    public F.Promise<Result> getWeather() {
//        Form<String> boundForm = form.bindFromRequest();
//        String city = boundForm.bindFromRequest().field("city").value();
//        String location = "http://api.openweathermap.org/data/2.5/find?q=" + city + "&units=metric";
//        WSRequest rq = WS.url(location);
//        F.Promise<Result> responsePromise = rq.get().map(wsResponse -> {
//            return ok(wsResponse.getBody());
//        });
//        return responsePromise;
//    }

//    @Inject WSClient ws;
//    public Result getWeather() {
//        Form<String> boundForm = form.bindFromRequest();
//        String[] cityName = boundForm.bindFromRequest().field("city").value().split(" ");
//        String city = "";
//
//        for (String s : cityName) {
//            city += s;
//        }
//
//        String location = "http://api.openweathermap.org/data/2.5/find?q=" + city + "&units=metric";
//        WSRequest rq = WS.url(location);
//        F.Promise<JsonNode> responsePromise = rq.get().map(wsResponse -> {
//            return wsResponse.asJson();
//        });
//
//        JsonNode node = responsePromise.get(3000);
//        String definition =  node.findPath("temp").asText();
//
//
//
//        return ok(definition);
//
//
//    }

    @Inject WSClient ws;
    public F.Promise<Result> getWeather() {
        WSRequest request = ws.url("https://community-open-weather-map.p.mashape.com/find");
        request.setHeader("X-Mashape-Key", "IR6xgWjD53mshTavK1B0iVLFbpAjp1QIN3pjsne1YfhEs3wLFc");
        request.setHeader("Accept", "text/plain").setMethod("GET");
        F.Promise<Result> responsePromise = request.get().map(response -> {
           // parse(response.asJson().);
            return ok(response.getBody());
        });

        return responsePromise;
    }

    public void parse(String json)  {
        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            System.out.println("Key: " + field.getKey() + "\tValue:" + field.getValue());
        }
    }
 }

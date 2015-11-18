package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.ConfigProvider;
import models.Course;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by ajla on 11/12/15.
 */
public class ConnectionToClassroom extends Controller {
    /**
     * Saves the course received from bitClassroom web application. The information about the course are received
     * as JSON. After the JSON object is received, it creates and saves new course into the database.
     *
     * After the new course is saved into database it returns to the bitClassroom web application the JSON object that
     * contains the token.
     *
     * @return JSON object that contains the new saved course token.
     */
    public Result saveCourse() {
        // Checking if the request has security key.
        if (request().getHeader("secret_key").equals(ConfigProvider.BIT_CLASSROOM_KEY)) {
            // Declaring JSON object that will contain JSON object from request.
            JsonNode json = request().body().asJson();
            // Declaring string variables that represent course attributes.
            String name = json.findPath("course_name").textValue();
            String description = json.findPath("course_description").textValue();
            String price = json.findPath("course_price").textValue();
            // Creating new course and saving it into database.

            Logger.debug(json.toString());

            Course course = new Course(name, description,  Double.parseDouble(price), 10);
            course.save();

            // Declaring JSON object that contain premiumId.
            JsonNode object = Json.toJson(course.id + "bitbooking");
            // Returning created JSON object.
            return ok(object);
        }
        return badRequest(views.html.notFound.render());
    }
}

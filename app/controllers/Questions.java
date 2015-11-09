package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
import helpers.CommonHelperMethods;
import models.Feature;
import models.Question;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Result;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Security;
import views.html.admin.createFAQ;
import views.html.admin.editFAQ;
import views.html.admin.showFAQ;

import java.util.List;


/**
 * Created by Alen Bumbulovic on 10/19/2015.
 */
public class Questions extends Controller {

    /**
     * Renders view for admin so he/she can create new FAQ
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result createFAQ() {
        return ok(createFAQ.render());
    }

    /**
     * Renders a view for admin so he/she can edit specific FAQ
     *
     * @param Id <code>Integer</code> type value of question id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result editFAQ(Integer Id) {
        Question questions = Question.findQuestionById(Id);

        return ok(editFAQ.render(questions));
    }

    /**
     * Saves new FAW to database, checks if input fields are valid, then tries to save to database.
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result saveFAQ() {
        DynamicForm form = Form.form().bindFromRequest();
        String question = form.field("question").value();
        String answer = form.field("answer").value();
        if (!CommonHelperMethods.validateInputString(question) || !CommonHelperMethods.validateInputString(answer)) {
            flash("error-search", "Seems like you didn't write anything in input fields.");
            return redirect(routes.Questions.seeAll());
        }
        if (Question.createNewFAQ(question, answer)) {
            flash("save", "A new FAQ was created!");
            return redirect(routes.Questions.seeAll());
        }
        flash("error-search", "Failed to save FAQ.");
        return redirect(routes.Questions.seeAll());
    }


    /**
     * Finds specific FAQ and tries to update it with new values.
     *
     * @param Id <code>Integer</code> type value of FAQ id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateFAQ(Integer Id) {
        DynamicForm form = Form.form().bindFromRequest();
        String question = form.field("question").value();
        String answer = form.field("answer").value();

        if (!CommonHelperMethods.validateInputString(question) || !CommonHelperMethods.validateInputString(answer)) {
            flash("error-search", "Seems like you didn't write anything in input fields.");
            return redirect(routes.Questions.seeAll());
        }

        if (Question.updateFAQ(Id, question, answer)) {
            flash("update", "The FAQ was updated!");
            return redirect(routes.Questions.seeAll());
        }
        flash("error-search", "The FAQ could not be updated!");
        return redirect(routes.Questions.seeAll());
    }

    /**
     * Tries to delete specific FAQ from database.
     *
     * @param Id <code>Integer</code> type value of FAQ id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteFAQ(Integer Id) {
        if (Question.deleteFAQ(Id)) {
            flash("delete", "The FAQ was deleted!");
            return redirect(routes.Questions.seeAll());
        }
        flash("error-search", "The FAQ could not be deleted!");
        return redirect(routes.Questions.seeAll());
    }

    /**
     * Renders all FAQ for public users to see
     *
     * @return
     */
    public Result seeAll() {
        List<Question> questions = Question.getAllQuestions();
        return ok(showFAQ.render(questions));
    }


}
package controllers;

import com.avaje.ebean.Model;
import helpers.Authenticators;
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

    private Form<Question> questionForm = Form.form(Question.class);
    List<Question> questions = Question.finder.all();



    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result createFAQ(){
        return ok(createFAQ.render());
    }

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result editFAQ(Integer Id){
        Question questions = Question.findQuestionById(Id);

        return ok(editFAQ.render(questions));
    }

    //This method saves the FAQ
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result saveFAQ(){
        Logger.info("evoo");
        DynamicForm form = Form.form().bindFromRequest();
        Question q = new Question();
        q.question = form.field("question").value();
        q.answer = form.field("answer").value();
        q.save();

        return redirect(routes.Questions.seeAll());
    }


    //This method updates the FAQ
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateFAQ(Integer Id){
        Question q = Question.findQuestionById(Id);
        DynamicForm form = Form.form().bindFromRequest();
        q.question = form.field("question").value();
        q.answer = form.field("answer").value();

        q.update();

        return redirect(routes.Questions.seeAll());
    }


    //This method deletes the FAQ
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteFAQ(Integer Id){
        Question q = Question.findQuestionById(Id);

        q.delete();

        return redirect(routes.Questions.seeAll());
    }


    //shows all the FAQ's
    public Result seeAll(){
        List<Question> questions = Question.finder.all();

        return ok(showFAQ.render(questions));
    }



}
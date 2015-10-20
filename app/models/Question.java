package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by Alen Bumbulovic on 10/19/2015.
 */
public class Question extends Model {

    public static Finder<Integer, Question> finder = new Finder<>(Question.class);


    @Id
    public Integer id;

    @Column(name = "question", columnDefinition = "TEXT")
    public String question;

    @Column(name = "answer", columnDefinition = "TEXT")
    public String answer;

    @Column(name = "updated_by", length = 50)
    public String updatedBy;
    @Column(name = "update_date", columnDefinition = "datetime")
    public Date updateDate;
    @Column(name = "created_by", length = 50, updatable = false)
    public String createdBy;
    @Column(name = "create_date", updatable = false, columnDefinition = "datetime")
    public Date createDate = new Date();


    public Question(Integer id, String question, String answer){
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Question(){

    }


    public static Question findQuestionById(Integer id){
        Question questions = finder.where().eq("id", id).findUnique();

        return questions;
    }


    public String toString() {
        return question + " " + answer;
    }
}

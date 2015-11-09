package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

/**
 * Created by Alen Bumbulovic on 10/19/2015.
 */
@Entity
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


    public Question(Integer id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Question() {

    }

    /**
     * Saves new FAQ to database with provided question and answer.
     *
     * @param question <code>String</code> type value of FAQ question
     * @param answer   <code>String</code> type value of FAQ answer
     * @return <code>boolean</code> type value true if FAQ is saved, false if not
     */
    public static boolean createNewFAQ(String question, String answer) {
        try {
            Question q = new Question();
            q.question = question;
            q.answer = answer;
            q.save();
            return true;
        } catch (PersistenceException e) {
            ErrorLogger.createNewErrorLogger("Failed to save FAQ.", e.getMessage());
            return false;
        }
    }

    /**
     * Updates specific FAQ and saves new value in database.
     *
     * @param faqId    <code>Integer</code> type value of FAQ id
     * @param question <code>String</code> type value of FAQ question
     * @param answer   <code>String</code> type value of FAQ answer
     * @return <code>boolean</code> type value true if FAQ was updated, false if not
     */
    public static boolean updateFAQ(Integer faqId, String question, String answer) {
        Question q = Question.findQuestionById(faqId);
        if (q != null) {
            try {
                q.question = question;
                q.answer = answer;
                q.update();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to update FAQ.", e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Tries to delete specific FAQ from database.
     *
     * @param faqId <code>Integer</code> type value of FAQ id
     * @return <code>boolean</code> type value true if FAQ is deleted successfully, false if not
     */
    public static boolean deleteFAQ(Integer faqId) {
        Question q = Question.findQuestionById(faqId);
        if (q != null) {
            try {
                q.delete();
                return true;
            } catch (PersistenceException e) {
                ErrorLogger.createNewErrorLogger("Failed to delete FAQ.", e.getMessage());
                return false;
            }
        }
        return false;
    }


    /**
     * Finds unique question with provided unique id
     *
     * @param id
     * @return
     */
    public static Question findQuestionById(Integer id) {
        Question questions = finder.where().eq("id", id).findUnique();

        return questions;
    }

    /**
     * Finds all questions from database
     *
     * @return
     */
    public static List<Question> getAllQuestions() {
        return finder.all();
    }


    public String toString() {
        return question + " " + answer;
    }
}

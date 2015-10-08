package models;

import com.avaje.ebean.Model;
import junit.framework.TestCase;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by User on 10/8/2015.
 */
public class AppUserTest extends TestCase {
    public static Model.Finder<String, AppUser> finder = new Model.Finder<>(AppUser.class);
    public void setUpApplication() throws Exception {
        fakeApplication(inMemoryDatabase());

    }

    public void testExistsInDB(String email) throws Exception {
        AppUser user = finder.where().eq("email", email.toString()).findUnique();
        assertNotNull(user);
    }

    public void testGetUserByEmail() throws Exception {

    }

    public void testFindUserById() throws Exception {

    }
}
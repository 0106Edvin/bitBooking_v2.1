package controllers;

import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import play.test.WithApplication;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

/**
 * Created by User on 10/9/2015.
 */
public class UsersTest extends WithApplication {

    /**
     * Testing database
     */
    @Before
    public void configureDatabase() {
        fakeApplication(inMemoryDatabase());
    }


    @Test
    public void testRegisterUser() throws Exception {
        Result result = route(controllers.routes.Users.registerUser());
        assertEquals(OK, result.status());

    }

    @Test
    public void testSaveUser() throws Exception {
        running(fakeApplication(), () -> {
            Result result = route(controllers.routes.Users.saveUser());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/user/register/save");
        });


    }

    @Test
    public void testLogin() throws Exception {

        running(fakeApplication(), () -> {
            Result result = route(controllers.routes.Users.login());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/user/login ");
        });

    }

    @Test
    public void testEditUser() throws Exception {
        Result result = route(controllers.routes.Users.editUser("email"));
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void testLogOut() throws Exception {
        running(fakeApplication(), () -> {
            Result result = route(controllers.routes.Users.logOut());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/user/logout ");
        });

    }

    @Test
    public void testShowAdminHotels() throws Exception {
        Result result = route(controllers.routes.Users.showAdminHotels());
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void testShowAdminUsers() throws Exception {
        Result result = route(controllers.routes.Users.showAdminUsers());
        assertEquals(SEE_OTHER, result.status());
    }
    @Test
    public void testShowAdminFeatures() throws Exception {
        Result result = route(controllers.routes.Users.showAdminFeatures());
        assertEquals(SEE_OTHER, result.status());
    }
    @Test
    public void testShowManagerHotels() throws Exception {
        Result result = route(controllers.routes.Users.showManagerHotels());
        assertEquals(SEE_OTHER, result.status());
    }
    @Test
    public void testShowAdminPanel() throws Exception {
        Result result = route(controllers.routes.Users.showAdminPanel());
        assertEquals(OK, result.status());
    }
    @Test
    public void testDeleteUser() throws Exception {
        running(fakeApplication(), () -> {
            Result result = route(controllers.routes.Users.deleteUser("edvin.mulabdic@bitcamp.ba"));
            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.redirectLocation()).isEqualTo("/admin/adminusers/:email");
        });
    }
    @Test
    public void testUpdateUser() throws Exception {
        running(fakeApplication(), () -> {

            Result result = route(controllers.routes.Users.updateUser("edvin.mulabdic@bitcamp.ba"));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/user/update/:email");
        });
    }

    @Test
    public void testSetRole() throws Exception {
        Result result = route(controllers.routes.Users.setRole("edvin.mulabdic@bitcamp.ba"));
        assertEquals(SEE_OTHER, result.status());
    }
    @Test
    public void testReservationApprovedNotification() throws Exception {
        Result result = route(controllers.routes.Users.reservationApprovedNotification());
        assertEquals(SEE_OTHER, result.status());
    }
}
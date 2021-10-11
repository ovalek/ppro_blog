import models.*;
import org.junit.*;
import static org.junit.Assert.*;

import org.mindrot.jbcrypt.BCrypt;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    /**
     * Test for adding user
     */
    @Test
    public void createAndRetrieveUser() {
        User testUser = new User();
        testUser.email = "bob@gmail.com";
        testUser.name = "Bob";
        testUser.password = BCrypt.hashpw("heslo", BCrypt.gensalt(12));
        testUser.save();
        User bob = User.find.query().where().eq("email", "bob@gmail.com").findOne();
        assertNotNull(bob);
        assertEquals("Bob", bob.name);
    }

    /**
     * Test for user authentication
     */
    @Test
    public void tryAuthenticateUser() {
        User testUser = new User();
        testUser.email = "bob@gmail.com";
        testUser.name = "Bob";
        testUser.password = BCrypt.hashpw("secret", BCrypt.gensalt(12));
        testUser.save();

        assertNotNull(User.authenticate("bob@gmail.com", "secret"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "secret"));
    }
}

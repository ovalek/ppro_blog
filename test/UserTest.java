import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveUser() {
        // new User("bob@gmail.com", "Bob", "secret").save();
        User testUser = new User();
        testUser.email = "bob@gmail.com";
        testUser.name = "Bob";
        testUser.password = "heslo";
        testUser.save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.name);
    }

    @Test
    public void tryAuthenticateUser() {
        User testUser = new User();
        testUser.email = "bob@gmail.com";
        testUser.name = "Bob";
        testUser.password = "secret";
        testUser.save();

        assertNotNull(User.authenticate("bob@gmail.com", "secret"));
        assertNull(User.authenticate("bob@gmail.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "secret"));
    }
}

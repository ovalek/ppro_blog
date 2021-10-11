package models;

import io.ebean.*;
import io.ebean.annotation.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class User extends Model {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int id;

    @Constraints.Required
    @Column(unique = true)
    public String email;

    public String password;

    @Constraints.Required
    public String name;

    public static final Finder<Integer, User> find = new Finder<>(User.class);

    public static User authenticate(String email, String password) {
        if (email == null && password == null) return null;

        User user = User.find.query().where().eq("email", email).findOne();
        if (Objects.nonNull(user) && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    @Transactional
    public ValidationError saveWithValidation(Integer userID) {
        // check email
        if (User.find.query().where().eq("email", email).ne("id", userID).findCount() != 0) {
            return new ValidationError("email", "Email must be unique.");
        }

        // force password
        if (password.isEmpty() && userID == 0) {
            return new ValidationError("password", "Password must be specified.");
        } else if (password.isEmpty() && !User.find.byId(userID).email.equals(email)) {
            return new ValidationError("email", "When you change the email, you must also fill the new password.");
        } else if (password.isEmpty()) {
            password = null;
        }

        // hash password
        if (password != null) {
            System.out.println(password);
            password = BCrypt.hashpw(password, BCrypt.gensalt(12));
            System.out.println(password);
        }

        if (userID != 0) {
            id = userID;
            update();
        } else {
            save();
        }

        return null;
    }
}

package models;

//import com.avaje.ebean.Model;
import io.ebean.*;
import io.ebean.annotation.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
//import com.github.t3hnar.bcrypt;
//import t3hnar

import javax.persistence.*;

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

//    public static Model.Finder<Integer, User> find = new Finder<>(User.class);

    public static User authenticate(String email, String password) {
        if (email == null && password == null) return null;

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));

        return User.find.query().where().eq("email", email).eq("password", hash).findOne();
    }

    @Transactional
    public boolean saveWithValidation(Integer userID, Form<User> form) {
        // check email
        if (User.find.query().where().eq("email", email).ne("id", userID).findCount() != 0) {
            form.errors().add(new ValidationError("email", "Email must be unique."));
            return false;
        }

        // force password
        if (password.isEmpty() && userID == 0) {
            form.errors().add(new ValidationError("password", "Password must be specified."));
            return false;
        } else if (password.isEmpty() && !User.find.byId(userID).email.equals(email)) {
            form.errors().add(new ValidationError("email", "When you change the email, you must also fill the new password."));
            return false;
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

        return true;
    }
}

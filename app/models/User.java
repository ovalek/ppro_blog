package models;

//import com.avaje.ebean.Model;
import io.ebean.*;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.ebean.Transactional;

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

        password = email + password;
        String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);

        return User.find.where().eq("email", email).eq("password", sha256hex).findUnique();
    }

    @Transactional
    public boolean saveWithValidation(Integer userID, Form<User> form) {
        // check email
        if (User.find.where().eq("email", email).ne("id", userID).findRowCount() != 0) {
            form.reject("email", "Email must be unique.");
            return false;
        }

        // force password
        if (password.isEmpty() && userID == 0) {
            form.reject("password", "Password must be specified.");
            return false;
        } else if (password.isEmpty() && !User.find.byId(userID).email.equals(email)) {
            form.reject("email", "When you change the email, you must also fill the new password.");
            return false;
        } else if (password.isEmpty()) {
            password = null;
        }

        Logger.debug("Ale už");
        // hash password
        if (password != null) {
            Logger.debug(org.apache.commons.codec.digest.DigestUtils.sha256Hex(email + password));
            password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(email + password);
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

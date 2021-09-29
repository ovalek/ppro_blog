package models;

//import com.avaje.ebean.Model;
import io.ebean.*;
import io.ebean.annotation.Transactional;
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

        password = email + password;
//        bcrypt
//        String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
//        bcrypt
//        String sha256hex = HashHelper.createPassword(password);

        // TODO: fixme add hash (https://index.scala-lang.org/t3hnar/scala-bcrypt/scala-bcrypt/4.3.0?target=_2.13)
        String sha256hex = password;

        return User.find.query().where().eq("email", email).eq("password", sha256hex).findOne();
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

        Logger.debug("Ale už");
        // hash password
        if (password != null) {
//            Logger.debug(org.apache.commons.codec.digest.DigestUtils.sha256Hex(email + password));
//            password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(email + password);
            //TODO: hash
            Logger.debug(email + password);
            password = email + password;
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

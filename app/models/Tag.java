package models;

import io.ebean.*;
import io.ebean.annotation.Transactional;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import javax.persistence.OrderBy;
import java.util.*;

@Entity
public class Tag extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @Constraints.Required
    public String description;

    @Constraints.Required
    @Constraints.Pattern("#(?:[0-9a-fA-F]{3}){1,2}")
    public String color;

    @Column(unique = true)
    public String name;

    @ManyToMany
    @OrderBy("posted DESC")
    public List<Post> posts = new ArrayList<>();

    public static Finder<Integer, Tag> find = new Finder<>(Tag.class);

    @Transactional
    public ValidationError saveWithValidation(Integer tagID) {
        // check name
        if (Tag.find.query().where().eq("name", name).ne("id", tagID).findCount() != 0){
            return new ValidationError("name", "Name must be unique.");
        }
        if (tagID != 0) {
            id = tagID;
            update();
        } else {
            save();
        }
        return null;
    }

    public static List<Tag> getAllOrdered() {
        return Tag.find.query().where().orderBy("name ASC").findList();
    }
}

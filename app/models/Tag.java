package models;

import io.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.annotation.Transactional;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import javax.persistence.OrderBy;
import java.sql.SQLOutput;
import java.util.*;

@Entity
public class Tag extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @Constraints.Required
    @Column(unique = true)
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
    public boolean saveWithValidation(Integer tagID, Form<Tag> form) {
        // check name
        if (Tag.find.query().where().eq("name", name).ne("id", tagID).findCount() != 0){
            form.errors().add(new ValidationError("name", "Name must be unique."));
            return false;
        }
        if (tagID != 0) {
            id = tagID;
            update();
        } else {
            save();
        }
        return true;
    }

    public static List<Tag> getAllOrdered() {
        return Tag.find.query().where().orderBy("name ASC").findList();
    }
}

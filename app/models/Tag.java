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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Entity
public class Tag extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @Constraints.Required
    @Column(unique = true)
    public String name;

    @Constraints.Required
    @Constraints.Pattern("#(?:[0-9a-fA-F]{3}){1,2}")
    @Column(unique = true)
    public String color;

    @Column(unique = true)
    @Constraints.Pattern("([a-z0-9_]+(([\\-\\/])([a-z0-9_]+))*)?")
    public String alias;

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

        return true;
    }
}

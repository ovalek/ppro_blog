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
public class Section extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @Constraints.Required
    @Column(unique = true)
    public String name;

    @Column(unique = true)
    @Constraints.Pattern("([a-z0-9_]+(([\\-\\/])([a-z0-9_]+))*)?")
    public String alias;

    @OneToMany(mappedBy = "section", cascade = CascadeType.REMOVE)
    @OrderBy("posted DESC")
    public List<Post> posts = new ArrayList<>();

    public int menu_order;

    public static Finder<Integer, Section> find = new Finder<>(Section.class);

    @Transactional
    public boolean saveWithValidation(Integer sectionID, Form<Section> form) {
        // check name
        if (Section.find.query().where().eq("name", name).ne("id", sectionID).findCount() != 0){
            form.errors().add(new ValidationError("name", "Name must be unique."));
            return false;
        }

        // check alias
        if (Section.find.query().where().eq("alias", alias).ne("id", sectionID).findCount() != 0){
            form.errors().add(new ValidationError("alias", "Alias must be unique."));
            return false;
        }

        if (sectionID != 0) {
            id = sectionID;
            update();
        } else {
            int maxOrder = find.query().setMaxRows(1).select("MAX(menu_order)").findOne().menu_order;
            menu_order = ++maxOrder;
            save();
        }

        return true;
    }

    @Transactional
    public static boolean saveOrder(String json) {
        JsonNode data = Json.parse(json);

        Iterator<Map.Entry<String, JsonNode>> nodeIterator = data.fields();

        Section s;
        while (nodeIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();

            s = Section.find.byId(Integer.valueOf(entry.getKey()));
            s.menu_order = entry.getValue().asInt();
            s.update();
        }

        return true;
    }

}

package models;

import io.ebean.*;
import io.ebean.annotation.CreatedTimestamp;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Post extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    public Section section;

    @Constraints.Required
    public String title;

    @Constraints.Required
    @Column(columnDefinition = "TEXT")
    public String content;

    public Boolean published = true;

    @CreatedTimestamp
    public Date posted;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("posted DESC")
    public List<Comment> comments = new ArrayList<>();

    public static Finder<Integer, Post> find = new Finder<>(Post.class);
}

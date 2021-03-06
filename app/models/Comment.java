package models;

import io.ebean.*;
import io.ebean.annotation.CreatedTimestamp;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Comment extends Model {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    public Post post;

    @Constraints.Required
    public String name;

    @Constraints.Required
    @Column(columnDefinition = "TEXT")
    public String content;

    @CreatedTimestamp
    public Date posted;

    public static Finder<Integer, Comment> find = new Finder<>(Comment.class);
}

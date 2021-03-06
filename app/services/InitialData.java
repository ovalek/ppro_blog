package services;

import io.ebean.*;
import models.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Singleton;
import java.text.SimpleDateFormat;

@Singleton
public class InitialData {

    public InitialData() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (Ebean.find(User.class).findCount() == 0) {
            User u1 = new User();
            u1.id = 1;
            u1.email = "admin@nekde.cz";
            u1.name = "Admin";
            u1.password =  BCrypt.hashpw("password", BCrypt.gensalt(12));
            u1.save();
        }

        if (Ebean.find(Section.class).findCount() == 0) {
            Section s1 = new Section();
            s1.id = 1;
            s1.name = "Homepage";
            s1.alias = "";
            s1.menu_order = 0;
            s1.save();
            Section s2 = new Section();
            s2.id = 2;
            s2.name = "Section A";
            s2.alias = "section-a";
            s2.menu_order = 1;
            s2.save();
            Section s3 = new Section();
            s3.id = 3;
            s3.name = "Section B";
            s3.alias = "section-b";
            s3.menu_order = 2;
            s3.save();
            Section s4 = new Section();
            s4.id = 4;
            s4.name = "Section C";
            s4.alias = "section-c";
            s4.menu_order = 3;
            s4.save();
            Section s5 = new Section();
            s5.id = 5;
            s5.name = "Section D";
            s5.alias = "section-d";
            s5.menu_order = 4;
            s5.save();
        }

        if (Ebean.find(Tag.class).findCount() == 0) {
            Tag t1 = new Tag();
            t1.id = 1;
            t1.description = "Tohle je super článek";
            t1.color = "#0000FF";
            t1.name = "Super";
            t1.save();
            Tag t2 = new Tag();
            t2.id = 2;
            t2.description = "Tohle je špatný článek";
            t2.color = "#ff0000";
            t2.name = "Špatný";
            t2.save();
            Tag t3 = new Tag();
            t3.id = 3;
            t3.description = "Hustodémonskykrutopřísný";
            t3.color = "#00ff00";
            t3.name = "HDKP";
            t3.save();
        }

        if (Ebean.find(Post.class).findCount() == 0) {
            Post p1 = new Post();
            p1.id = 1;
            p1.section = Section.find.byId(1);
            p1.title = "Homepage post #1";
            p1.content = "Příliš <strong>žluťoučký kůň</strong> <em>úpěl</em> <s>ďábelské</s> ódy.";
            p1.tags = Tag.find.query().where().eq("id", 2).findList();
            p1.save();
            Post p2 = new Post();
            p2.id = 2;
            p2.section = Section.find.byId(1);
            p2.title = "Homepage post #2";
            p2.content = "Lorem <strong>ipsum</strong> <em>dolor</em> <s>sit</s> amet.";
            p2.save();
            Post p3 = new Post();
            p3.id = 3;
            p3.section = Section.find.byId(1);
            p3.title = "Homepage post #3";
            p3.content = "Příliš <strong>žluťoučký kůň</strong> <em>úpěl</em> <s>ďábelské</s> ódy.";
            p3.tags = Tag.find.query().where().or().eq("id", 1).eq("id", 3).endOr().findList();
            p3.save();
            Post p4 = new Post();
            p4.id = 4;
            p4.section = Section.find.byId(2);
            p4.title = "Section A post #1";
            p4.content = "Lorem <strong>ipsum</strong> <em>dolor</em> <s>sit</s> amet.";
            p4.save();
            Post p5 = new Post();
            p5.id = 5;
            p5.section = Section.find.byId(2);
            p5.title = "Section A post #2";
            p5.content = "Příliš <strong>žluťoučký kůň</strong> <em>úpěl</em> <s>ďábelské</s> ódy.";
            p5.save();
        }

        if (Ebean.find(Comment.class).findCount() == 0) {
            Comment c1 = new Comment();
            c1.name = "Lisa Simpson";
            c1.post = Post.find.byId(1);
            c1.content = "Plain text comment: <strong>žluťoučký kůň</strong> <em>úpěl</em>.";
            c1.save();
            Comment c2 = new Comment();
            c2.name = "Homer Simpson";
            c2.post = Post.find.byId(1);
            c2.content = "Plain text comment: <strong>ipsum</strong> <em>dolor</em>.";
            c2.save();
            Comment c3 = new Comment();
            c3.name = "Bart Simpson";
            c3.post = Post.find.byId(2);
            c3.content = "Plain text comment: <strong>žluťoučký kůň</strong> <em>úpěl</em>.";
            c3.save();
            Comment c4 = new Comment();
            c4.name = "Marge Simpson";
            c4.post = Post.find.byId(2);
            c4.content = "Plain text comment: <strong>ipsum</strong> <em>dolor</em>.";
            c4.save();
            Comment c5 = new Comment();
            c5.name = "Maggie Simpson";
            c5.post = Post.find.byId(3);
            c5.content = "Plain text comment: <strong>žluťoučký kůň</strong> <em>úpěl</em>.";
            c5.save();
        }
    }
}
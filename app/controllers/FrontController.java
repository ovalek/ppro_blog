package controllers;

import models.Comment;
import models.Post;
import models.Section;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class FrontController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result section(String sectionAlias) {
        Section section = Section.find.where().eq("alias", sectionAlias).findUnique();

        if (section == null) {
            section = Section.find.where().eq("alias", "").findUnique();
            if (section != null) {
                return redirect(routes.FrontController.section(""));
            } else {
                return badRequest("Page not found.");
            }
        } else {
            return ok(views.html.front.section.render(section));
        }
    }

    @AddCSRFToken
    public Result post(String sectionAlias, Integer postID) {
        Section section = Section.find.where().eq("alias", sectionAlias).findUnique();
        if (section == null) {
            section = Section.find.where().eq("alias", "").findUnique();
            if (section != null) {
                return redirect(routes.FrontController.section(""));
            } else {
                return badRequest("Page not found.");
            }
        }

        Post post = Post.find.byId(postID);
        if (post == null) {
            return redirect(routes.FrontController.section(sectionAlias));
        }

        Form<Comment> commentForm = formFactory.form(Comment.class);

        return ok(views.html.front.post.render(section, post, commentForm));
    }

    @RequireCSRFCheck
    public Result saveComment(String sectionAlias, Integer postID) {
        Section section = Section.find.where().eq("alias", sectionAlias).findUnique();
        if (section == null) {
            section = Section.find.where().eq("alias", "").findUnique();
            if (section != null) {
                return redirect(routes.FrontController.section(""));
            } else {
                return badRequest("Page not found.");
            }
        }

        Post post = Post.find.byId(postID);
        if (post == null) {
            return redirect(routes.FrontController.section(sectionAlias));
        }

        Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest();

        if (commentForm.hasErrors()) {
            return badRequest(views.html.front.post.render(section, post, commentForm));
        } else {
            Comment s = commentForm.get();
            s.post = post;
            s.save();

            return redirect(routes.FrontController.post(sectionAlias, postID));
        }
    }

}

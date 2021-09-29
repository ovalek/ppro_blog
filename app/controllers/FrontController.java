package controllers;

import models.Comment;
import models.Post;
import models.Section;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.api.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class FrontController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    Http.Request request;

    @Inject
    Messages messages;

    public Result section(String sectionAlias) {
        Section section = Section.find.query().where().eq("alias", sectionAlias).findOne();

        if (section == null) {
            section = Section.find.query().where().eq("alias", "").findOne();
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
        Section section = Section.find.query().where().eq("alias", sectionAlias).findOne();
        if (section == null) {
            section = Section.find.query().where().eq("alias", "").findOne();
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

        return ok(views.html.front.post.render(section, post, commentForm, request, messages));
    }

    @RequireCSRFCheck
    public Result saveComment(String sectionAlias, Integer postID) {
        Section section = Section.find.query().where().eq("alias", sectionAlias).findOne();
        if (section == null) {
            section = Section.find.query().where().eq("alias", "").findOne();
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

        Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest(request);

        if (commentForm.hasErrors()) {
            return badRequest(views.html.front.post.render(section, post, commentForm, request, messages));
        } else {
            Comment s = commentForm.get();
            s.post = post;
            s.save();

            return redirect(routes.FrontController.post(sectionAlias, postID));
        }
    }

}

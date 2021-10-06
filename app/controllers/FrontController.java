package controllers;

import models.Comment;
import models.Post;
import models.Section;
import models.view.DependenciesContainer;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class FrontController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    DependenciesContainer dc;

    public Result section(Http.Request request, String sectionAlias) {
        dc.setRequest(request);
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
    public Result post(Http.Request request, String sectionAlias, Integer postID) {
        dc.setRequest(request);
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
        System.out.println(post.tags.size());

        Form<Comment> commentForm = formFactory.form(Comment.class);

        return ok(views.html.front.post.render(section, post, commentForm, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result saveComment(Http.Request request, String sectionAlias, Integer postID) {
        dc.setRequest(request);
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

        Form<Comment> commentForm = formFactory.form(Comment.class).bindFromRequest(dc.request);

        if (commentForm.hasErrors()) {
            return badRequest(views.html.front.post.render(section, post, commentForm, dc.request, dc.messages));
        } else {
            Comment s = commentForm.get();
            s.post = post;
            s.save();

            return redirect(routes.FrontController.post(sectionAlias, postID));
        }
    }

}

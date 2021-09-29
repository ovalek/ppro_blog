package controllers;

import models.Comment;
import models.Post;
import models.Section;
import models.view.DependenciesContainer;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.posts.list;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminPostsController extends Controller {

    @Inject
    DependenciesContainer dc;

    @Inject
    FormFactory formFactory;

    private Section getSection(Integer sectionID) {
        Section section = null;

        if (sectionID == 0 && Section.find.query().findCount() > 0) {
            section = Section.find.all().get(0);
            redirect(routes.AdminPostsController.list(Integer.valueOf(section.id)));
        } else if (sectionID == 0) {
            redirect(routes.AdminHomeController.index());
        } else {
            section = Section.find.byId(sectionID);
        }

        return section;
    }

    @AddCSRFToken
    public Result list(Http.Request request, Integer sectionID) {
        dc.setRequest(request);
        dc.title = "Posts";

        Section section = getSection(sectionID);

        return ok(
                list.render(dc, section, CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"))
        );
    }

    @AddCSRFToken
    public Result post(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Section section = getSection(sectionID);

        Form<Post> postForm = formFactory.form(Post.class);

        if (postID != 0) {
            dc.title = "Update post";

            Post post = Post.find.byId(postID);
            if (post != null) {
                postForm = postForm.fill(post);
            } else {
                return redirect(routes.AdminPostsController.post(sectionID, 0));
            }
        } else {
            dc.title = "New post";
        }

        return ok(views.html.admin.posts.post.render(dc, section, postID, postForm, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result save(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Section section = getSection(sectionID);

        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(dc.request);

        if (postForm.hasErrors()) {
            return badRequest(views.html.admin.posts.post.render(dc, section, postID, postForm, dc.request, dc.messages));
        } else {
            Post p = postForm.get();

            if (postID != 0) {
                p.id = postID;
                p.update();
            } else {
                p.section = section;
                p.save();
            }

            return redirect(routes.AdminPostsController.list(sectionID));
        }
    }

    @RequireCSRFCheck
    public Result remove(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Post post = Post.find.byId(postID);
        if (post != null) {
            post.delete();
        }

        return redirect(routes.AdminPostsController.list(sectionID));
    }

    @AddCSRFToken
    public Result comments(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Section section = getSection(sectionID);

        dc.title = "Post comments";

        Post post = Post.find.byId(postID);
        if (post == null) {
            return redirect(routes.AdminPostsController.list(sectionID));
        }

        return ok(
                views.html.admin.posts.comments.render(dc, section, post, CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"), dc.request, dc.messages)
        );
    }

    @RequireCSRFCheck
    public Result removeComment(Http.Request request, Integer sectionID, Integer commentID) {
        dc.setRequest(request);
        Comment comment = Comment.find.byId(commentID);
        if (comment == null) {
            return redirect(routes.AdminPostsController.list(0));
        } else {
            Integer postID = comment.post.id;
            comment.delete();
            return redirect(routes.AdminPostsController.comments(sectionID, postID));
        }

        //Comment.find.byId(commentID).delete();

        //return redirect(routes.AdminPostsController.comments(sectionID, postID));
    }

    @RequireCSRFCheck
    public Result removeAllComments(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Post post = Post.find.byId(postID);
        if (post == null) {
            return redirect(routes.AdminPostsController.list(sectionID));
        }

        int deletedRows = Comment.find.query().where().eq("post_id", (int) postID).delete();

        return redirect(routes.AdminPostsController.comments(sectionID, postID));
    }

}

package controllers;

import models.*;
import models.view.DependenciesContainer;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.posts.list;

import javax.inject.Inject;
import java.util.*;

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
        List<Integer> selectedTags = new ArrayList<Integer>();

        if (postID != 0) {
            dc.title = "Update post";

            Post post = Post.find.byId(postID);
            if (post != null) {
                postForm = postForm.fill(post);
                for (Tag tag: post.tags) {
                    selectedTags.add(tag.id);
                }
            } else {
                return redirect(routes.AdminPostsController.post(sectionID, 0));
            }
        } else {
            dc.title = "New post";
        }

        return ok(views.html.admin.posts.post.render(dc, section, postID, postForm, Tag.getAllOrdered(), selectedTags, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result save(Http.Request request, Integer sectionID, Integer postID) {
        dc.setRequest(request);
        Section section = getSection(sectionID);

//        MultipartFormData<PostSaveData> data = dc.request.body().asMultipartFormData();
//        Wrappers.MapWrapper<String, String> data = dc.request.body().;

//        DynamicForm requestData = formFactory.form().bindFromRequest(dc.request);

//        Form<PostSaveData> postSaveDataForm = formFactory.form(PostSaveData.class).bindFromRequest(dc.request);
        Form<PostSaveData> postSaveDataForm = formFactory.form(PostSaveData.class).withDirectFieldAccess(true).bindFromRequest(dc.request);



//        List<Tag> tags = new ArrayList<Tag>();
//        for (String tagID: requestData.get("tags[]")) {
//
//        }
//

//        Map<String, String> newData = new HashMap<>();
//        Map<String, String[]> urlFormEncoded = dc.request.body().asFormUrlEncoded();
//        if (urlFormEncoded != null) {
//            for (String key : urlFormEncoded.keySet()) {
//                String[] value = urlFormEncoded.get(key);
//                if (value.length == 1) {
//                    newData.put(key, value[0]);
//                } else if (value.length > 1) {
//                    for (int i = 0; i < value.length; i++) {
//                        newData.put(key + "[" + i + "]", value[i]);
//                    }
//                }
//            }
//        }

//        Map<String, String> newData = new HashMap<>();
//        Map<String, String[]> urlFormEncoded = dc.request.body().asFormUrlEncoded();
//        if (urlFormEncoded != null) {
//            for (String key : urlFormEncoded.keySet()) {
//                String[] value = urlFormEncoded.get(key);
//                if (value.length == 1) {
//                    newData.put(key, value[0]);
//                } else if (value.length > 1) {
//                    String keyPrefix = key;
//                    String keyPostfix = "";
//                    int pos = key.indexOf(".");
//                    if (pos > -1) {
//                        keyPrefix = key.substring(0, pos);
//                        keyPostfix = key.substring(pos, key.length());
//                    }
//                    for (int i = 0; i < value.length; i++) {
//                        newData.put(keyPrefix + "[" + i + "]" + keyPostfix, value[i]);
//                    }
//                }
//            }
//        }

//        Map<String, String> newData = new HashMap<String, String>();
//        Map<String, String[]> urlFormEncoded = dc.request.body().asFormUrlEncoded();
//
//        if (urlFormEncoded != null) {
//            for (String key : urlFormEncoded.keySet()) {
//                String[] value = urlFormEncoded.get(key);
//                if (value.length == 1 || key.equals("published")) {
//                    newData.put(key, value[0]);
//                } else if (value.length > 1) {
//                    for (int i = 0; i < value.length; i++) {
//
//                        newData.put(key + "[" + i + "]", value[i]);
//                    }
//                }
//            }
//        }
        // bind to the MyEntity form object
//        Form<Post> postForm = formFactory.form(Post.class).bind(dc.lang, dc.request.attrs(), newData);

//        DynamicForm form = formFactory.form().bindFromRequest(dc.request);
//        Collection<String> tags = form.rawData().values();
//
//        // bind to the MyEntity form object
////        Form<Post> postForm = formFactory.form(Post.class).bindFromRequestData(newData);
//        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(dc.request);
//        Form<Post> postForm = formFactory.form(Post.class).bind(dc.lang, dc.request.attrs(), newData);


        java.util.List<play.data.validation.ValidationError> errors = new ArrayList<ValidationError>();
        if (postSaveDataForm.hasErrors()) {
            errors = postSaveDataForm.errors();
        }
        postSaveDataForm = postSaveDataForm.discardingErrors();
        PostSaveData psd = postSaveDataForm.get();
        Post tmpPost = new Post();
        tmpPost.title = psd.title;
        tmpPost.content = psd.content;
        tmpPost.published = psd.published;
        tmpPost.tags = Tag.find.query().where().in("id", psd.tags).orderBy("name ASC").findList();
        Form<Post> postForm = formFactory.form(Post.class).fill(tmpPost);
        for (ValidationError error: errors) {
            postForm = postForm.withError(error);
        }

//        Form<Post> postForm = formFactory.form(Post.class).bindFromRequest(dc.request);

//        if (postSaveDataForm.hasErrors()) {
        if (postForm.hasErrors()) {
//            Form<Post> postForm = formFactory.form(Post.class);
            List<Integer> selectedTags = new ArrayList<Integer>();
            if (tmpPost != null) {
                for (Tag tag: tmpPost.tags) {
                    selectedTags.add(tag.id);
                }
            }
            return badRequest(views.html.admin.posts.post.render(dc, section, postID, postForm, Tag.getAllOrdered(), selectedTags, dc.request, dc.messages));
        } else {
            Post p = postForm.get();
//            PostSaveData psd = postSaveDataForm.get();
//            Post p = new Post();
//            p.title = psd.title;
//            p.content = psd.content;
////            p.tags.addAll(psd.tags);
//            p.tags = Tag.find.query().where().in("id", psd.tags).orderBy("name ASC").findList();
//            for (Integer tagID: psd.tags) {
//                p.tags.add(tagID, );
//            }

            // Hack to fix saving empty tags
            if (p.tags.size() == 0) {
                p.tags = new ArrayList<>();
            }

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

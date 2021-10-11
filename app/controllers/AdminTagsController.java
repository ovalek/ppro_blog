package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Tag;
import models.view.DependenciesContainer;
import play.data.DynamicForm;
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
import views.html.admin.tags.list;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminTagsController extends Controller {

    @Inject
    DependenciesContainer dc;

    @Inject
    FormFactory formFactory;

    @AddCSRFToken
    public Result list(Http.Request request) {
        dc.setRequest(request);
        dc.title = "Tags";

        return ok(
                list.render(dc, Tag.find.query().where().orderBy("id ASC").findList(), CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"), dc.request, dc.messages)
        );
    }

    @AddCSRFToken
    public Result tag(Http.Request request, Integer tagID) {
        dc.setRequest(request);
        Form<Tag> tagForm = formFactory.form(Tag.class);

        if (tagID != 0) {
            dc.title = "Update tag";

            Tag tag = Tag.find.byId(tagID);
            if (tag != null) {
                tagForm = tagForm.fill(tag);
            } else {
                return redirect(routes.AdminTagsController.tag(0));
            }
        } else {
            dc.title = "New tag";
        }

        return ok(views.html.admin.tags.tag.render(dc, tagID, tagForm, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result save(Http.Request request, Integer tagID) {
        dc.setRequest(request);
        Form<Tag> tagForm = formFactory.form(Tag.class).bindFromRequest(dc.request);

        if (tagForm.hasErrors()) {
            return badRequest(views.html.admin.tags.tag.render(dc, tagID, tagForm, dc.request, dc.messages));
        } else {
            Tag t = tagForm.get();

            ValidationError result = t.saveWithValidation(tagID);
            if (result != null) {
                tagForm = tagForm.withError(result);
                return badRequest(views.html.admin.tags.tag.render(dc, tagID, tagForm, dc.request, dc.messages));
            }

            return redirect(routes.AdminTagsController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Http.Request request, Integer tagID) {
        dc.setRequest(request);
        Tag tag = Tag.find.byId(tagID);
        if (tag != null) {
            tag.delete();
        }

        return redirect(routes.AdminTagsController.list());
    }
}


package controllers;

import models.User;
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
import views.html.admin.users.list;

import javax.inject.Inject;
import java.util.List;

@Security.Authenticated(Secured.class)
public class AdminUsersController extends Controller {

    @Inject
    DependenciesContainer dc;

    @Inject
    FormFactory formFactory;

    @AddCSRFToken
    public Result list(Http.Request request) {
        dc.setRequest(request);
        dc.title = "Users";

        List<User> users = User.find.query().where().orderBy("name ASC").findList();
        return ok(list.render(dc, users, CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"), dc.request, dc.messages));
    }

    @AddCSRFToken
    public Result user(Http.Request request, Integer userID) {
        dc.setRequest(request);
        Form<User> userForm = formFactory.form(User.class);

        if (userID != 0) {
            dc.title = "Update user";

            User user = User.find.byId(userID);
            if (user != null) {
                userForm = userForm.fill(user);
            } else {
                return redirect(routes.AdminUsersController.user(0));
            }
        } else {
            dc.title = "New user";
        }

        return ok(views.html.admin.users.user.render(dc, userID, userForm, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result save(Http.Request request, Integer userID) {
        dc.setRequest(request);
        Form<User> userForm = formFactory.form(User.class).bindFromRequest(dc.request);

        if (userForm.hasErrors()) {
            return badRequest(views.html.admin.users.user.render(dc, userID, userForm, dc.request, dc.messages));
        } else {
            User u = userForm.get();

            boolean result = u.saveWithValidation(userID, userForm);
            if (!result) {
                return badRequest(views.html.admin.users.user.render(dc, userID, userForm, dc.request, dc.messages));
            }

            return redirect(routes.AdminUsersController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Http.Request request, Integer userID) {
        dc.setRequest(request);
        User user = User.find.byId(userID);
        if (user != null) {

            if (dc.session.get("email").equals(user.email)) {
                user.delete();
                return redirect(routes.FrontController.section("")).withNewSession();
            }

            user.delete();
        }

        return redirect(routes.AdminUsersController.list());
    }

}

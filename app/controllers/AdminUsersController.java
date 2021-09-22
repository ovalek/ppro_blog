package controllers;

import models.User;
import models.view.AdminViewModel;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.users.list;

import javax.inject.Inject;
import java.util.List;

@Security.Authenticated(Secured.class)
public class AdminUsersController extends Controller {

    @Inject
    AdminViewModel vm;

    @Inject
    FormFactory formFactory;

    @AddCSRFToken
    public Result list() {
        vm.title = "Users";

        List<User> users = User.find.where().orderBy("name ASC").findList();
        return ok(list.render(vm, users, CSRF.getToken(request()).map(t -> t.value()).orElse("no token")));
    }

    @AddCSRFToken
    public Result user(Integer userID) {
        Form<User> userForm = formFactory.form(User.class);

        if (userID != 0) {
            vm.title = "Update user";

            User user = User.find.byId(userID);
            if (user != null) {
                userForm = userForm.fill(user);
            } else {
                return redirect(routes.AdminUsersController.user(0));
            }
        } else {
            vm.title = "New user";
        }

        return ok(views.html.admin.users.user.render(vm, userID, userForm));
    }

    @RequireCSRFCheck
    public Result save(Integer userID) {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();

        if (userForm.hasErrors()) {
            return badRequest(views.html.admin.users.user.render(vm, userID, userForm));
        } else {
            User u = userForm.get();

            boolean result = u.saveWithValidation(userID, userForm);
            if (!result) {
                return badRequest(views.html.admin.users.user.render(vm, userID, userForm));
            }

            return redirect(routes.AdminUsersController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Integer userID) {
        User user = User.find.byId(userID);
        if (user != null) {

            if (session("email").equals(user.email)) {
                user.delete();
                session().clear();
                return redirect(routes.FrontController.section(""));
            }

            user.delete();
        }

        return redirect(routes.AdminUsersController.list());
    }

}

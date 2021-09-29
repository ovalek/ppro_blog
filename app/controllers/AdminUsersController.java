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

        List<User> users = User.find.query().where().orderBy("name ASC").findList();
        return ok(list.render(vm, users, CSRF.getToken(vm.request).map(t -> t.value()).orElse("no token"), vm.request, vm.messages));
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

        return ok(views.html.admin.users.user.render(vm, userID, userForm, vm.request, vm.messages));
    }

    @RequireCSRFCheck
    public Result save(Integer userID) {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest(vm.request);

        if (userForm.hasErrors()) {
            return badRequest(views.html.admin.users.user.render(vm, userID, userForm, vm.request, vm.messages));
        } else {
            User u = userForm.get();

            boolean result = u.saveWithValidation(userID, userForm);
            if (!result) {
                return badRequest(views.html.admin.users.user.render(vm, userID, userForm, vm.request, vm.messages));
            }

            return redirect(routes.AdminUsersController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Integer userID) {
        User user = User.find.byId(userID);
        if (user != null) {

            if (vm.session.get("email").equals(user.email)) {
                user.delete();
                return redirect(routes.FrontController.section("")).withNewSession();
            }

            user.delete();
        }

        return redirect(routes.AdminUsersController.list());
    }

}

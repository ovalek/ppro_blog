package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

import javax.inject.Inject;

public class AuthController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result login() {
        return ok(
            login.render(formFactory.form(LoginForm.class))
        );
    }

    public Result authenticate() {
        //return ok(User.find.all().toString());

        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.AdminHomeController.index()
            );
        }
    }

    public Result logout() {
        session().clear();
        return redirect(routes.FrontController.section(""));
    }


    public static class LoginForm {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid email or password";
            }
            return null;
        }
    }
}

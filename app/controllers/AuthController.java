package controllers;

import models.User;
import models.view.DependenciesContainer;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.login;

import javax.inject.Inject;

public class AuthController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    DependenciesContainer dc;

    public Result login(Http.Request request) {
        dc.request = request;
        return ok(
            login.render(formFactory.form(LoginForm.class), dc.request, dc.messages)
        );
    }

    public Result authenticate(Http.Request request) {
        dc.request = request;
        //return ok(User.find.all().toString());

        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest(dc.request);

        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm, dc.request, dc.messages));
        } else {
//            session().clear();
            Http.Session newSession = new Http.Session().adding("email", loginForm.get().email);
            return redirect(
                    routes.AdminHomeController.index()
            ).withSession(newSession);
        }
    }

    public Result logout(Http.Request request) {
        dc.request = request;
//        session().clear();
        return redirect(routes.FrontController.section("")).withNewSession();
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

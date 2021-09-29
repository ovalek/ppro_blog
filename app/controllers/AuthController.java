package controllers;

import models.User;
//import play.data.Form;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.login;

import javax.inject.Inject;

public class AuthController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    Http.Request request;

    @Inject
    Messages messages;

    public Result login() {
        return ok(
            login.render(formFactory.form(LoginForm.class), request, messages)
        );
    }

    public Result authenticate() {
        //return ok(User.find.all().toString());

        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest(request);

        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm, request, messages));
        } else {
//            session().clear();
            Http.Session newSession = new Http.Session().adding("email", loginForm.get().email);
            return redirect(
                    routes.AdminHomeController.index()
            ).withSession(newSession);
        }
    }

    public Result logout() {
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

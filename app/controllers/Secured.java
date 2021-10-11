package controllers;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;

public class Secured extends Security.Authenticator {

    @Override
    public Optional getUsername(Http.Request req) {
        return req.session().get("email");
    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return redirect(controllers.routes.AuthController.login()).
                flashing("danger",  "You need to login before access the application.");
    }
}

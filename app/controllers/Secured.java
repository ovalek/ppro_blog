package controllers;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;

public class Secured extends Security.Authenticator {

//    @Override
//    public String getUsername(Http.Context ctx) {
//        return ctx.session().get("email");
//    }
//
//    @Override
//    public play.mvc.Result onUnauthorized(Http.Context ctx) {
//        return redirect(routes.AuthController.login());
//    }

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

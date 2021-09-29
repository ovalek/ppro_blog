package controllers;

import models.view.DependenciesContainer;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminHomeController extends Controller {

    @Inject
    DependenciesContainer dc;

    public Result index(Http.Request request) {
        dc.setRequest(request);
        dc.title = "Blog administration";
        return ok(views.html.admin.index.render(dc));
    }

}

package controllers;

import models.view.AdminViewModel;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminHomeController extends Controller {

    @Inject
    AdminViewModel vm;

    public Result index() {
        vm.title = "Blog administration";
        return ok(views.html.admin.index.render(vm));
    }

}

package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Section;
import models.view.AdminViewModel;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.sections.list;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminSectionsController extends Controller {

    @Inject
    AdminViewModel vm;

    @Inject
    FormFactory formFactory;

    @AddCSRFToken
    public Result list() {
        vm.title = "Sections";

        return ok(
                list.render(vm, Section.find.query().where().orderBy("menu_order ASC").findList(), CSRF.getToken(vm.request).map(t -> t.value()).orElse("no token"), vm.request, vm.messages)
        );
    }

    @AddCSRFToken
    public Result sort() {
        vm.title = "Sort sections";

        return ok(
                views.html.admin.sections.sort.render(vm, Section.find.query().where().orderBy("menu_order ASC").findList(), CSRF.getToken(vm.request).map(t -> t.value()).orElse("no token"), vm.request, vm.messages)
        );
    }

    @RequireCSRFCheck
    public Result saveOrder() {
        boolean result;

        DynamicForm form = formFactory.form().bindFromRequest(vm.request);
        if (form.rawData().size() == 0) {
            result = false;
        } else {
            result = Section.saveOrder(form.get("serializedOrder"));
        }

        ObjectNode jsonResponse = play.libs.Json.newObject();
        jsonResponse.put("status", result);
        if (result) {
            jsonResponse.put("msg", "Sections order was saved.");
            return ok(jsonResponse);
        } else {
            jsonResponse.put("msg", "Error occured while saving.");
            return badRequest(jsonResponse);
        }
    }

    @AddCSRFToken
    public Result section(Integer sectionID) {
        Form<Section> sectionForm = formFactory.form(Section.class);

        if (sectionID != 0) {
            vm.title = "Update section";

            Section section = Section.find.byId(sectionID);
            if (section != null) {
                sectionForm = sectionForm.fill(section);
            } else {
                return redirect(routes.AdminSectionsController.section(0));
            }
        } else {
            vm.title = "New section";
        }

        return ok(views.html.admin.sections.section.render(vm, sectionID, sectionForm, vm.request, vm.messages));
    }

    @RequireCSRFCheck
    public Result save(Integer sectionID) {
        Form<Section> sectionForm = formFactory.form(Section.class).bindFromRequest(vm.request);

        if (sectionForm.hasErrors()) {
            return badRequest(views.html.admin.sections.section.render(vm, sectionID, sectionForm, vm.request, vm.messages));
        } else {
            Section s = sectionForm.get();

            boolean result = s.saveWithValidation(sectionID, sectionForm);
            if (!result) {
                return badRequest(views.html.admin.sections.section.render(vm, sectionID, sectionForm, vm.request, vm.messages));
            }

            return redirect(routes.AdminSectionsController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Integer sectionID) {
        Section section = Section.find.byId(sectionID);
        if (section != null) {
            section.delete();
        }

        return redirect(routes.AdminSectionsController.list());
    }
}

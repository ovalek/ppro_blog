package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Section;
import models.view.DependenciesContainer;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.sections.list;

import javax.inject.Inject;

@Security.Authenticated(Secured.class)
public class AdminSectionsController extends Controller {

    @Inject
    DependenciesContainer dc;

    @Inject
    FormFactory formFactory;

    @AddCSRFToken
    public Result list(Http.Request request) {
        dc.setRequest(request);
        dc.title = "Sections";

        return ok(
                list.render(dc, Section.find.query().where().orderBy("menu_order ASC").findList(), CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"), dc.request, dc.messages)
        );
    }

    @AddCSRFToken
    public Result sort(Http.Request request) {
        dc.setRequest(request);
        dc.title = "Sort sections";

        return ok(
                views.html.admin.sections.sort.render(dc, Section.find.query().where().orderBy("menu_order ASC").findList(), CSRF.getToken(dc.request).map(t -> t.value()).orElse("no token"), dc.request, dc.messages)
        );
    }

    @RequireCSRFCheck
    public Result saveOrder(Http.Request request) {
        dc.setRequest(request);
        boolean result;

        DynamicForm form = formFactory.form().bindFromRequest(dc.request);
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
    public Result section(Http.Request request, Integer sectionID) {
        dc.setRequest(request);
        Form<Section> sectionForm = formFactory.form(Section.class);

        if (sectionID != 0) {
            dc.title = "Update section";

            Section section = Section.find.byId(sectionID);
            if (section != null) {
                sectionForm = sectionForm.fill(section);
            } else {
                return redirect(routes.AdminSectionsController.section(0));
            }
        } else {
            dc.title = "New section";
        }

        return ok(views.html.admin.sections.section.render(dc, sectionID, sectionForm, dc.request, dc.messages));
    }

    @RequireCSRFCheck
    public Result save(Http.Request request, Integer sectionID) {
        dc.setRequest(request);
        Form<Section> sectionForm = formFactory.form(Section.class).bindFromRequest(dc.request);

        if (sectionForm.hasErrors()) {
            return badRequest(views.html.admin.sections.section.render(dc, sectionID, sectionForm, dc.request, dc.messages));
        } else {
            Section s = sectionForm.get();

            boolean result = s.saveWithValidation(sectionID, sectionForm);
            if (!result) {
                return badRequest(views.html.admin.sections.section.render(dc, sectionID, sectionForm, dc.request, dc.messages));
            }

            return redirect(routes.AdminSectionsController.list());
        }
    }

    @RequireCSRFCheck
    public Result remove(Http.Request request, Integer sectionID) {
        dc.setRequest(request);
        Section section = Section.find.byId(sectionID);
        if (section != null) {
            section.delete();
        }

        return redirect(routes.AdminSectionsController.list());
    }
}

package models.view;

import play.api.i18n.Messages;
import play.mvc.Http;

import javax.inject.Inject;

public class AdminViewModel {


    /*public Session session;

    /*@Inject
    public AdminViewModel (Session session) {
        this.session = session;
    }*/

    public Http.Request request;

    public Http.Session session;

    public Messages messages;

    @Inject
    public AdminViewModel (Http.Request request, Http.Session session, Messages messages) {
        this.request = request;
        this.session = session;
        this.messages = messages;
    }

    //public User user = User.find.where().eq("email", session.get("email")).findUnique();

    //public User user = User.find.where().eq("email", context.current().session.get("email")).findUnique();

    public String title;
}

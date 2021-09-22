package models.view;

import play.mvc.Http;

public class AdminViewModel {


    /*public Session session;

    /*@Inject
    public AdminViewModel (Session session) {
        this.session = session;
    }*/

    public Http.Request request;

    public Http.Session session;

    @Inject
    public AdminViewModel (Http.Request request, Http.Session session) {
        this.request = request;
        this.session = session;
    }

    //public User user = User.find.where().eq("email", session.get("email")).findUnique();

    //public User user = User.find.where().eq("email", context.current().session.get("email")).findUnique();

    public String title;
}

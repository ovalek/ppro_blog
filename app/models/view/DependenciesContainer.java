package models.view;

import play.api.i18n.*;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Locale;

public class DependenciesContainer {


    /*public Session session;

    /*@Inject
    public AdminViewModel (Session session) {
        this.session = session;
    }*/

    public Http.Request request;

    public Http.Session session;

    public MessagesApi messagesApi;

//    public MessagesProvider messagesProvider;

    public Messages messages;

    @Inject
    public DependenciesContainer(MessagesApi messagesApi) {
//    public DependenciesContainer(Http.Request request, Http.Session session, MessagesApi messagesApi, MessagesProvider messagesProvider, Lang lang) {
//        this.request = request;
//        this.session = session;
        this.messagesApi = messagesApi;
//        this.messagesProvider = messagesProvider;
        this.messages = new MessagesImpl(new Lang(new Locale("en", "en", "en")), messagesApi);
    }

    //public User user = User.find.where().eq("email", session.get("email")).findUnique();

    //public User user = User.find.where().eq("email", context.current().session.get("email")).findUnique();


    public void setRequest(Http.Request request) {
        this.request = request;
        this.session = request.session();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;
}

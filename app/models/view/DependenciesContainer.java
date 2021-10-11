package models.view;

import play.api.i18n.*;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Locale;

public class DependenciesContainer {

    public Http.Request request;

    public Http.Session session;

    public MessagesApi messagesApi;

    public Messages messages;

    @Inject
    public DependenciesContainer(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
        this.messages = new MessagesImpl(new Lang(new Locale("en", "en", "en")), messagesApi);
    }

    public void setRequest(Http.Request request) {
        this.request = request;
        this.session = request.session();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;
}

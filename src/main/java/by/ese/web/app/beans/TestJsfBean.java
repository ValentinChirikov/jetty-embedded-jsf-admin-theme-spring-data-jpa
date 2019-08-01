package by.ese.web.app.beans;

import by.ese.web.app.service.MsgService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class TestJsfBean {
    @Inject
    private MsgService msgService;

    public String getMsg() {
        return msgService.getMsg();
    }
}
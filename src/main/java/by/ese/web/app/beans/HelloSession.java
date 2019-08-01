package by.ese.web.app.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class HelloSession implements Serializable {

    private String name;
    private String message;


    public void createMessage() {
        message = "Hello, " + name + "!";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

}
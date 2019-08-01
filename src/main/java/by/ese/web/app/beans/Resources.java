package by.ese.web.app.beans;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;


@Dependent
public class Resources {

    @Produces
    public Logger getLogger(InjectionPoint p) {
        return Logger.getLogger(p.getMember().getDeclaringClass().getName());
    }

}
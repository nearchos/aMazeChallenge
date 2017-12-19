package org.inspirecenter.amazechallenge;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.*;
import org.inspirecenter.amazechallenge.model.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OfyHelper implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warm-up request, or the first user request if no warm-up request.

        // server-specific data types
        ObjectifyService.register(Parameter.class);

        // common data types
        ObjectifyService.register(Challenge.class);
        ObjectifyService.register(Game.class);
        ObjectifyService.register(Player.class);
        ObjectifyService.register(Position.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}
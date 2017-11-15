package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GameStateServlet extends HttpServlet {

    public static final String LAST_UPDATED_TIMESTAMP = "LAST_UPDATED_TIMESTAMP";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        final long lastUpdated = memcacheService.contains(LAST_UPDATED_TIMESTAMP) ? (long) memcacheService.get(LAST_UPDATED_TIMESTAMP) : 0L;
        final long now = System.currentTimeMillis();
        if(now - lastUpdated > 1000L) {
            // todo generate new state and store on memcache
        } else {
            // todo get stored state from memcache
        }

        //todo return cached value
    }
}

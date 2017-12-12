package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.amazechallenge.model.GameFullState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.admin.RunEngineServlet.KEY_CACHED_GAME;

public class GameStateServlet extends HttpServlet {

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String challengeIdAsString = request.getParameter("id");

        final Vector<String> errors = new Vector<>();

        GameFullState gameFullState = null;

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(email == null || email.isEmpty()) {
            errors.add("Missing or empty 'email' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else {
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

            gameFullState = (GameFullState) memcacheService.get(KEY_CACHED_GAME.replaceAll("%", challengeIdAsString));
            if(gameFullState == null) errors.add("Could not find game for challenge id '" + challengeIdAsString + "' in memcache");
        }

        // return cached or generated value
        final String reply;
        if(errors.isEmpty()) {
            reply = gson.toJson(new ReplyWithGameFullState(gameFullState));
        } else {
            reply = gson.toJson(new ReplyWithErrors(errors));
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}
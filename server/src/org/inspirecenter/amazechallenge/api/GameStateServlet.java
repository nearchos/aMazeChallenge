package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.inspirecenter.amazechallenge.model.Game;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.admin.RunEngineServlet.KEY_CACHED_GAME;

public class GameStateServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String challengeIdAsString = request.getParameter("id");

        final Vector<String> errors = new Vector<>();

        Game game = null;

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

            game = (Game) memcacheService.get(KEY_CACHED_GAME.replaceAll("%", challengeIdAsString));
            if(game == null) errors.add("Could not find game for challenge id '" + challengeIdAsString + "' in memcache");
        }

        // return cached or generated value
        final String reply;
        if(errors.isEmpty()) {
            reply = ReplyBuilder.createReplyWithGame(game);
        } else {
            reply = ReplyBuilder.createReplyWithErrors(errors);
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}
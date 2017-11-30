package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import org.inspirecenter.amazechallenge.data.GameState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class GameStateServlet extends HttpServlet {

    public static final String KEY_CACHED_GAME_STATE  = "CACHED_GAME_STATE";
    public static final String KEY_LAST_UPDATED_TIMESTAMP = "KEY_LAST_UPDATED_TIMESTAMP";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String challengeIdAsString = request.getParameter("id");

        final Vector<String> errors = new Vector<>();

        GameState gameState = null;

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
            final long lastUpdated = memcacheService.contains(KEY_LAST_UPDATED_TIMESTAMP) ? (long) memcacheService.get(KEY_LAST_UPDATED_TIMESTAMP) : 0L;
            final long now = System.currentTimeMillis();

            if(now - lastUpdated <= 1000L) {
                // get stored state from memcache
                gameState = (GameState) memcacheService.get(KEY_CACHED_GAME_STATE);
            } else {
                // generate new state and store on memcache
                gameState = new GameState(); // todo generate new state
                memcacheService.put(KEY_CACHED_GAME_STATE, gameState);
            }
        }

        // return cached or generated value
        final String reply;
        if(errors.isEmpty()) {
            reply = ReplyBuilder.createReplyWithGameState(gameState);
        } else {
            reply = ReplyBuilder.createReplyWithErrors(errors);
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}
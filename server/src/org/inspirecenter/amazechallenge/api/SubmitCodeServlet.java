package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class SubmitCodeServlet extends HttpServlet {

    private static final String KEY_MAZE_SOLVER_CODE = "challenge-%1-player-%2";

    private final Gson gson = new Gson();

    private Logger log = Logger.getLogger(getClass().getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String magic = request.getParameter("magic");
        final String playerId = request.getParameter("id");
        final String challengeIdAsString = request.getParameter("challenge");

        final BufferedReader bufferedReader = request.getReader();
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        final String code = stringBuilder.toString();

        final Vector<String> errors = new Vector<>();

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(playerId == null || playerId.isEmpty()) {
            errors.add("Missing or empty 'id' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else if(code.isEmpty()) {
            errors.add("Empty 'code' payload");
        } else {
            try {
                final long challengeId = Long.parseLong(challengeIdAsString);
                final Challenge challenge = ObjectifyService.ofy().load().type(Challenge.class).id(challengeId).now();
                if(challenge == null) {
                    errors.add("Invalid or unknown challenge for id: " + challengeId);
                } else {

                    final Game game = ofy()
                            .load()
                            .type(Game.class)
                            .filter("challengeId = ", challengeId)
                            .first().now();

                    if(game == null) {
                        errors.add("Invalid game for selected challengeId: " + challengeId);
                    } else {
                        if(!game.containsPlayerById(playerId)) {
                            errors.add("Player not found in specified challenge for 'id': " + playerId);
                        } else {
                            game.resetPlayerById(playerId);

                            // store maze solver code in data-store
                            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
log("Submit code. Adding binding in memcache to key: " + getKey(challengeId, playerId));
                            memcacheService.put(getKey(challengeId, playerId), code);
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                errors.add(nfe.getMessage());
            }
        }

        final String reply;
        if(errors.isEmpty()) {
            reply = gson.toJson(new Reply());
        } else {
            reply = gson.toJson(new ReplyWithErrors(errors));
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }

    public static String getKey(final long challengeId, final String playerId) {
        return KEY_MAZE_SOLVER_CODE.replace("%1", Long.toString(challengeId)).replace("%2", playerId);
    }
}
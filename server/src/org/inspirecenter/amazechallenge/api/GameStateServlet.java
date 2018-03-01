package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import org.inspirecenter.amazechallenge.admin.RunEngineServlet;
import org.inspirecenter.amazechallenge.model.GameFullState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Logger;

public class GameStateServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(GameStateServlet.class.getName());

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String installationId = request.getParameter("installation");
        final String challengeIdAsString = request.getParameter("challenge");

        final Vector<String> errors = new Vector<>();

        GameFullState gameFullState = null;

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(installationId == null || installationId.isEmpty()) {
            errors.add("Missing or empty 'installation' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else {
            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

            gameFullState = (GameFullState) memcacheService.get(RunEngineServlet.getGameKey(challengeIdAsString));

            if(gameFullState == null) {
                log.severe("Could not find game for challenge id '" + challengeIdAsString + "' in memcache (missing key: " + RunEngineServlet.getGameKey(challengeIdAsString) + ")");
                errors.add("Could not find game for challenge id '" + challengeIdAsString + "' in memcache");
            } else if(!gameFullState.getAllPlayerIDs().contains(installationId)) {
                log.warning("Could not find existing installation (player) ID '" + installationId + "' in game");
                errors.add("Invalid player id '" + installationId + "' in memcache");
            }
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
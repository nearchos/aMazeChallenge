package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.data.ChallengeInstance;
import org.inspirecenter.amazechallenge.data.GameState;
import org.inspirecenter.amazechallenge.model.Challenge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RunEngineServlet extends HttpServlet {

    public static final String KEY_CACHED_GAME_STATE = "cached-game-state-%";
    public static final String KEY_CACHED_LEADER_BOARD = "cached-leader-board-%";

    private Logger log = Logger.getAnonymousLogger();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String challengeIdAsString = request.getParameter("challenge-id");
        try {
            final long challengeId = Long.parseLong(challengeIdAsString);
            final Challenge challenge = ObjectifyService.ofy().load().type(Challenge.class).id(challengeId).now();
            ChallengeInstance challengeInstance = ObjectifyService.ofy().load()
                    .type(ChallengeInstance.class)
                    .filter("challengeId = ", challengeId)
                    .first().now();

            final GameState gameState = challengeInstance.getGameState();
            final Set<String> playerEmails = challengeInstance.getPlayerEmails();
            final Map<String,PlayerMove> emailsToPlayerMoves = new HashMap<>();

            // todo ... revise to make multi-threaded and with deadlines?
            for(final String playerEmail : playerEmails) {
                final String playerCode = challengeInstance.getLatestSubmittedCode(playerEmail);

                final PlayerMove playerMove = PlayerMove.randomPlayerMove(); // todo get actual move
                emailsToPlayerMoves.put(playerEmail, playerMove);
            }
            System.out.println("moves: " + emailsToPlayerMoves);

            // update game state
            updateGameState(challenge, challengeInstance, emailsToPlayerMoves);

        } catch (NumberFormatException nfe) {
            log.warning("Could not parse parameter 'challenge-id' with value: " + challengeIdAsString + " (" + nfe.getMessage() + ")");
        }
    }

    private void updateGameState(final Challenge challenge, final ChallengeInstance challengeInstance, final Map<String,PlayerMove> emailsToPlayerMoves) {

        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

        // 1. update game state
        final GameState gameState = (GameState) memcacheService.get(KEY_CACHED_GAME_STATE.replaceAll("%", Long.toString(challenge.getId())));
        // todo ...
        memcacheService.put(KEY_CACHED_GAME_STATE.replaceAll("%", Long.toString(challenge.getId())), gameState);

        // 2. update leader board
        final Object leaderBoard = memcacheService.get(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())));
        // todo ... update leader board
        memcacheService.put(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())), leaderBoard);
    }
}
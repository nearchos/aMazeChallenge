package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.inspirecenter.amazechallenge.api.Common;
import org.inspirecenter.amazechallenge.api.ReplyWithErrors;
import org.inspirecenter.amazechallenge.api.ReplyWithGameFullState;
import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.data.ChallengeInstance;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.GameFullState;
import org.inspirecenter.amazechallenge.model.Player;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class RunEngineServlet extends HttpServlet {

    public static final String KEY_CACHED_GAME = "cached-game-state-%";
    public static final String KEY_CACHED_LEADER_BOARD = "cached-leader-board-%";

    private Logger log = Logger.getAnonymousLogger();

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("Running 'RunEngineServlet'");

        final Vector<String> errors = new Vector<>();

        final String magic = request.getParameter("magic");
        final String challengeIdAsString = request.getParameter("challenge-id");
        final String challengeInstanceIdAsString = request.getParameter("challenge-instance-id");
        final String gameIdAsString = request.getParameter("game-id");

        GameFullState gameFullState = null;

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else if(challengeInstanceIdAsString == null || challengeInstanceIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge instance 'id'");
        } else if(gameIdAsString == null || gameIdAsString.isEmpty()) {
            errors.add("Missing or empty game 'id'");
        } else {
            try {
                final long challengeId = Long.parseLong(challengeIdAsString);
                final long challengeInstanceId = Long.parseLong(challengeInstanceIdAsString);
                final long gameId = Long.parseLong(gameIdAsString);


                gameFullState = ofy().transact(() -> {
                    final Challenge challenge = ofy().load().key(Key.create(Challenge.class, challengeId)).now();

                    final ChallengeInstance challengeInstance = ofy().load().key(Key.create(ChallengeInstance.class, challengeInstanceId)).now();

                    Game game = gameId == 0 ? new Game(challengeId, challenge.getGrid()) : ofy().load().key(Key.create(Game.class, gameId)).now();

                    // update game state
                    game = implementGameLogic(challenge, challengeInstance, game);

                    // store new game in data store
                    ofy().save().entity(game).now();
                    ofy().save().entity(challengeInstance).now();

                    return new GameFullState(game.getPlayerPositionAndDirections(), game.getQueuedPlayers(), game.getPlayerEmailsToPlayers(), game.getGrid(), game.getLastUpdated(), game.getCounter());
                });

                // update values in memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

//log.info("Storing game in memcache: " + KEY_CACHED_GAME.replaceAll("%", Long.toString(challengeId)) + " -> " + gameFullState);

                // 1. update game state
                memcacheService.put(KEY_CACHED_GAME.replaceAll("%", Long.toString(challengeId)), gameFullState);

                // 2. update leader board
//        final Object leaderBoard = memcacheService.get(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())));
                // todo ... update leader board
//        memcacheService.put(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())), leaderBoard);

                // check if active or queued players players, then repeat
                if(!gameFullState.getAllPlayerEmails().isEmpty()) {
                    // schedule next run
                    final Queue queue = QueueFactory.getDefaultQueue();
                    TaskOptions taskOptions = TaskOptions.Builder
                            .withUrl("/admin/run-engine")
                            .param("magic", magic)
                            .param("challenge-id", Long.toString(challengeId))
                            .param("challenge-instance-id", Long.toString(challengeInstanceId))
                            .param("game-id", Long.toString(gameId))
                            .countdownMillis(2500) // wait 1/10th of a second before running again
                            .method(TaskOptions.Method.GET);
                    queue.add(taskOptions);
                }
            } catch (NumberFormatException nfe) {
                log.warning("Could not parse parameter 'challenge-id' or 'challenge-instance-id' or 'game-id' (" + nfe.getMessage() + ")");
                errors.add("Could not parse parameter 'challenge-id' or 'challenge-instance-id' or 'game-id' (" + nfe.getMessage() + ")");
            }
        }

        if(gameFullState == null) {
            errors.add("Invalid state (gameFullState=null).");
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

    private Game implementGameLogic(final Challenge challenge, final ChallengeInstance challengeInstance, final Game game) {

        // first check if we need to remove any players
        RuntimeController.removePlayersWhoHaveReachedTheTargetPosition(game);

        // next check if we can add more players
        while(challengeInstance.hasPendingPlayers()) {
            final String email = challengeInstance.peekPendingPlayerEmail();
            final String latestSubmittedCode = challengeInstance.getLatestSubmittedCode(email);

            if(latestSubmittedCode != null) {
                final ChallengeInstance.SubmissionDetails submissionDetails = challengeInstance.pollPendingPlayer();
                final Player player = challengeInstance.getPlayer(email);
                game.addPlayer(player, latestSubmittedCode);
            }
        }

        // now check if we can upgrade any players from 'queued' to 'active'
        while(game.getNumberOfActivePlayers() < challenge.getMaxActivePlayers() && game.hasQueuedPlayers()) {
            // activate players as needed
            game.activateNextPlayer();
        }

        // todo ... revise to make multi-threaded and with deadlines?
        RuntimeController.makeMove(game);

        // update game with number of rounds executed
        game.touch();

        return game;
    }
}
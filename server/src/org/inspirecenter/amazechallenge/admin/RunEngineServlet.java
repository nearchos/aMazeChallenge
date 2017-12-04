package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.api.Common;
import org.inspirecenter.amazechallenge.api.ReplyBuilder;
import org.inspirecenter.amazechallenge.data.ChallengeInstance;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Player;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver.PARAMETER_KEY_CODE;

public class RunEngineServlet extends HttpServlet {

    public static final String KEY_CACHED_GAME = "cached-game-state-%";
    public static final String KEY_CACHED_LEADER_BOARD = "cached-leader-board-%";

    private Logger log = Logger.getAnonymousLogger();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("Running 'RunEngineServlet'");

        final Vector<String> errors = new Vector<>();

        final String magic = request.getParameter("magic");
        final String challengeIdAsString = request.getParameter("challenge-id");
        final String challengeInstanceIdAsString = request.getParameter("challenge-instance-id");
        final String gameIdAsString = request.getParameter("game-id");

        Game game = null;

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


                game = ofy().transact(new Work<Game>() {
                    @Override
                    public Game run() {
                        final Challenge challenge = ofy().load().key(Key.create(Challenge.class, challengeId)).now();

                        final ChallengeInstance challengeInstance = ofy().load().key(Key.create(ChallengeInstance.class, challengeInstanceId)).now();

                        Game game = gameId == 0 ? new Game(challengeId, challenge.getGrid()) : ofy().load().key(Key.create(Game.class, gameId)).now();

                        // update game state
                        game = implementGameLogic(challenge, challengeInstance, game);

                        // store new game in data store
                        ofy().save().entity(game).now();
                        ofy().save().entity(challengeInstance).now();

                        return game;
                    }
                });

                // update values in memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

log.info("Storing game in memcache: " + KEY_CACHED_GAME.replaceAll("%", Long.toString(challengeId)) + " -> " + game);

                // 1. update game state
                memcacheService.put(KEY_CACHED_GAME.replaceAll("%", Long.toString(challengeId)), game);

                // 2. update leader board
//        final Object leaderBoard = memcacheService.get(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())));
                // todo ... update leader board
//        memcacheService.put(KEY_CACHED_LEADER_BOARD.replaceAll("%", Long.toString(challenge.getId())), leaderBoard);

                // check if remaining players, then repeat
                if(game.hasActivePlayers()) {
                    // schedule next run
                    final Queue queue = QueueFactory.getDefaultQueue();
                    TaskOptions taskOptions = TaskOptions.Builder
                            .withUrl("/admin/run-engine")
                            .param("magic", magic)
                            .param("challenge-id", Long.toString(challengeId))
                            .param("challenge-instance-id", Long.toString(challengeInstanceId))
                            .param("game-id", Long.toString(game.id))
                            .countdownMillis(500) // wait 1/10th of a second before running again
                            .method(TaskOptions.Method.GET);
                    queue.add(taskOptions);
                }
            } catch (NumberFormatException nfe) {
                log.warning("Could not parse parameter 'challenge-id' or 'challenge-instance-id' or 'game-id' (" + nfe.getMessage() + ")");
                errors.add("Could not parse parameter 'challenge-id' or 'challenge-instance-id' or 'game-id' (" + nfe.getMessage() + ")");
            }
        }

        if(game == null) {
            errors.add("Invalid state (game=null).");
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

    private Game implementGameLogic(final Challenge challenge, final ChallengeInstance challengeInstance, final Game game) {

        // first check if we need to remove any players
        game.removePlayersWhoHaveReachedTheTargetPosition();

        // next check if we can add more players
        while(challengeInstance.hasPendingPlayers()) {
            final String email = challengeInstance.peekPendingPlayerEmail();
            final String latestSubmittedCode = challengeInstance.getLatestSubmittedCode(email);

            if(latestSubmittedCode != null) {
                final ChallengeInstance.SubmissionDetails submissionDetails = challengeInstance.pollPendingPlayer();
                final Player player = challengeInstance.getPlayer(email);
                game.addPlayer(player, latestSubmittedCode);
            }
//            final Map<String,Serializable> parameters = new HashMap<>();
//            parameters.put(PARAMETER_KEY_CODE, challengeInstance.getLatestSubmittedCode(email));
//            game.addPlayer(player, InterpretedMazeSolver.class, parameters);
        }

//        final Set<String> playerEmails = challengeInstance.getPlayerEmails();
//        final Map<String,PlayerMove> emailsToPlayerMoves = new HashMap<>();

        // todo ... revise to make multi-threaded and with deadlines?
//        for(final String playerEmail : playerEmails) {
//            final String playerCode = challengeInstance.getLatestSubmittedCode(playerEmail);
//
//            final PlayerMove playerMove = PlayerMove.randomPlayerMove(); // todo get actual move
//            emailsToPlayerMoves.put(playerEmail, playerMove);
//        }
//        System.out.println("moves: " + emailsToPlayerMoves);

//        final Map<String,PlayerMove> emailsToPlayerMoves; // todo
        game.touch();

        return game;
    }
}
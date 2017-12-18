package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.MazeSolver;
import org.inspirecenter.amazechallenge.api.Common;
import org.inspirecenter.amazechallenge.api.ReplyWithErrors;
import org.inspirecenter.amazechallenge.api.ReplyWithGameFullState;
import org.inspirecenter.amazechallenge.api.SubmitCodeServlet;
import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class RunEngineServlet extends HttpServlet {

    public static final String KEY_CACHED_GAME = "cached-game-state-%";
    public static final String KEY_CACHED_MAZE_SOLVER_STATE = "cached-maze-solver-state-%1-player-%2";
    public static final String KEY_CACHED_LEADER_BOARD = "cached-leader-board-%";

    private java.util.logging.Logger log = java.util.logging.Logger.getAnonymousLogger();

    private final Gson gson = new Gson();

    // update values in memcache
    private final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // log.info("Running 'RunEngineServlet'");

        final Vector<String> errors = new Vector<>();

        final String magic = request.getParameter("magic");
        final String challengeIdAsString = request.getParameter("challenge-id");
        final String gameIdAsString = request.getParameter("game-id"); // todo is this necessary? or could we simply query ofy by challengeId?

        GameFullState gameFullState = null;

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else if(gameIdAsString == null || gameIdAsString.isEmpty()) {
            errors.add("Missing or empty game 'id'");
        } else {
            try {
                final long challengeId = Long.parseLong(challengeIdAsString);
                final long gameId = Long.parseLong(gameIdAsString);

                final Challenge challenge = ofy().load().key(Key.create(Challenge.class, challengeId)).now();

                final Game game = ofy().transact(() -> {

                    final Game initialGame = ofy().load().key(Key.create(Game.class, gameId)).now();

                    // update game state
                    final Game updatedGame = implementGameLogic(challenge, initialGame);

                    // store new game in data store
                    ofy().save().entity(updatedGame).now();

                    return updatedGame;
                });

                gameFullState = game.getFullState(challenge.getGrid());

                // 1. update game state
                memcacheService.put(getGameKey(challengeId), gameFullState);

                // 2. update leader board
                // todo ... update leader board and save in memcache

                // Scheduling next run...
                // log.info("Scheduling next run...");

                // check if active or queued players players, then repeat
                if(challenge.isActive() && game.hasAnyPlayers()) {

                    // schedule next run
                    final Queue queue = QueueFactory.getDefaultQueue();
                    TaskOptions taskOptions = TaskOptions.Builder
                            .withUrl("/admin/run-engine")
                            .param("magic", magic)
                            .param("challenge-id", Long.toString(challengeId))
                            .param("game-id", Long.toString(gameId))
                            .countdownMillis(1000) // wait 1/10th of a second before running again // todo adjust as needed
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

    private Game implementGameLogic(final Challenge challenge, final Game game) {
        final long startTime = System.currentTimeMillis();

        final Grid grid = challenge.getGrid();

        // check if we can add upgrade any players from 'waiting' to 'queued'
        final List<String> waitingPlayers = game.getWaitingPlayers();
        for(final String playerEmail : waitingPlayers) {
            final String code = (String) memcacheService.get(SubmitCodeServlet.getKey(challenge.getId(), playerEmail));
            if(code != null) {
                game.queuePlayer(playerEmail);
            }
        }
//System.out.println("**** after queueing: " + game); // todo

        // now check if we can upgrade any players from 'queued' to 'active'
        while(game.getNumberOfActivePlayers() < challenge.getMaxActivePlayers() && game.hasQueuedPlayers()) {
            // activate players as needed
            game.activateNextPlayer(grid);
        }

// System.out.println("**** after activating: " + game);
// System.out.println("ActivePlayers: " + game.getActivePlayers()); // todo delete

        // prepare active players
        final Map<String,MazeSolver> playerEmailToMazeSolvers = new HashMap<>();
        final List<String> activePlayers = game.getActivePlayers();
        for(final String activePlayerEmail : activePlayers) {
            final String code = (String) memcacheService.get(SubmitCodeServlet.getKey(challenge.getId(), activePlayerEmail));
            final MazeSolver mazeSolver = new InterpretedMazeSolver(challenge, game, activePlayerEmail, code); // todo
            mazeSolver.init(challenge, game);
            final byte [] state = (byte[]) memcacheService.get(getMazeSolverStateKey(game.getId(), activePlayerEmail));
            mazeSolver.setState(state);
            playerEmailToMazeSolvers.put(activePlayerEmail, mazeSolver);
        }

        // todo ... revise to make multi-threaded and with deadlines?
        // todo check out: com.google.appengine.api.ThreadManager
        RuntimeController.makeMove(grid, game, playerEmailToMazeSolvers);

        // store maze solvers' state to memcache
        for(final String activePlayerEmail : activePlayers) {
            final MazeSolver mazeSolver = playerEmailToMazeSolvers.get(activePlayerEmail);
            memcacheService.put(getMazeSolverStateKey(game.getId(), activePlayerEmail), mazeSolver.getState());
        }

        // remove completed players (move from 'active' back to 'waiting')
        // todo
        final Position targetPosition = challenge.getGrid().getTargetPosition();
        for(final String activePlayerEmail : activePlayers) {
            final Position playerPosition = game.getPosition(activePlayerEmail);
            if(playerPosition.equals(targetPosition)) {
                memcacheService.delete(getMazeSolverStateKey(game.getId(), activePlayerEmail));
                game.resetPlayer(activePlayerEmail);
            }
        }


        // update game with number of rounds executed
        game.touch(System.currentTimeMillis() - startTime);

        return game;
    }

    private static String getGameKey(final long challengeId) {
        return KEY_CACHED_GAME.replace("%", Long.toString(challengeId));
    }

    private static String getMazeSolverStateKey(final long gameId, final String playerEmail) {
        return KEY_CACHED_MAZE_SOLVER_STATE.replace("%1", Long.toString(gameId)).replace("%2", playerEmail);
    }
}
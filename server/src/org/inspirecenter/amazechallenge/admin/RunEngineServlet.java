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

    private static final long ONE_SECOND = 1000L;

    private static final String KEY_CACHED_GAME = "cached-game-state-%";
    public static final String KEY_CACHED_MAZE_SOLVER_STATE = "cached-maze-solver-state-%1-player-%2";
    public static final String KEY_CACHED_LEADER_BOARD = "cached-leader-board-%";

    private java.util.logging.Logger log = java.util.logging.Logger.getAnonymousLogger();

    private final Gson gson = new Gson();

    // update values in memcache
    private final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("Running 'RunEngineServlet'");

        final Vector<String> errors = new Vector<>();

        final String magic = request.getParameter("magic");
        final String challengeIdAsString = request.getParameter("challenge");
        final String gameIdAsString = request.getParameter("game");

        GameFullState gameFullState = null;

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else if(gameIdAsString == null || gameIdAsString.isEmpty() || gameIdAsString.equalsIgnoreCase("0")) {
            errors.add("Missing or empty or zero game 'id'");
        } else {
            try {
                final long challengeId = Long.parseLong(challengeIdAsString);
                final long gameId = Long.parseLong(gameIdAsString);

                final Challenge challenge = ofy().load().key(Key.create(Challenge.class, challengeId)).now();

                final Game game = ofy().transact(() -> {

                    final Game initialGame = ofy().load().key(Key.create(Game.class, gameId)).now();

                    if(initialGame == null) {
                        log.severe("Initial Game not set so quitting the RunEngineServlet");
                        return null;
                    } else {
                        // update game state
                        final Game updatedGame = implementGameLogic(challenge, initialGame);

                        // store new game in data store
                        ofy().save().entity(updatedGame).now();

                        return updatedGame;
                    }
                });

                if(game == null) {
                    log.severe("Game could not be updated so quitting the RunEngineServlet");
                    return; // skip scheduling the next run
                }
                gameFullState = game.getFullState(challenge.getGrid());

                // 1. update game state
                memcacheService.put(getGameKey(challengeId), gameFullState);

                // 2. update leader board
                // todo ... update leader board and save in memcache

                // 3. schedule next run
                final Queue queue = QueueFactory.getDefaultQueue();
                TaskOptions taskOptions = TaskOptions.Builder
                        .withUrl("/admin/run-engine")
                        .param("magic", magic)
                        .param("challenge", Long.toString(challengeId))
                        .param("game", Long.toString(gameId))
                        .countdownMillis(ONE_SECOND)
                        .method(TaskOptions.Method.GET);
                queue.add(taskOptions);

            } catch (NumberFormatException nfe) {
                log.severe("Could not parse parameter 'challenge-id' or 'challenge-instance-id' or 'game-id' (" + nfe.getMessage() + ")");
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
        final List<String> waitingPlayerIDs = game.getWaitingPlayerIDs();
        for(final String waitingPlayerId : waitingPlayerIDs) {
            final String code = (String) memcacheService.get(SubmitCodeServlet.getMazeCodeKey(challenge.getId(), waitingPlayerId));
            if(code != null) {
                game.queuePlayerById(waitingPlayerId);
            }
        }

        // now check if we can upgrade any players from 'queued' to 'active'
        while(game.getNumberOfActivePlayers() < challenge.getMaxActivePlayers() && game.hasQueuedPlayers()) {
            // activate players as needed
            game.activateNextPlayer(grid);
        }

        // prepare active players
        final Map<String,MazeSolver> playerIDsToMazeSolvers = new HashMap<>();
        final List<String> activePlayerIDs = game.getActivePlayerIDs();
        for(final String activePlayerId : activePlayerIDs) {
            final String code = (String) memcacheService.get(SubmitCodeServlet.getMazeCodeKey(challenge.getId(), activePlayerId));
            final MazeSolver mazeSolver = new InterpretedMazeSolver(challenge, game, activePlayerId, code);
            mazeSolver.init(challenge, game);
            final byte [] state = (byte[]) memcacheService.get(getMazeSolverStateKey(game.getId(), activePlayerId));
            mazeSolver.setState(state);
            playerIDsToMazeSolvers.put(activePlayerId, mazeSolver);
        }

        // todo ... revise to make multi-threaded and with deadlines? [e.g. check out: com.google.appengine.api.ThreadManager]
        RuntimeController.makeMove(challenge, game, playerIDsToMazeSolvers);

        // store maze solvers' state to memcache
        for(final String activePlayerId : activePlayerIDs) {
            final MazeSolver mazeSolver = playerIDsToMazeSolvers.get(activePlayerId);
            memcacheService.put(getMazeSolverStateKey(game.getId(), activePlayerId), mazeSolver.getState());
        }

        // remove completed players (move from 'active' back to 'waiting')
        final Position targetPosition = challenge.getGrid().getTargetPosition();
        for(final String activePlayerId : activePlayerIDs) {
            final Position playerPosition = game.getPositionById(activePlayerId);
            // for any players that were moved in 'inactive' status, reset their state and code so they are not restarted automatically
            if(playerPosition.equals(targetPosition)) {
                memcacheService.delete(getMazeSolverStateKey(game.getId(), activePlayerId)); // reset algorithm's state
                memcacheService.delete(SubmitCodeServlet.getMazeCodeKey(challenge.getId(), activePlayerId)); // reset submitted code
                game.resetPlayerById(activePlayerId);
            }
        }

        // update game with number of rounds executed
        game.touch(System.currentTimeMillis() - startTime);

        return game;
    }

    public static String getGameKey(final long challengeId) {
        return getGameKey(Long.toString(challengeId));
    }

    public static String getGameKey(final String challengeIdAsString) {
        return KEY_CACHED_GAME.replace("%", challengeIdAsString);
    }

    private static String getMazeSolverStateKey(final long gameId, final String playerId) {
        return KEY_CACHED_MAZE_SOLVER_STATE.replace("%1", Long.toString(gameId)).replace("%2", playerId);
    }
}
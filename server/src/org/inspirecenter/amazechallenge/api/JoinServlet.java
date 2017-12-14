package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.inspirecenter.amazechallenge.api.Common.checkMagic;
import static org.inspirecenter.amazechallenge.api.Common.isValidEmailAddress;

public class JoinServlet extends HttpServlet {

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String name = request.getParameter("name");
        final String email = request.getParameter("email");
        final String colorName = request.getParameter("color");
        final String iconName = request.getParameter("icon");
        final String shapeCode = request.getParameter("shape");
        final String challengeIdAsString = request.getParameter("id");

        final Vector<String> errors = new Vector<>();

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(email == null || email.isEmpty()) {
            errors.add("Missing or empty 'email' parameter");
        } else if(!isValidEmailAddress(email)) {
            errors.add("Invalid 'email' parameter - must be a valid email: " + email);
        } else if(name == null || name.isEmpty()) {
            errors.add("Missing or empty 'name' parameter");
        } else if(colorName == null || colorName.isEmpty()) {
            errors.add("Missing or empty 'color' parameter");
        } else if(iconName == null || iconName.isEmpty()) {
            errors.add("Missing or empty 'icon' parameter");
        } else if(shapeCode == null || shapeCode.isEmpty()) {
            errors.add("Missing or empty 'shape' parameter");
        } else if(challengeIdAsString == null || challengeIdAsString.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else {
            try {
                final long challengeId = Long.parseLong(challengeIdAsString);
                final Challenge challenge = ObjectifyService.ofy().load().type(Challenge.class).id(challengeId).now();
                if(challenge == null) {
                    errors.add("Invalid or unknown challenge for id: " + challengeId);
                } else {
                    final long now = System.currentTimeMillis();
                    if(now < challenge.getStartTimestamp()) {
                        errors.add("Challenge has not started yet. It starts on: " + new Date(challenge.getStartTimestamp()));
                    } else if(now > challenge.getEndTimestamp()) {
                        errors.add("Challenge has ended on: " + new Date(challenge.getEndTimestamp()));
                    } else {
                        final AmazeColor playerColor = AmazeColor.getByName(colorName);
                        final AmazeIcon playerIcon = AmazeIcon.getByName(iconName);
                        final Shape playerShape = Shape.getShapeByCode(shapeCode);

                        final long gameId;

                        {
                            Game game = ofy()
                                    .load()
                                    .type(Game.class)
                                    .filter("challengeId=", challengeId)
                                    .first()
                                    .now();
                            gameId = game == null ? 0L : game.getId();
                        }


                        // handle the addition of a new player in a transaction to ensure atomicity
                        final Game game = ofy().transact(() -> {
                            // add binding of user to game
                            final Game tGame = gameId == 0 ?
                                    new Game(challengeId) :
                                    ofy().load().key(Key.create(Game.class, gameId)).now();

                            // modify
                            tGame.addPlayer(new Player(email, name, playerColor, playerIcon, playerShape));

                            // save
                            ofy().save().entity(tGame).now();
                            return tGame;
                        });

                        // trigger processing of game state
                        final Queue queue = QueueFactory.getDefaultQueue();
                        TaskOptions taskOptions = TaskOptions.Builder
                                .withUrl("/admin/run-engine")
                                .param("magic", magic)
                                .param("challenge-id", Long.toString(challengeId))
                                .param("game-id", Long.toString(game.getId()))
                                .countdownMillis(1000) // wait 1 second before the call
                                .method(TaskOptions.Method.GET);
                        queue.add(taskOptions);
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
}
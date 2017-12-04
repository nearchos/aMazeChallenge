package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.inspirecenter.amazechallenge.data.*;
import org.inspirecenter.amazechallenge.model.AmazeColor;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Shape;

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String name = request.getParameter("name");
        final String email = request.getParameter("email");
        final String colorName = request.getParameter("color");
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
                        final Shape playerShape = Shape.getShapeByCode(shapeCode);

                        ChallengeInstance ci = ofy().load()
                                .type(ChallengeInstance.class)
                                .filter("challengeId = ", challengeId)
                                .first().now();
                        long id = ci == null ? 0L : ci.id;

                        // handle the addition of a new player in a transaction to ensure atomicity
                        ofy().transact(new VoidWork() {
                            public void vrun() {
                                // add binding of user to challenge instance
                                ChallengeInstance challengeInstance = id == 0 ?
                                        new ChallengeInstance(challengeId) :
                                        ofy().load().key(Key.create(ChallengeInstance.class, id)).now();

                                // modify
                                challengeInstance.addPlayer(email, name, playerColor, playerShape);

                                // save
                                ofy().save().entity(challengeInstance).now();
                            }
                        });

                        final ChallengeInstance challengeInstance = ofy().load().type(ChallengeInstance.class).filter("challengeId =", challengeId).first().now();
                        final long challengeInstanceId = challengeInstance == null ? 0L : challengeInstance.id;
                        final Game game = ofy().load().type(Game.class).filter("challengeId =", challengeId).first().now();
                        final long gameId = game == null ? 0L : game.id;

                        // trigger processing of game state
                        final Queue queue = QueueFactory.getDefaultQueue();
                        TaskOptions taskOptions = TaskOptions.Builder
                                .withUrl("/admin/run-engine")
                                .param("magic", magic)
                                .param("challenge-id", Long.toString(challengeId))
                                .param("challenge-instance-id", Long.toString(challengeInstanceId))
                                .param("game-id", Long.toString(gameId))
//                                .countdownMillis(90000) // wait 90 seconds before the poll
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
            reply = ReplyBuilder.createReply();
        } else {
            reply = ReplyBuilder.createReplyWithErrors(errors);
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}
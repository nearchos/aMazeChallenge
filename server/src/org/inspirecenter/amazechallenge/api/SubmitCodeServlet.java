package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.inspirecenter.amazechallenge.data.ChallengeInstance;
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

import static com.googlecode.objectify.ObjectifyService.ofy;

public class SubmitCodeServlet extends HttpServlet {

    private final Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String challengeIdAsString = request.getParameter("id");

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
        } else if(email == null || email.isEmpty()) {
            errors.add("Missing or empty 'email' parameter");
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

                    final ChallengeInstance challengeInstance = ofy().load()
                            .type(ChallengeInstance.class)
                            .filter("challengeId = ", challengeId)
                            .first().now();
                    final long challengeInstanceId = challengeInstance == null ? 0L : challengeInstance.id;

                    // handle the addition of a new player in a transaction to ensure atomicity
                    ofy().transact(new VoidWork() {
                        public void vrun() {
                            // add binding of user to challenge instance
                            ChallengeInstance challengeInstance = ofy().load()
                                    .key(Key.create(ChallengeInstance.class, challengeInstanceId))
                                    .now();

                            // modify
                            if(challengeInstance == null) {
                                errors.add("Invalid or missing challenge instance for id: " + challengeId);
                            } else {
                                if(!challengeInstance.containsPlayer(email)) {
                                    errors.add("Player not found in challenge instance for email: " + email);
                                } else {
                                    challengeInstance.submitCode(email, code);
                                }
                            }

                            // save
                            ObjectifyService.ofy().save().entity(challengeInstance).now();
                        }
                    });

                    final Game game = ofy().load().type(Game.class).filter("challengeId =", challengeId).first().now();
                    final long gameId = game == null ? 0L : game.id;

                    // trigger processing of game state
                    if(gameId != 0L) {
                        final Queue queue = QueueFactory.getDefaultQueue();
                        TaskOptions taskOptions = TaskOptions.Builder
                                .withUrl("/admin/run-engine")
                                .param("magic", magic)
                                .param("challenge-id", Long.toString(challengeId))
                                .param("challenge-instance-id", Long.toString(challengeInstanceId))
                                .param("game-id", Long.toString(gameId))
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
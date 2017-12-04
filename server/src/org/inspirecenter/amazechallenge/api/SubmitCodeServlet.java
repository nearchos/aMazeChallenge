package org.inspirecenter.amazechallenge.api;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.inspirecenter.amazechallenge.data.ChallengeInstance;
import org.inspirecenter.amazechallenge.model.Challenge;

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
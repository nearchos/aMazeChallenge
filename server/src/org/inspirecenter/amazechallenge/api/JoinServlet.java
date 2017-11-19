package org.inspirecenter.amazechallenge.api;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;

public class JoinServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String name = request.getParameter("name");
        final String colorName = request.getParameter("color");
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
                        // add binding of user to challenge instance
                        ChallengeInstance challengeInstance = ObjectifyService.ofy().load()
                                .type(ChallengeInstance.class)
                                .filter("challengeId = ", challengeId)
                                .first().now();

                        if(challengeInstance == null) {
                            challengeInstance = new ChallengeInstance(challengeId);
                        }

                        final AmazeColor playerColor = AmazeColor.getByName(colorName);
                        final Shape playerShape = Shape.TRIANGLE; // todo
                        challengeInstance.addPlayer(email, name, playerColor, playerShape);
                        ObjectifyService.ofy().save().entity(challengeInstance).now();
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

    private static boolean checkMagic(final String magic) {
        final Parameter parameter = ObjectifyService.ofy().load().type(Parameter.class).filter("name", "magic").first().now();
        return parameter != null && parameter.value.equals(magic);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
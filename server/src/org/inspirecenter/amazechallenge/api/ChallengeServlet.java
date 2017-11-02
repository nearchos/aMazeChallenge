package org.inspirecenter.amazechallenge.api;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Challenge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

public class ChallengeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String idAsString = request.getParameter("challenge-id");

        final Vector<String> errors = new Vector<>();
        String json = null;

        if(idAsString == null || idAsString.trim().isEmpty()) {
            errors.add("missing or empty challenge-id");
        } else {
            try {
                final long id = Long.parseLong(idAsString);
                final Challenge challenge = ObjectifyService.ofy()
                        .load()
                        .type(Challenge.class)
                        .id(id)
                        .now();

                if(challenge == null) { // id not found
                    errors.add("specified id not found (" + id + ")");
                } else {
                    json = challenge.toJson();
                }
            } catch (NumberFormatException nfe) {
                errors.add("invalid challenge-id (" + nfe.getMessage() + ")");
            }
        }

        final String reply;
        if(errors.isEmpty()) {
            reply = ReplyBuilder.createReplyWithOneParameter("challenge", json);
        } else {
            reply = ReplyBuilder.createReplyWithError(errors);
        }

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);

    }
}
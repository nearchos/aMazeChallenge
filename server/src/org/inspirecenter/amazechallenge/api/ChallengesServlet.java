package org.inspirecenter.amazechallenge.api;

import com.google.gson.Gson;
import org.inspirecenter.amazechallenge.model.Challenge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ChallengesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final List<Challenge> allChallenges = ofy()
                .load()
                .type(Challenge.class)
                .order("name")
                .list();

        final StringBuilder reply = new StringBuilder();
        // todo use gson
        reply.append(
                "{" +
                "  \"status\": \"ok\"," +
                "  \"challenges\": [");

        final Gson gson = new Gson();
        for(int i = 0; i < allChallenges.size(); i++) {
            final Challenge challenge = allChallenges.get(i);
            final String json = gson.toJson(challenge);
            reply.append(json).append(i < allChallenges.size() - 1 ? ", " : " ");
        }

        reply.append(
                "  ]" +
                "}");

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}

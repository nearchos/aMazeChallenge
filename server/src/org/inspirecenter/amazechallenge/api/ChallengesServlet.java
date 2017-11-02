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

public class ChallengesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final List<Challenge> allChallenges = ObjectifyService.ofy()
                .load()
                .type(Challenge.class)
                .order("name")
                .list();

        final StringBuilder reply = new StringBuilder();
        reply.append(
                "{" +
                "  \"status\": \"ok\"," +
                "  \"challenges\": [");

        for(int i = 0; i < allChallenges.size(); i++) {
            final Challenge challenge = allChallenges.get(i);
            reply.append(challenge.toJsonSummary()).append(i < allChallenges.size() - 1 ? "," : "");
        }

        reply.append(
                "  ]" +
                "}");

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}

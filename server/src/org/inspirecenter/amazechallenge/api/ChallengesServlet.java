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
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ChallengesServlet extends HttpServlet {

    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final List<Challenge> allChallenges = ofy()
                .load()
                .type(Challenge.class)
                .list();

        final String reply = gson.toJson(new ChallengesReply(allChallenges));

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(reply);
    }
}
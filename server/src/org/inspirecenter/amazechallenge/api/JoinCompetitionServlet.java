package org.inspirecenter.amazechallenge.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JoinCompetitionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String challengeId = request.getParameter("challengeId");

        // todo

        response.getWriter().println("{ \"status\": \"OK\" } ");
    }
}

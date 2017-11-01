package org.inspirecenter.amazechallenge.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ChallengesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final PrintWriter printWriter = response.getWriter();
        printWriter.println(
                "{" +
                "  \"challenges\": [" +
                "    {" +
                "    }" +
                "  ]" +
                "}");
    }
}

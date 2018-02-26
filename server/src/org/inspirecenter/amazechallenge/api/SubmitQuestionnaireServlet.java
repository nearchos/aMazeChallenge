package org.inspirecenter.amazechallenge.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.inspirecenter.amazechallenge.model.questionnaire.QuestionnaireEntry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class SubmitQuestionnaireServlet extends HttpServlet {

    private Logger log = Logger.getLogger(getClass().getName());

    private final Gson gson = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String magic = request.getParameter("magic");
        final BufferedReader bufferedReader = request.getReader();
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        final String json = stringBuilder.toString();

        final Vector<String> errors = new Vector<>();

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!Common.checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(json.isEmpty()) {
            errors.add("Empty 'json' payload");
        } else {
            try {
                log.info("Received JSON: " + json);
                final QuestionnaireEntry questionnaireEntry = gson.fromJson(json, QuestionnaireEntry.class);

                // save entity to datastore
                ofy().save().entity(questionnaireEntry).now();

            } catch (JsonSyntaxException jsone) {
                log.severe("Error while converting string to JSON: " + jsone.getMessage());
                errors.add("JSON parsing error: " + jsone.getMessage());
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

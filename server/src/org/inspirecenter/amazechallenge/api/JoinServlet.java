package org.inspirecenter.amazechallenge.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Challenge;
import org.inspirecenter.amazechallenge.data.Parameter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class JoinServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String magic = request.getParameter("magic");
        final String email = request.getParameter("email");
        final String challengeId = request.getParameter("id");

        final Vector<String> errors = new Vector<>();

        if(magic == null || magic.isEmpty()) {
            errors.add("Missing or empty 'magic' parameter");
        } else if(!checkMagic(magic)) {
            errors.add("Invalid 'magic' parameter");
        } else if(email == null || email.isEmpty()) {
            errors.add("Missing or empty 'email' parameter");
        } else if(!isValidEmailAddress(email)) {
            errors.add("Invalid 'email' parameter - must be a valid email: " + email);
        } else if(challengeId == null || challengeId.isEmpty()) {
            errors.add("Missing or empty challenge 'id'");
        } else {
            try {
                final long id = Long.parseLong(challengeId);
                final Challenge challenge = ObjectifyService.ofy().load().type(Challenge.class).id(id).now();
                // todo add binding of user to challenge instance
            } catch (NumberFormatException nfe) {
                errors.add(nfe.getMessage());
            }
        }

        final String reply = ReplyBuilder.createReplyWithErrors(errors);

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
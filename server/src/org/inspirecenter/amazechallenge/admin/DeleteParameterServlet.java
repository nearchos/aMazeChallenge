package org.inspirecenter.amazechallenge.admin;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Parameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteParameterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // you can assume admin access at this point

        String error = "";

        final String idAsString = request.getParameter("id");
        if(idAsString == null || idAsString.trim().isEmpty()) {
            error += "Invalid or missing 'id' argument.\n";
        } else {
            try {
                final long id = Long.parseLong(idAsString);
                ObjectifyService.ofy().delete().type(Parameter.class).id(id).now();
            } catch (NumberFormatException nfe) {
                error += "Invalid number format (" + nfe.getMessage() + ")";
            }
        }

        if(error.isEmpty()) {
            response.sendRedirect("/admin/parameters");
        } else {
            response.sendRedirect("/admin/parameters?error=" + error);
        }
    }
}

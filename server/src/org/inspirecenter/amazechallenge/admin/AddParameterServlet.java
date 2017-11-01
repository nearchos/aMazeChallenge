package org.inspirecenter.amazechallenge.admin;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Parameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddParameterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // you can assume admin access at this point

        String error = "";

        final String name = request.getParameter("name");
        final String value = request.getParameter("value");

        if(name == null || name.trim().isEmpty()) {
            error += "Invalid or missing 'name' argument.\n";
        }
        if(value == null || value.trim().isEmpty()) {
            error += "Invalid or missing 'value' argument.\n";
        }

        if(name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
            final Parameter parameter = new Parameter(name, value);
            ObjectifyService.ofy().save().entity(parameter).now();
            response.sendRedirect("/admin/parameters" );
        } else {
            response.sendRedirect("/admin/parameters?error=" + error);
        }
    }
}

package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.generator.MazeGenerator;
import org.inspirecenter.amazechallenge.model.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class AddChallengeServlet extends HttpServlet {

    private Logger log = Logger.getAnonymousLogger();

    public static final int API_VERSION = 1;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error = "";

        final String name = request.getParameter("name");
        final String description= request.getParameter("description");
        final String gridWidth = request.getParameter("width");
        final String gridHeight = request.getParameter("height");
        final String gridAsHex = request.getParameter("grid");
        final String startingPositionX = request.getParameter("startingPositionX");
        final String startingPositionY = request.getParameter("startingPositionY");
        final String targetPositionX = request.getParameter("targetPositionX");
        final String targetPositionY = request.getParameter("targetPositionY");

        if(!UserServiceFactory.getUserService().isUserLoggedIn()) {
            error += "User must be logged in.";
        } else if(!UserServiceFactory.getUserService().isUserAdmin()) {
            error += "The logged in user must be an admin.";
        } else if(name == null || name.trim().isEmpty()) {
            error += "Invalid or missing 'name' parameter. ";
        } else if(description == null || description.trim().isEmpty()) {
            error += "Invalid or missing 'description' parameter. ";
        } else if(gridWidth == null || gridHeight == null) {
            error += "Invalid or missing 'width' and/or 'height' parameter. ";
        } else if(startingPositionX == null || startingPositionY == null || targetPositionX == null || targetPositionY == null) {
            error += "Invalid or missing 'width' and/or 'height' parameter. ";
        } else if(gridAsHex == null || gridAsHex.trim().isEmpty()) {
            error += "Invalid or missing 'grid' parameter. ";
        } else {
            try {
                final int width = Integer.parseInt(gridWidth);
                final int height = Integer.parseInt(gridHeight);
                final int spX = Integer.parseInt(startingPositionX);
                final int spY = Integer.parseInt(startingPositionY);
                final int tpX = Integer.parseInt(targetPositionX);
                final int tpY = Integer.parseInt(targetPositionY);
                final Grid grid = new Grid(width, height, gridAsHex, spX, spY, tpX, tpY);
                final int apiVersion = API_VERSION;
                // todo customize other fields
                final boolean hasQuestionnaire = false;
                final String maxRewards = PickableIntensity.LOW.name();
                final String maxPenalties = PickableIntensity.LOW.name();
                final String algorithm = MazeGenerator.Algorithm.SINGLE_SOLUTION.getName();
                final String lineColor = AmazeColor.COLOR_BLUE.getName();
                final String difficulty = ChallengeDifficulty.EASY.name();
                final String createdBy = UserServiceFactory.getUserService().getCurrentUser().getEmail();
                final long createdOn = System.currentTimeMillis();
                final String backgroundAudioName = "";
                final String backgroundAudioFormat = "";
                final String backgroundImageName = "texture_grass";
                final BackgroundImage.BackgroundImageType backgroundImageType = BackgroundImage.BackgroundImageType.JPG;

                final Challenge challenge = new Challenge(name, apiVersion, description, true, true, true, 0, 10, 0L,
                        Long.MAX_VALUE, hasQuestionnaire, maxRewards, maxPenalties, algorithm, grid, lineColor, difficulty, createdBy, createdOn,
                        backgroundAudioName, backgroundAudioFormat, backgroundImageName, backgroundImageType);
                ObjectifyService.ofy().save().entity(challenge).now();
            } catch (NumberFormatException nfe) {
                log.severe(request.getParameterMap() + " ... -> " + nfe.getMessage());
                error += "Invalid number format of 'width' or 'height' or 'startingPositionX', 'startingPositionY', 'targetPositionX', 'targetPositionY' (" + nfe.getMessage() + ")";
            }
        }
        response.sendRedirect("/admin/challenges" + (error.isEmpty() ? "" : "?error=" + error));
    }
}

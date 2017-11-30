package org.inspirecenter.amazechallenge.admin;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Challenge;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PrepareEngineServlet extends HttpServlet {

    private static final long TEN_MINUTES = 10L * 60 * 1000;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final long now = System.currentTimeMillis();
        final List<Challenge> upcomingChallenges = ObjectifyService.ofy()
                .load()
                .type(Challenge.class)
                .filter("startTimestamp < ", now - TEN_MINUTES)
                .list();

        final Queue queue = QueueFactory.getDefaultQueue();

        for(final Challenge upcomingChallenge : upcomingChallenges) {
            final long startTime = upcomingChallenge.startTimestamp;
            final long delay = startTime - System.currentTimeMillis();
            TaskOptions taskOptions = TaskOptions.Builder
                    .withUrl("/admin/run-engine")
                    .param("challenge-id", upcomingChallenge.id.toString())
                    .countdownMillis(delay)
                    .method(TaskOptions.Method.GET);
            queue.add(taskOptions);
        }
    }
}

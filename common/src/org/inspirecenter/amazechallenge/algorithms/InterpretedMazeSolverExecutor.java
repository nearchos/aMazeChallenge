package org.inspirecenter.amazechallenge.algorithms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class InterpretedMazeSolverExecutor {

    private static final String TAG = "Executor";
    private final org.mozilla.javascript.Function FUNCTION;
    private final InterpretedMazeSolver INSTANCE;
    private final int DELAY_S;

    InterpretedMazeSolverExecutor(org.mozilla.javascript.Function function, InterpretedMazeSolver instance, int delay_seconds) {
        this.FUNCTION = function;
        this.INSTANCE = instance;
        this.DELAY_S = delay_seconds;
    }

    PlayerMove execute() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<PlayerMove> future = executor.submit(new InterpretedMazeSolverExecutorTask(FUNCTION, INSTANCE));

        try {
            future.get(DELAY_S, TimeUnit.SECONDS);
            executor.shutdownNow();
            return future.get();
        }
        catch (TimeoutException e) {
            future.cancel(true);
            executor.shutdownNow();
            return PlayerMove.NO_MOVE;
        }

    }

}
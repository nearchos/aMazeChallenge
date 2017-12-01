package org.inspirecenter.amazechallenge.algorithms;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import java.util.concurrent.Callable;

class InterpretedMazeSolverExecutorTask implements Callable<PlayerMove> {

    private final Function FUNCTION;
    private final InterpretedMazeSolver INSTANCE;

    InterpretedMazeSolverExecutorTask(final Function function, final InterpretedMazeSolver instance) {
        this.FUNCTION = function;
        this.INSTANCE = instance;
    }//end InterpretedMazeSolverExecutorTask()

    @Override
    public PlayerMove call() throws Exception {
        Object result = FUNCTION.call(Context.enter(), Context.enter().initStandardObjects(), Context.enter().initStandardObjects(), new Object[] { INSTANCE });
        return (PlayerMove) Context.jsToJava(result, PlayerMove.class);
    }//end call()

}//end class InterpretedMazeSolverExecutorTask
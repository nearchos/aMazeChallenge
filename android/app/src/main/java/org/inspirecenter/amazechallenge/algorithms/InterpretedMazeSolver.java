package org.inspirecenter.amazechallenge.algorithms;

import android.util.Log;

import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.interpreter.MazeInterpreter;
import org.mozilla.javascript.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";

    public static final String PARAMETER_KEY_CODE = "code";
    private String code;
    private static boolean justTurned = false;

    private static Context RHINO;

    public InterpretedMazeSolver(final Game game, final Player player) {
        super(game, player);
        RHINO = Context.enter();
        RHINO.setOptimizationLevel(-1);
    }

    public void setParameter(final String name, final Serializable value) {
        if(PARAMETER_KEY_CODE.equals(name)) {
            code = wrapCode((String) value);
        }
    }

    @Override
    public PlayerMove getNextMove() {
        Log.i("Wrapped Code: ", code);

        PlayerMove nextMove;

        Object instanceAsObject = this;

        try {
            InterpretedMazeRunnerParams returnedParams  = (InterpretedMazeRunnerParams) MazeInterpreter.callFunction(code, "wrapper", InterpretedMazeRunnerParams.class, new Object[] { instanceAsObject, justTurned });
            nextMove = returnedParams.getMove();
            justTurned = returnedParams.isJustTurned();
        } catch (Exception e) {
            Log.e("maze", e.getMessage());
            throw new RuntimeException(e);
        }

        /*
         * Blockly if block error --> The "else if" and "else" statements are not generated!
         * Fixed! (https://github.com/google/blockly-android/issues/666)
         */

        Log.i("Current Move: ", nextMove == null ? "null" : nextMove.toString());
        return nextMove == null ? PlayerMove.NO_MOVE : nextMove;
        //return PlayerMove.NO_MOVE;
    }

    @Override
    public String toString() {
        return "Interpreted Maze";
    }

    private Map<String,Object> javascriptArguments = new HashMap<>();

    public  Object getJavascriptArgument(final String key) {
        return javascriptArguments.get(key);
    }

    public  Object setJavascriptArgument(final String key, final Object value) {
        return javascriptArguments.put(key, value);
    }

    private static String wrapCode(String code) {
        String turned =
                "var justTurned = false;\n function getJustTurned() { return justTurned; }\n";
        String wrapperStart =
                "function wrapper(instance, justTurnedInput) {\n" +
                "  justTurned = justTurnedInput;\n"; //Initialize justTurned to saved value.
        String wrapperEnd =
                "\nreturn Packages.PlayerMove.NO_MOVE\n}"; //Return no move if the player's code does not return.
        return turned + wrapperStart + code + wrapperEnd;
    }
}
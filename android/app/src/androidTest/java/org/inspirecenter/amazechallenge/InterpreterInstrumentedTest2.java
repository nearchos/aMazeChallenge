package org.inspirecenter.amazechallenge;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.algorithms.PlayerMove;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Maze;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;
import org.inspirecenter.amazechallenge.model.ShapeColor;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 * Tests if the JS parsing and execution works as intended (especially whether locally-defined
 * variables keep their state).
 */
@RunWith(AndroidJUnit4.class)

public class InterpreterInstrumentedTest2 {

    private static final String TAG = "aMazeChallenge:a-test";

    private static final String SAMPLE_CODE_2 =
                    "var justTurned;\n" +
                    "\n" +
                    "function init(instance) {\n" +
                    "   justTurned = false;\n" +
                    "   propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" +
                    "}//end init()\n" +
                    "\n" +
                    "function wrapper(instance) {\n" +
                    "   propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );\n" +
                    "   \n" +
                    "   //---- PLAYER'S CODE ----\n" +
                    "   \n" +
                    "   if (justTurned) {\n" +
                    "       justTurned = false;\n" +
                    "       retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD;" +
                    "       propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );" +
                    "       return results;\n" +
                    "   }\n" +
                    "   if (instance.canMoveLeft()) {\n" +
                    "       justTurned = true;\n" +
                    "       retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_COUNTERCLOCKWISE;" +
                    "       propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); });" +
                    "       return results;\n" +
                    "   } else if (instance.canMoveForward()) {\n" +
                    "       retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD;" +
                    "       propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );" +
                    "       return results;\n" +
                    "   } else {\n" +
                    "       justTurned = true;\n" +
                    "       retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE;" +
                    "       propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );" +
                    "       return results;\n" +
                    "   }\n" +
                    "   \n" +
                    "   //-----------------------\n" +
                    "   \n" +
                    "   propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" +
                    "}";

    @Test
    public void interpretation_isCorrect() throws Exception {

        Log.i(TAG, SAMPLE_CODE_2);

        final Context context = InstrumentationRegistry.getTargetContext();

        final Player player = new Player("test", ShapeColor.PLAYER_COLOR_BLACK, Shape.CIRCLE, InterpretedMazeSolver.class);
        final Maze maze = new Maze(context.getAssets().open("challenges/grid0.txt"));
        final Game game = new Game(maze);
        final InterpretedMazeSolver interpretedMazeSolver = new InterpretedMazeSolver(game, player);
        interpretedMazeSolver.setParameter(InterpretedMazeSolver.PARAMETER_KEY_CODE, SAMPLE_CODE_2);

        {
            final PlayerMove playerMove = interpretedMazeSolver.getNextMove();
            Log.i(TAG, "playerMove: " + playerMove);
        }

        {
            final PlayerMove playerMove = interpretedMazeSolver.getNextMove();
            Log.i(TAG, "playerMove: " + playerMove);
        }

        assertTrue(true);
    }
}

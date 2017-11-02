package org.inspirecenter.amazechallenge;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.inspirecenter.amazechallenge.algorithms.InterpretedMazeSolver;
import org.inspirecenter.amazechallenge.model.Challenge;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Grid;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.model.Shape;
import org.inspirecenter.amazechallenge.model.ShapeColor;
import org.inspirecenter.amazechallenge.ui.TrainingActivity;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 * Tests if the JS parsing and execution works as intended (especially whether locally-defined
 * variables keep their state).
 */
@RunWith(AndroidJUnit4.class)

public class InterpreterInstrumentedTest {

    private static final String TAG = "aMazeChallenge:a-test";

    private static final String SAMPLE_CODE_1 =
                    "var test = false;\n" +
                    "var retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.NO_MOVE;\n" +
                    "var propNames = [];\n" +
                    "for(var propName in this) {\n" +
                    "  if (typeof this[propName] != 'function' && this[propName] != 'propName' && !(this[propName] instanceof Array)) {\n" + // ignore the 'propName' and 'propNames' fields
                    "    propNames.push(propName);\n" +
                    "  }\n" +
                    "}\n" +
                    "function wrapper(instance) {\n" +
                    "  propNames.forEach(function(item, index) { this[item] = instance.getJavascriptArgument(item); } );\n" + // stateful arguments are restored here
                    "  test = !test;\n" +
                    "  if (test) {\n" +
                    "    retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.MOVE_FORWARD;" +
                    "  } else {\n" +
                    "    retVal = Packages.org.inspirecenter.amazechallenge.algorithms.PlayerMove.TURN_CLOCKWISE;" +
                    "  }\n" +
                    "\n" +
                    "  var results = new Object();\n" + // use object as map - populate map with field names and values
                    "  propNames.forEach(function(item, index) { instance.setJavascriptArgument(item, this[item]); } );\n" + // stateful arguments are stored/returned here
                    "  return results;\n" +
                    "}";

    @Test
    public void interpretation_isCorrect() throws Exception {

        Log.i(TAG, SAMPLE_CODE_1);

        final Context context = InstrumentationRegistry.getTargetContext();

        final String json = TrainingActivity.convertStreamToString(context.getAssets().open("challenges/challenge0.json"));
        final Challenge challenge = Challenge.parseJSON(new JSONObject(json));

        final Player player = new Player("test", ShapeColor.PLAYER_COLOR_BLACK, Shape.CIRCLE, InterpretedMazeSolver.class);
        final Grid grid = challenge.getGrid();
        final Game game = new Game(grid);
        final InterpretedMazeSolver interpretedMazeSolver = new InterpretedMazeSolver(game, player);
        interpretedMazeSolver.setParameter(InterpretedMazeSolver.PARAMETER_KEY_CODE, SAMPLE_CODE_1);

        org.mozilla.javascript.Context RHINO = org.mozilla.javascript.Context.enter();
        RHINO.setOptimizationLevel(-1);
        ScriptableObject scope = RHINO.initStandardObjects();
        RHINO.evaluateString(scope, SAMPLE_CODE_1, "wrapper", 1, null);
        Function function = (Function) scope.get("wrapper", scope);

        boolean test1;
        {
            final Object o = function.call(RHINO, scope, scope, new Object[] { interpretedMazeSolver });

            Log.i(TAG, "returned object: " + o + " (type: " + o.getClass() + ")");
            if(o instanceof org.mozilla.javascript.NativeObject) {
                final org.mozilla.javascript.NativeObject no = (NativeObject) o;
                Log.i(TAG, "no: " + no.keySet());
                Log.i(TAG, "no: " + no.values());
            }
            test1 = (Boolean) interpretedMazeSolver.getJavascriptArgument("test");
            Log.i("Value of Test: ", String.valueOf(test1));
        }

        boolean test2;
        {
            final Object o = function.call(RHINO, scope, scope, new Object[] { interpretedMazeSolver });

            Log.i(TAG, "returned object: " + o + " (type: " + o.getClass() + ")");
            if(o instanceof org.mozilla.javascript.NativeObject) {
                final org.mozilla.javascript.NativeObject no = (NativeObject) o;
                Log.i(TAG, "no: " + no.keySet());
                Log.i(TAG, "no: " + no.values());
            }
            test2 = (Boolean) interpretedMazeSolver.getJavascriptArgument("test");
            Log.i("Value of Test: ", String.valueOf(test2));
        }


        assertNotEquals(test1, test2);
    }
}

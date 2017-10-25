package org.inspirecenter.amazechallenge.algorithms;

import android.util.Log;
import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.Player;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";
    public static final String PARAMETER_KEY_CODE = "code";
    private String code;

    public InterpretedMazeSolver(final Game game, final Player player) {
        super(game, player);
    }

    public void setParameter(final String name, final Serializable value) {
        if(PARAMETER_KEY_CODE.equals(name)) {
            code = (String) value;

            //DEBUG INFO:
            Log.d(TAG, "####### PRE-WRAPPED CODE #######");
            Log.d(TAG, code);
            Log.d(TAG, "################################");

            //Wrap the code:
            code = wrapCode(code);

            //DEBUG INFO:
            Log.d(TAG, "########## FINAL CODE ##########");
            Log.d(TAG, code);
            Log.d(TAG, "################################");

            //Call the Initialization function
            try {
                Log.d(TAG, "Running init...");
                Log.d(TAG, " ** javascriptArguments before init: " + javascriptArguments);

                Context RHINO = Context.enter();
                RHINO.setOptimizationLevel(-1);
                ScriptableObject scope = RHINO.initStandardObjects();
                RHINO.evaluateString(scope, code, "init", 1, null);
                Function function = (Function) scope.get("init", scope);
                function.call(RHINO, scope, scope, new Object[] { this });

                Log.d(TAG, " ** javascriptArguments after init: " + javascriptArguments);
                Log.d(TAG, "Done Running init...");
            }
            catch (Exception e) { e.printStackTrace(); }
            finally { Context.exit(); }

            Log.d(TAG, " ** After init: " + javascriptArguments);
        }
    }//end setParameter()

    @Override
    public PlayerMove getNextMove() {

        PlayerMove nextMove = PlayerMove.NO_MOVE;

        Log.d(TAG, " ** Before wrapper: " + javascriptArguments);

        // todo set a deadline (e.. 1 sec) and if no return from RHINO, stop it and return NO_MOVE
        //Call the Wrapper function (run)
        try {
            Context RHINO = Context.enter();
            RHINO.setOptimizationLevel(-1);
            ScriptableObject scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, code, "wrapper", 1, null);
            Function function = (Function) scope.get("wrapper", scope);
            Object result = function.call(RHINO, scope, scope, new Object[] { this });
            nextMove = (PlayerMove) Context.jsToJava(result, PlayerMove.class);
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { Context.exit(); }

        Log.d(TAG, " ** After wrapper: " + javascriptArguments);

        return nextMove;
    }//end getNextMove()

    @Override
    public String toString() { return "Interpreted Maze"; }

    private Map<String,Object> javascriptArguments = new HashMap<>();

    public Object getJavascriptArgument(final String key) {
//        Log.d(TAG, "    *** getJavascriptArgument GET: " + key + " -> " + javascriptArguments.get(key) + ", value type: " + (javascriptArguments.get(key) != null ? javascriptArguments.get(key).getClass() :  "null"));
        return javascriptArguments.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
//        Log.d(TAG, "    *** getJavascriptArgument SET: " + key + " -> " + value + " of type: [" + value.getClass() + "] (value was : " + javascriptArguments.get(key) + ")");
        return javascriptArguments.put(key, value);
    }

    private static String wrapCode(final String code) {
        final int declarationsEnd = getPlayerCodeStart(code);
        final String variableStoring = "\n\n\n//Params Array\n" +
                "var propNames = [];\n" +
                "for(var propName in this) {\n" +
                "   if (typeof this[propName] != 'function' && this[propName] != 'propName' && !(this[propName] instanceof Array)) {\n" +
                "      propNames.push(propName);\n" +
                "   }\n" +
                "}\n";
        final String startingCode = code.substring(0, declarationsEnd);
        final String endingCode = code.substring(declarationsEnd, code.length());
        final String finalCode = startingCode + variableStoring + endingCode;
        return finalCode;
    }//end wrapCode()

    /**
     *
     * @param code the JS code automatically generated by Blockly
     * @return Return the index of the character at which the variable declarations stop.
     */
    private static int getPlayerCodeStart(String code) {
        if (code.length() == 0) return 0;
        int totalLineCharacters = 0;
        String [] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("var ")) totalLineCharacters += lines[i].length() + 1;
            else break;
        }//end for all lines
        return totalLineCharacters - 1; //Return index, not length
    }//end getPlayerCodeStart()
}
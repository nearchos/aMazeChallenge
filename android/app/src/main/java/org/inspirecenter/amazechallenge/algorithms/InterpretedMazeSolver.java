package org.inspirecenter.amazechallenge.algorithms;

import android.util.Log;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams;
import org.inspirecenter.amazechallenge.model.Player;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";
    public static final String PARAMETER_KEY_CODE = "code";
    private String code;
    private InterpretedMazeSolver instance;

    public InterpretedMazeSolver(final Game game, final Player player) {
        super(game, player);
        instance = this;
    }

    public void setParameter(final String name, final Serializable value) {
        if(PARAMETER_KEY_CODE.equals(name)) {
            code = (String) value;

            //DBEUG INFO:
            Log.i(TAG, "####### PRE-WRAPPED CODE #######");
            Log.i(TAG, code);
            Log.i(TAG, "##########################");

            //Wrap the code:
            code = wrapCode(code);

            //DBEUG INFO:
            Log.i(TAG, "####### FINAL CODE #######");
            Log.i(TAG, code);
            Log.i(TAG, "##########################");

            try {
                Log.d(TAG, "Running init...");
                Log.d(TAG, " ** javascriptArguments before init: " + javascriptArguments);

                Context RHINO = Context.enter();
                RHINO.setOptimizationLevel(-1);
                ScriptableObject scope = RHINO.initStandardObjects();
                RHINO.evaluateString(scope, code, "init", 1, null);
                Function function = (Function)scope.get("init", scope);
                function.call(RHINO, scope, scope, new Object[] { instance });

                Log.d(TAG, " ** javascriptArguments after init: " + javascriptArguments);
                Log.d(TAG, "Done Running init...");
            }
            catch (Exception e) { e.printStackTrace(); }
            finally { Context.exit(); }

            Log.d(TAG, " ** After init: " + javascriptArguments);
        }
    }

    @Override
    public PlayerMove getNextMove() {

        PlayerMove nextMove = null;

        Log.d(TAG, " ** Before wrapper: " + javascriptArguments);

        try {
            Context RHINO = Context.enter();
            RHINO.setOptimizationLevel(-1);
            ScriptableObject scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, code, "wrapper", 1, null);
            Function function = (Function) scope.get("wrapper", scope);
            Object result = function.call(RHINO, scope, scope, new Object[] { instance });
            nextMove = (PlayerMove) Context.jsToJava(result, PlayerMove.class);
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { Context.exit(); }
        Log.d(TAG, " ** After wrapper: " + javascriptArguments);

        return nextMove == null ? PlayerMove.NO_MOVE : nextMove;
    }//end getNextMove()

    @Override
    public String toString() { return "Interpreted Maze"; }

    public Map<String,Object> javascriptArguments = new HashMap<>();
    public Map<String, Class> javascriptArgumentTypes = new HashMap<>();

    public Object getJavascriptArgument(final String key) {
        Log.d(TAG, "    *** getJavascriptArgument GET: " + key + " -> " + javascriptArguments.get(key) + ", value type: " + (javascriptArguments.get(key) != null ? javascriptArguments.get(key).getClass() :  "null"));
        return javascriptArguments.get(key);
    }

    public Class getJavascriptArgumentType(final String key) {
        return javascriptArgumentTypes.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
        Log.d(TAG, "    *** getJavascriptArgument SET: " + key + " -> " + value + " of type: [" + value.getClass() + "] (value was : " + javascriptArguments.get(key) + ")");
        javascriptArgumentTypes.put(key, value.getClass());
        return javascriptArguments.put(key, value);
    }

    private static String wrapCode(final String code) {
        final int DECL_END = getPlayerCodeStart(code);
        final String PROPNAMES = "\n\n\n//Params Array\n" +
                "var propNames = [];\n" +
                "for(var propName in this) {\n" +
                "   if (typeof this[propName] != 'function' && this[propName] != 'propName' && !(this[propName] instanceof Array)) {\n" +
                "      propNames.push(propName);\n" +
                "   }\n" +
                "}\n";
        final String START_CODE = code.substring(0, DECL_END);
        final String END_CODE = code.substring(DECL_END, code.length());
        final String FINAL_CODE = START_CODE + PROPNAMES + END_CODE;
        return FINAL_CODE;
    }//end wrapCode()

    /**
     *
     * @param code
     * @return Return the index of the character at which the variable declarations stop.
     */
    public static int getPlayerCodeStart(String code) {
        if (code.length() == 0) return 0;
        int totalLineCharacters = 0;
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("var ")) {
                totalLineCharacters += lines[i].length() + 1; //Also include the removed \n for each line.
            }//end if line starts with "var "
            else break;
        }//end for all lines
        return totalLineCharacters - 1; //Return index, not length
    }//end getPlayerCodeStart()

}
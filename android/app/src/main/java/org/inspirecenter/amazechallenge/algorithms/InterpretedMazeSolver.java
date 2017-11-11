package org.inspirecenter.amazechallenge.algorithms;

import android.util.Log;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.InterpreterError;
import org.inspirecenter.amazechallenge.model.Player;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";
    public static final String PARAMETER_KEY_CODE = "code";
    public static final String RUN_FUNCTION = "run";
    public static final String INIT_FUNCTION = "init";
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
            code = processCode(code);

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
                RHINO.evaluateString(scope, code, INIT_FUNCTION, 1, null);
                Function function = (Function) scope.get(INIT_FUNCTION, scope);
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
        try {
            Context RHINO = Context.enter();
            RHINO.setOptimizationLevel(-1);
            ScriptableObject scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, code, RUN_FUNCTION, 1, null);
            Function function = (Function) scope.get(RUN_FUNCTION, scope);
            Object result = function.call(RHINO, scope, scope, new Object[] { this });
            nextMove = (PlayerMove) Context.jsToJava(result, PlayerMove.class);
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { Context.exit(); }

        Log.d(TAG, " ** After wrapper: " + javascriptArguments);

        return nextMove;
    }//end getNextMove()

    @Override
    public String toString() { return "Interpreted Grid"; }

    private Map<String,Object> javascriptArguments = new HashMap<>();

    public Object getJavascriptArgument(final String key) {
//        Log.d(TAG, "    *** getJavascriptArgument GET: " + key + " -> " + javascriptArguments.get(key) + ", value type: " + (javascriptArguments.get(key) != null ? javascriptArguments.get(key).getClass() :  "null"));
        return javascriptArguments.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
//        Log.d(TAG, "    *** getJavascriptArgument SET: " + key + " -> " + value + " of type: [" + value.getClass() + "] (value was : " + javascriptArguments.get(key) + ")");
        return javascriptArguments.put(key, value);
    }

    private static String processCode(final String code) {
        final int declarationsEnd = getPlayerCodeStart(code);
        final String variableStoring = "\n\n\n//Params Array\n" +
                "var propNames = [];\n" +
                "function populatePropNames() {\n"+
                "   for(var propName in this) {\n" +
                "      if (typeof this[propName] != 'function' && this[propName] != 'propName' && !(this[propName] instanceof Array)) {\n" +
                "         propNames.push(propName);\n" +
                "      }\n" +
                "   }\n" +
                "}//end populatePropNames()\n" +
                "\n" +
                "function _getRandomInt(a, b) {\n" +
                "  if (a > b) {\n" +
                "    var c = a;\n" +
                "    a = b;\n" +
                "    b = c;\n" +
                "  }\n" +
                "  return Math.floor(Math.random() * (b - a + 1) + a);\n" +
                "}//end _getRandomInt()\n";
        String startingCode = "";
        if (declarationsEnd > 0) startingCode = code.substring(0, declarationsEnd);
        String endingCode = "";
        if (declarationsEnd > 0) endingCode = code.substring(declarationsEnd, code.length());
        else endingCode = code;
        final String finalCode = startingCode + variableStoring + endingCode;
        return filterCode(finalCode);
    }//end wrapCode()

    /**
     * Finds and returns the place where variable declarations stop in the generated code.
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

    /**
     * Filters the generated code so that only variable declarations and functions are allowed.
     * IMPORTANT: Requires that functions are terminated using //end after their ending brace ( }//end )
     * @param CODE The original generated code.
     * @return A filtered string containing the processed code.
     */
    private static String filterCode(final String CODE) {
        StringBuilder builder = new StringBuilder();
        String code = CODE.replaceAll("\r", "");
        String[] lines = code.split("\n");
        boolean parseFunction = false;
        for (String line : lines) {
            if (!parseFunction) {
                if (line.startsWith("var ")) {
                    builder.append(line);
                    builder.append("\n");
                }//end if var decl
                if (line.startsWith("function ")) {
                    builder.append("\n");
                    builder.append(line);
                    builder.append("\n");
                    parseFunction = true;
                }//end if func decl
            }//end if not function
            else {
                builder.append(line);
                builder.append("\n");
                if (line.contains("//end")) {
                    builder.append("\n");
                    parseFunction = false;
                }//end if func end
            }//end if function
        }//end foreach line
        return builder.toString();
    }//end filterCode()

}//end class InterpretedMazeSolver
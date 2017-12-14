package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.model.*;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";
    private static final String RUN_FUNCTION = "run";
    private static final String INIT_FUNCTION = "init";
    private String code;
    private static final int TIME_LIMIT_SECONDS = 1;

    private final Challenge challenge;
    private final Game game;

    public InterpretedMazeSolver(
            final Challenge challenge,
            final Game game,
            final String playerEmail,
            final String code) {
        super(playerEmail);
        this.game = game;
        this.challenge = challenge;
        init(code);
    }

    @Override
    Grid getGrid() {
        return challenge.getGrid();
    }

    @Override
    Direction getDirection() {
        return game.getPlayerPositionAndDirection(playerEmail).getDirection();
    }

    @Override
    Position getPosition() {
        return game.getPlayerPositionAndDirection(playerEmail).getPosition();
    }

    // todo consider upgrading to latest rhino: https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Download_Rhino
    private void init(final String unprocessedCode) {

        //Wrap the code:
        code = processCode(unprocessedCode);
//            Log.d(TAG, "Code:\n" + code);

        //Call the Initialization function
        try {
            Context RHINO = Context.enter();
            RHINO.setOptimizationLevel(-1);
            ScriptableObject scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, code, INIT_FUNCTION, 1, null);
            Function function = (Function) scope.get(INIT_FUNCTION, scope);
            function.call(RHINO, scope, scope, new Object[] { this });
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { Context.exit(); }
    }

    @Override
    public PlayerMove getNextMove() {

        PlayerMove nextMove = PlayerMove.NO_MOVE;

        // todo set a deadline (e.g. 1 sec) and if no return from RHINO, stop it and return NO_MOVE
        try {
            Context RHINO = Context.enter();
            RHINO.setOptimizationLevel(-1);
            ScriptableObject scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, code, RUN_FUNCTION, 1, null);
            Function function = (Function) scope.get(RUN_FUNCTION, scope);

            //Object result = function.call(RHINO, scope, scope, new Object[] { this });
            //nextMove = (PlayerMove) Context.jsToJava(result, PlayerMove.class);

            InterpretedMazeSolverExecutor executor = new InterpretedMazeSolverExecutor(function, this, TIME_LIMIT_SECONDS);
            nextMove = executor.execute();
        }
        catch (Exception e) { e.printStackTrace(); }
        finally { Context.exit(); }

        return nextMove;
    }//end getNextMove()

    @Override
    public String toString() { return "Interpreted Maze Solver"; }

    private Map<String,Serializable> javascriptArguments = new HashMap<>();

    @Override
    public Map<String, Serializable> getState() {
        return javascriptArguments;
    }

    @Override
    public void setState(Map<String, Serializable> stateMap) {
        javascriptArguments.putAll(stateMap);
    }

    public Object getJavascriptArgument(final String key) {
//        Log.d(TAG, "getJavascriptArgument - key: " + key + " (" + javascriptArguments.get(key) + " of type: " + javascriptArguments.get(key).getClass() + ")");
        return javascriptArguments.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
//        Log.d(TAG, "setJavascriptArgument - key->value: " + key + " -> " + value + " (of type: " + value.getClass() + ")");
        return javascriptArguments.put(key, (Serializable) value);
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
     * @return A filtered string containing the processed/filtered code.
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
    }
}
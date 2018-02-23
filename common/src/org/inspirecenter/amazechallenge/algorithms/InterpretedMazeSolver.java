package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.controller.RuntimeController;
import org.inspirecenter.amazechallenge.model.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String RUN_FUNCTION = "run";
    private static final String INIT_FUNCTION = "init";
    private String code;
    private static final int TIME_LIMIT_SECONDS = 1;

    private Challenge challenge;
    private Game game;

    public void init(final Challenge challenge, final Game game) {
        this.challenge = challenge;
        this.game = game;
    }

    public InterpretedMazeSolver(final Challenge challenge, final Game game, final String playerId, final String code) {
        super(challenge, game, playerId);
        init(code);
    }

    @Override
    Grid getGrid() {
        return challenge.getGrid();
    }

    @Override
    Direction getDirection() {
        return game.getDirectionByID(playerID);
    }

    @Override
    Position getPosition() {
        return game.getPositionById(playerID);
    }

    private void init(final String unprocessedCode) {

        //Wrap the code
        code = processCode(unprocessedCode);

        //Call the Initialization function
        try {
            final org.mozilla.javascript.Context rhinoContext = org.mozilla.javascript.Context.enter();
            rhinoContext.setOptimizationLevel(-1);
            final org.mozilla.javascript.ScriptableObject scope = rhinoContext.initStandardObjects();
            rhinoContext.evaluateString(scope, code, INIT_FUNCTION, 1, null);
            final org.mozilla.javascript.Function function = (org.mozilla.javascript.Function) scope.get(INIT_FUNCTION, scope);
            function.call(rhinoContext, scope, scope, new Object[] { this });
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            org.mozilla.javascript.Context.exit();
        }
    }

    @Override
    public PlayerMove getNextMove(final Game game) {
        this.game = game;

        PlayerMove nextMove = PlayerMove.NO_MOVE;

        try {
            final org.mozilla.javascript.Context rhinoContext = org.mozilla.javascript.Context.enter();
            rhinoContext.setOptimizationLevel(-1);
            final org.mozilla.javascript.ScriptableObject scope = rhinoContext.initStandardObjects();
            rhinoContext.evaluateString(scope, code, RUN_FUNCTION, 1, null);
            final org.mozilla.javascript.Function function = (org.mozilla.javascript.Function) scope.get(RUN_FUNCTION, scope);

            InterpretedMazeSolverExecutor executor = new InterpretedMazeSolverExecutor(function, this, TIME_LIMIT_SECONDS);
            nextMove = executor.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            org.mozilla.javascript.Context.exit();
        }

        return nextMove;
    }

    @Override
    public String toString() { return "Interpreted Maze Solver"; }

    private Map<String,Serializable> javascriptArguments = new HashMap<>();

    public Object getJavascriptArgument(final String key) {
        return javascriptArguments.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
        return javascriptArguments.put(key, (Serializable) value);
    }


    @Override
    public byte [] getState() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(javascriptArguments);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void setState(byte[] bytes) {
        if(bytes != null && bytes.length > 0) {
            try {
                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                javascriptArguments = (Map<String, Serializable>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String processCode(final String code) {
        final int declarationsEnd = getPlayerCodeStart(code);
        final String variableStoring = "\n\n\n/*Params Array*/\n" +
                "var propNames = [];\n" +
                "function populatePropNames() {\n"+
                "   for(var propName in this) {\n" +
                "      if (typeof this[propName] != 'function' && this[propName] != 'propName' && !(this[propName] instanceof Array)) {\n" +
                "         propNames.push(propName);\n" +
                "      }\n" +
                "   }\n" +
                "}\n" +
                "\n" +
                "function _getRandomInt(a, b) {\n" +
                "  if (a > b) {\n" +
                "    var c = a;\n" +
                "    a = b;\n" +
                "    b = c;\n" +
                "  }\n" +
                "  return Math.floor(Math.random() * (b - a + 1) + a);\n" +
                "}\n";
        String startingCode = "";
        if (declarationsEnd > 0) startingCode = code.substring(0, declarationsEnd);
        String endingCode = "";
        if (declarationsEnd > 0) endingCode = code.substring(declarationsEnd, code.length());
        else endingCode = code;
        final String finalCode = startingCode + variableStoring + endingCode;
        return filterCode(finalCode);
    }

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
        }
        return totalLineCharacters - 1; //Return index, not length
    }

    /**
     * Filters the generated code so that only variable declarations and functions are allowed.
     * IMPORTANT: Requires that functions are terminated using '/*end' after their ending brace ('}/*end').
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
                if (line.contains("/*end")) {
                    builder.append("\n");
                    parseFunction = false;
                }//end if func end
            }//end if function
        }//end foreach line
        return builder.toString();
    }

    @Override
    public PickableType.Bias look(Direction direction) {
        return RuntimeController.look(game, getGrid(), getPosition(), direction);
    }

}
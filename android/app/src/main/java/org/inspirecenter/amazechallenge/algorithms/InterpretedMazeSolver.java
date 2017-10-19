package org.inspirecenter.amazechallenge.algorithms;

import android.util.Log;
import android.widget.Toast;

import org.inspirecenter.amazechallenge.model.Game;
import org.inspirecenter.amazechallenge.model.InterpretedMazeRunnerParams;
import org.inspirecenter.amazechallenge.model.Player;
import org.inspirecenter.amazechallenge.interpreter.MazeInterpreter;
import org.mozilla.javascript.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InterpretedMazeSolver extends AbstractMazeSolver {

    private static final String TAG = "aMazeChallenge";
    public static final String PARAMETER_KEY_CODE = "code";
    private String code;
    private static Context RHINO;
    private InterpretedMazeSolver instance;

    public InterpretedMazeSolver(final Game game, final Player player) {
        super(game, player);
        RHINO = Context.enter();
        RHINO.setOptimizationLevel(-1);
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
                MazeInterpreter.callFunction(code, "init", Object.class, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Iterator it = javascriptArguments.entrySet().iterator();
            if (!it.hasNext()) {
                Log.i(TAG, "EMPTY MAP AFTER INIT!");
            }
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Log.i(TAG, pair.getKey() + " = " + pair.getValue() + "// Type: " + pair.getValue().getClass());
                it.remove(); // avoids a ConcurrentModificationException
            }

        }
    }

    @Override
    public PlayerMove getNextMove() {

        PlayerMove nextMove;

        /*
        try {
            MazeInterpreter.callFunction(code, "wrapper", Object.class, new Object[] { instanceAsObject });
            nextMove  = (PlayerMove) getJavascriptArgument("retVal");
        } catch (Exception e) {
            Log.e("maze", e.getMessage());
            throw new RuntimeException(e);
        }

        Log.i("Current Move: ", nextMove == null ? "null" : nextMove.toString());
        return nextMove == null ? PlayerMove.NO_MOVE : nextMove;*/

        Iterator it = javascriptArguments.entrySet().iterator();
        if (!it.hasNext()) {
            Log.i(TAG, "EMPTY MAP BEFORE WRAPPER!");
        }
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Log.i(TAG, pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        try {
            MazeInterpreter.callFunction(code, "wrapper", Object.class, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        boolean varBool = (boolean) getJavascriptArgument("bool");
//        int varInt = (int) getJavascriptArgument("int");
//        float varFloat = (float) getJavascriptArgument("float");
//
//        Log.i(TAG, "Variable values are:  bool = " + String.valueOf(varBool) + ", int = " + String.valueOf(varInt) + ", float = " + String.valueOf(varFloat));

        Iterator its = javascriptArguments.entrySet().iterator();
        if (!its.hasNext()) {
            Log.i(TAG, "EMPTY MAP AFTER WRAPPER!");
        }
        while (its.hasNext()) {
            Map.Entry pair = (Map.Entry)its.next();
            Log.i(TAG, pair.getKey() + " = " + pair.getValue());
            its.remove(); // avoids a ConcurrentModificationException
        }



        return PlayerMove.NO_MOVE;
    }//end getNextMove()

    @Override
    public String toString() {
        return "Interpreted Maze";
    }

    public Map<String,Object> javascriptArguments = new HashMap<>();

    public Object getJavascriptArgument(final String key) {
        return javascriptArguments.get(key);
    }

    public Object setJavascriptArgument(final String key, final Object value) {
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

    void callme(String stuff) {
        Log.i("callme", stuff);
    }

}
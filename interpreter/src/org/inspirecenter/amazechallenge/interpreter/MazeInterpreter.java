package org.inspirecenter.amazechallenge.interpreter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class MazeInterpreter {

    public static final String ENGINE_NAME = "Rhino 7R4";

    /**
     * Evaluates a given JavaScript source.
     * @param CODE The code to evaluate
     */
    public static void interpret(final String CODE) {
        final Context RHINO = Context.enter();
        try {
            Scriptable scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, CODE, "script", 1, null);
        }//end try
        finally { Context.enter(); }
    }//end interpret

    /**
     * Calls the specified JavaScript function.
     * @param CODE A string containing the code to evaluate.
     * @param FUNCTION_NAME The function name being called.
     * @param desiredType The type returned by the function being called.
     * @param parameters Parameters passed to the function being called.
     * @return Returns a generic object as the result of the function being called. The object can be cast into any type as given in the desiredType parameter.
     * @throws Exception
     */
    public static Object callFunction(final String CODE, final String FUNCTION_NAME, Class<?> desiredType, Object ... parameters) throws Exception {
        final Context RHINO = Context.enter();
        try {
            Scriptable scope = RHINO.initStandardObjects();
            RHINO.evaluateString(scope, CODE, "script", 1, null);
            Function function = (Function) scope.get(FUNCTION_NAME, scope);
            Object result = function.call(RHINO, scope, scope, parameters);
            return RHINO.jsToJava(result, desiredType);
        }//end try
        finally { Context.exit(); }
    }//end callFunction

    /**
     * Wraps a variable as an object and passes it to the given JavaScript scope.
     * @param objectToWrap The object to be passed.
     * @param scope The scope in which the object will be passed to.
     * @return Returns an object wrapped in the given JavaScript scope.
     */
    public static Object wrapObject(Object objectToWrap, Scriptable scope) {
        return Context.javaToJS(objectToWrap, scope);
    }//end wrapObject()

    /**
     * Passes a *WRAPPED* object to the given JavaScript scope.
     * @param scope The scope to pass the object into.
     * @param scopeName The name of the scope that the object is being passed into.
     * @param passedObject
     */
    public static void passObject(Scriptable scope, final String scopeName, Object passedObject) {
        ScriptableObject.putProperty(scope, scopeName, passedObject);
    }//end passObject()

}//end class MazeInterpreter
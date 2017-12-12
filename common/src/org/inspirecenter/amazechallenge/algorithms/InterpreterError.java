package org.inspirecenter.amazechallenge.algorithms;

import org.inspirecenter.amazechallenge.algorithms.errorfinder.EmptyCompoundStatementFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.EmptyConditionalFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.EmptyInitFunctionFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.EmptyRunFunctionFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.ErrorFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.FunctionInFunctionFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.InfiniteLoopFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.MissingInitFunctionFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.MissingRunFunctionFinder;
import org.inspirecenter.amazechallenge.algorithms.errorfinder.RunRedefinitionFinder;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.ERROR;
import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.WARNING;

public enum InterpreterError {

    MISSING_RUN_FUNC("Missing run function.", ERROR, MissingRunFunctionFinder.class),
    MISSING_INIT_FUNC("Missing initialization function.", WARNING, MissingInitFunctionFinder.class),
    EMPTY_RUN_FUNC("Empty run function found.", ERROR, EmptyRunFunctionFinder.class),
    FUNCTION_IN_FUNCTION("Function in function statement found.", ERROR, FunctionInFunctionFinder.class),
    INFINITE_LOOP("Infinite loop found.", ERROR, InfiniteLoopFinder.class),
    EMPTY_CONDITIONAL("Empty conditional found.", ERROR, EmptyConditionalFinder.class),
    EMPTY_STATEMENT_BODY("Empty compound statement body found.", WARNING, EmptyCompoundStatementFinder.class),
    REDEFINITION_RUN_FUNC("Function \"Run\" is already defined.", ERROR, RunRedefinitionFinder.class),
    EMPTY_INIT_FUNC("Empty \"Initialize\" function found.",WARNING, EmptyInitFunctionFinder.class)
    ;

    public final String name;
    public final InterpreterErrorType type;
    public final Class<? extends ErrorFinder> errorFinder;

    InterpreterError(String name, InterpreterErrorType type, Class<? extends ErrorFinder> errorFinder) {
        this.name = name;
        this.type = type;
        this.errorFinder = errorFinder;
    }//end InterpreterError()

    @Override
    public String toString() {
        return name;
    }

    public ArrayList<InterpreterError> executeErrorFinder(final String code) {
        Method requiredMethod = null;
        Method[] methods = errorFinder.getMethods();
        try {
            for (Method m : methods) {
                if (m.getName().equalsIgnoreCase(ErrorFinder.EXECUTE_METHOD_NAME)) {
                    Class[] parameterClasses = m.getParameterTypes();
                    requiredMethod = errorFinder.getMethod(ErrorFinder.EXECUTE_METHOD_NAME, parameterClasses);
                    break;
                }//end if
            }//end for
            Object o = errorFinder.newInstance();
            return (ArrayList<InterpreterError>) requiredMethod.invoke(o, new Object[]{code});
        }//end try
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }//end executeErrorFinder()

    public enum InterpreterErrorType {
        WARNING,
        ERROR
    }//end enum InterpreterErrorType

}//end class InterpreterError
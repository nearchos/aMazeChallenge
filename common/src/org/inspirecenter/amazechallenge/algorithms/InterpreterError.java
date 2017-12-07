package org.inspirecenter.amazechallenge.algorithms;

import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.ERROR;
import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.WARNING;

public enum InterpreterError {

    MISSING_RUN_FUNC("Missing run function.", ERROR),
    MISSING_INIT_FUNC("Missing initialization function.", WARNING),
    EMPTY_RUN_FUNC("Empty run function found.", ERROR),
    FUNCTION_IN_FUNCTION("Function in function statement found.", ERROR),
    INFINITE_LOOP("Infinite loop found.", ERROR),
    EMPTY_CONDITIONAL("Empty conditional found.", ERROR),
    EMPTY_STATEMENT_BODY("Empty compound statement body found.", WARNING);

    public final String name;
    public final InterpreterErrorType type;

    InterpreterError(String name, InterpreterErrorType type) {
        this.name = name;
        this.type = type;
    }//end InterpreterError()

    @Override
    public String toString() {
        return name;
    }

    public enum InterpreterErrorType {
        WARNING,
        ERROR
    }
}//end class InterpreterError
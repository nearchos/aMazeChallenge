package org.inspirecenter.amazechallenge.model;



public enum InterpreterError {

    MISSING_RUN_FUNC("Missing run function.", InterpreterErrorType.ERROR),
    MISSING_INIT_FUNC("Missing initialization function.", InterpreterErrorType.WARNING),
    EMPTY_RUN_FUNC("Empty run function found.", InterpreterErrorType.ERROR),
    FUNCTION_IN_FUNCTION("Function in function statement found.", InterpreterErrorType.ERROR),
    INFINITE_LOOP("Infinite loop found.", InterpreterErrorType.ERROR);

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

}//end class InterpreterError

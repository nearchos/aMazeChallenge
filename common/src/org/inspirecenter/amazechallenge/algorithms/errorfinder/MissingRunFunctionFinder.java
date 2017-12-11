package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class MissingRunFunctionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.MISSING_RUN_FUNC;
    public static boolean runFunctionExists = false;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        if (!code.contains(ErrorFinderCommons.XML_RUN_FUNCTION_DEFINITION)) errorList.add(ERROR);
        else runFunctionExists = true;
        return errorList;
    }//execute()

}//end class MissingRunFunctionFinder

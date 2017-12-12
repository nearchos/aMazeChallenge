package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class MissingInitFunctionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.MISSING_INIT_FUNC;
    public static boolean initFunctionExists = false;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        if (!code.contains(ErrorFinderCommons.XML_INIT_FUNCTION_DEFINITION)) errorList.add(ERROR);
        else initFunctionExists = true;
        return errorList;
    }//execute()

}//end class MissingInitFunctionFinder

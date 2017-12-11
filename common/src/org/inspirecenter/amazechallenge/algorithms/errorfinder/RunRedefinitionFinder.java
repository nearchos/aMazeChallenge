package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class RunRedefinitionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.REDEFINITION_RUN_FUNC;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        if (ErrorFinderCommons.getOccurencesOfSubstring(ErrorFinderCommons.XML_RUN_FUNCTION_DEFINITION, code) > 1)
            errorList.add(ERROR);
        return errorList;
    }//end execute()

}//end class RunRedefinitionFinder

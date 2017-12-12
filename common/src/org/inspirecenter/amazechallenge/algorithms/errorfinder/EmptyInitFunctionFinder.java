package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;

import java.util.ArrayList;

public class EmptyInitFunctionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.EMPTY_INIT_FUNC;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        if (MissingInitFunctionFinder.initFunctionExists) {
            if (!code.contains(ErrorFinderCommons.XML_INIT_FUNCTION_BODY_START))
                errorList.add(ERROR);
        }//end if initFunctionExists
        return errorList;
    }//end execute()

}//end class EmptyInitFunctionFinder
package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;

import java.util.ArrayList;

public class InfiniteLoopFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.INFINITE_LOOP;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        final String[] lines = code.split("\n");
        boolean parsingWhileLoop = false;
        boolean infiniteLoopExists = false;
        for (final String line : lines) {
            if (!parsingWhileLoop) {
                if (line.contains(ErrorFinderCommons.XML_WHILE_DEFINITION)) parsingWhileLoop = true;
            }//end if not parsingWhileLoop
            else {
                if (line.contains(ErrorFinderCommons.XML_LOOP_CONDITIONAL_TRUE)) {
                    infiniteLoopExists = true;
                    parsingWhileLoop = true;
                    break;
                }//end if
            }//end if parsingWhileLoop
        }//end foreach line
        if (infiniteLoopExists) errorList.add(ERROR);
        return errorList;
    }//end execute()

}//end class InfiniteLoopFinder
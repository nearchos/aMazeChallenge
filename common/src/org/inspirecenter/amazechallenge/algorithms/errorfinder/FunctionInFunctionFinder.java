package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class FunctionInFunctionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.FUNCTION_IN_FUNCTION;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        final String[] lines = code.split("\n");
        boolean parsingFunction = false;
        boolean functionInFunctionExists = false;
        boolean nextStatementClauseFound = false;
        for (final String line : lines) {
            if (!parsingFunction) {
                if (line.contains(ErrorFinderCommons.XML_INIT_FUNCTION_DEFINITION)) parsingFunction = true;
                else if (line.contains(ErrorFinderCommons.XML_RUN_FUNCTION_DEFINITION)) parsingFunction = true;
            }//end if not parsing function
            else {
                if (line.contains(ErrorFinderCommons.XML_NEXT_STATEMENT_TAG)) nextStatementClauseFound = true;
                else if (line.contains(ErrorFinderCommons.XML_NEXT_STATEMENT_END)) nextStatementClauseFound = false;
                if (!nextStatementClauseFound && (line.contains(ErrorFinderCommons.XML_RUN_FUNCTION_DEFINITION) ||
                        line.contains(ErrorFinderCommons.XML_INIT_FUNCTION_DEFINITION))) {
                    functionInFunctionExists = true;
                    break;
                }//end if
            }//end if parsing function
        }//end foreach line
        if (functionInFunctionExists) errorList.add(ERROR);
        return errorList;
    }//end execute()

}//end FunctionInFunctionFinder
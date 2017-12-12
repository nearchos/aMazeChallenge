package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;

import java.util.ArrayList;

public class EmptyRunFunctionFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.EMPTY_RUN_FUNC;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        final String[] lines = code.split("\n");
        if (MissingRunFunctionFinder.runFunctionExists) {
            boolean runHasStatements = false;
            boolean parsingRunFunction = false;
            for (final String line : lines) {
                if (!parsingRunFunction) {
                    if (line.contains(ErrorFinderCommons.XML_RUN_FUNCTION_DEFINITION))
                        parsingRunFunction = true;
                }//end if not parsingRunFunction
                else {
                    if (line.contains(ErrorFinderCommons.XML_START_BLOCK)) {
                        runHasStatements = true;
                        break;
                    }//end if line has block decl
                    if (line.contains(ErrorFinderCommons.XML_END_BLOCK)) parsingRunFunction = false;
                }//end if parsingRunFunction
            }//end for all lines
            if (!runHasStatements)  errorList.add(ERROR);
        }//end if run exists
        return errorList;
    }//end execute()

}//end class EmptyRunFunctionFinder
package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class EmptyConditionalFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.EMPTY_CONDITIONAL;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        final String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (i + 1 < lines.length) {
                //Check if statements:
                if (lines[i].contains(ErrorFinderCommons.XML_IF_DEFINITION)) {
                    if (!lines[i + 1].contains(ErrorFinderCommons.XML_IF_CONDITIONAL_CLAUSE)) {
                        if (i + 2 < lines.length) {
                            if (!lines[i + 2].contains(ErrorFinderCommons.XML_IF_CONDITIONAL_CLAUSE))
                                errorList.add(InterpreterError.EMPTY_CONDITIONAL);
                        }//end if
                        else errorList.add(InterpreterError.EMPTY_CONDITIONAL);
                    }
                }//end if -if block
                //Check while statements:
                else if (lines[i].contains(ErrorFinderCommons.XML_WHILE_DEFINITION)) {
                    if (!lines[i + 1].contains(ErrorFinderCommons.XML_WHILE_CONDITIONAL_CLAUSE))
                        errorList.add(InterpreterError.EMPTY_CONDITIONAL);
                }//end if while block
            }//end if next line exists
            else {
                if (lines[i].contains(ErrorFinderCommons.XML_IF_DEFINITION)
                        || lines[i].contains(ErrorFinderCommons.XML_WHILE_DEFINITION)) {
                    errorList.add(ERROR);
                }//end if currentline is a statement
            }//end if no next line
        }//end foreach line
        return errorList;
    }//end execute()

}//end class EmptyConditionalFinder

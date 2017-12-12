package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

public class EmptyCompoundStatementFinder implements ErrorFinder {

    public static final InterpreterError ERROR = InterpreterError.EMPTY_STATEMENT_BODY;

    @Override
    public ArrayList<InterpreterError> execute(final String code) {
        ArrayList<InterpreterError> errorList = new ArrayList<>();
        final String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (ErrorFinderCommons.lineContainsCompoundStatement(lines[i])) {
                boolean foundNextOrStatementDecl = false;
                for (int j = i; j < lines.length; j++) {
                    if (lines[j].contains(ErrorFinderCommons.XML_NEXT_STATEMENT_TAG)) {
                        errorList.add(ERROR);
                        foundNextOrStatementDecl = true;
                        break;
                    }//end if line contains next decl
                    else if (lines[j].contains(ErrorFinderCommons.XML_STATEMENT_BODY)) {
                        foundNextOrStatementDecl = true;
                        break;
                    }//end if line contains statement decl
                }//end foreach subsequent line
                if (!foundNextOrStatementDecl) errorList.add(ERROR);
            }//end if compound statement found
        }//end foreach line
        return errorList;
    }//end execute()

}//end class EmptyCompoundStatementFinder
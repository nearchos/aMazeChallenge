package org.inspirecenter.amazechallenge.utils;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import org.inspirecenter.amazechallenge.ui.BlocklyActivity;

import java.util.ArrayList;

import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.ERROR;
import static org.inspirecenter.amazechallenge.algorithms.InterpreterError.InterpreterErrorType.WARNING;

public class ErrorFinderManager {

    /**
     * Checks the code for obvious user mistakes such as missing or empty functions etc.
     * @return Returns an InterpreterError to describe the error that occured.
     */
    public static ArrayList<InterpreterError> checkCode(BlocklyActivity a) {
        final String code = FileManager.getTempWorkspaceContents(a);
        ArrayList<InterpreterError> errorList = new ArrayList<>();

        //For each type of error, run its error finder and return a list of errors found:
        for (InterpreterError e : InterpreterError.values()) {
            ArrayList<InterpreterError> currentErrorList = e.executeErrorFinder(code);
            if (!currentErrorList.isEmpty() && currentErrorList != null)
                errorList.addAll(currentErrorList);
        }//end foreach InterpreterError

        //Sort the list before returning it:
        ArrayList<InterpreterError> sortedErrorList = new ArrayList<>();
        for (final InterpreterError e : errorList) { if (e.type == ERROR) sortedErrorList.add(e); }
        for (final InterpreterError e : errorList) { if (e.type == WARNING) sortedErrorList.add(e); }
        return sortedErrorList;
    }//end checkCode()

    /**
     * Gets the next error found in an array or InterpreterErrors.
     * @param list The list of InterpreterErrors (ArrayList)
     * @return Returns null of no errors found, otherwise returns the first error found in the array.
     */
    public static InterpreterError getNextError(ArrayList<InterpreterError> list) {
        for (final InterpreterError e : list) if (e.type == ERROR) return e;
        return null;
    }//end getNextError()

    /**
     * Gets the next warning found in an array or InterpreterErrors.
     * @param list The list of InterpreterErrors (ArrayList)
     * @return Returns null of no warnings found, otherwise returns the first error found in the array.
     */
    public static InterpreterError getNextWarning(ArrayList<InterpreterError> list) {
        for (final InterpreterError e : list) if (e.type == WARNING) return e;
        return null;
    }//end getNextWarning()

}//end class ErrorFinderManager
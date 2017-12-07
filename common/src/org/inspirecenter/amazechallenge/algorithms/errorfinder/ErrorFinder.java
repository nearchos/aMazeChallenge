package org.inspirecenter.amazechallenge.algorithms.errorfinder;

import org.inspirecenter.amazechallenge.algorithms.InterpreterError;
import java.util.ArrayList;

/**
 * Checks for errors in a piece of code.
 */
public interface ErrorFinder {

    public static String EXECUTE_METHOD_NAME = "execute";

    /**
     *  Executes checks of the implementing class on the code provided.
     * @param code The code to check.
     * @return Returns a list of errors encountered.
     */
    ArrayList<InterpreterError> execute(final String code);

}//end abstract class ErrorFinder


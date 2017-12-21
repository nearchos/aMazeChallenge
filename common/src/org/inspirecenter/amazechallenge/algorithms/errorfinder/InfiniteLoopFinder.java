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
        boolean afterShadow = false;
        boolean infiniteLoopExists = false;
        int lineCountAfterShadow = 0;
        boolean hasFalseShadow = false;
        boolean containsLoop = false;

        //Search for the conditional clause lines:
        for (int i = 0;  i < lines.length; i++) {
            if (!parsingWhileLoop) {
                if (lines[i].contains(ErrorFinderCommons.XML_WHILE_DEFINITION)) {
                    parsingWhileLoop = true;
                    containsLoop = true;
                }
            }//end if not parsingWhileLoop
            else {
                if (lines[i].contains(ErrorFinderCommons.XML_WHILE_MODE_END)) {
                    parsingWhileLoop = false;
                    afterShadow = false;
                }
                else {
                    if (!afterShadow) {
                        if (lines[i].contains(ErrorFinderCommons.XML_SHADOW_TAG)) afterShadow = true;
                        if (lines[i].contains(ErrorFinderCommons.XML_LOOP_CONDITIONAL_FALSE)) {
                            hasFalseShadow = true;
                            break;
                        }//end if shadow false
                    }//end if !shadow
                    else {
                        lineCountAfterShadow++;
                        if (lines[i].contains(ErrorFinderCommons.XML_WHILE_MODE_END)) {
                            afterShadow = false;
                            parsingWhileLoop = false;
                        }//end if mode end
                        else {
                            if(lines[i].contains(ErrorFinderCommons.XML_LOOP_CONDITIONAL_TRUE)) {
                                if (lineCountAfterShadow < 4) infiniteLoopExists = true;
                                break;
                            }//end if shadow true
                        }//end if not end
                    }//end if shadow
                }//end if not mode end
            }//end if parsingWhileLoop
        }//end foreach line
        if (lineCountAfterShadow < 2 && !hasFalseShadow && containsLoop) infiniteLoopExists = true;

        if (infiniteLoopExists) errorList.add(ERROR);
        return errorList;
    }//end execute()

}//end class InfiniteLoopFinder
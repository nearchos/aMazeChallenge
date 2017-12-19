package org.inspirecenter.amazechallenge.algorithms.errorfinder;

class ErrorFinderCommons {

    static final String XML_RUN_FUNCTION_DEFINITION = "<block type=\"maze_run_function\"";
    static final String XML_INIT_FUNCTION_DEFINITION = "<block type=\"maze_init_function\"";
    static final String XML_IF_DEFINITION = "<block type=\"controls_if\"";
    static final String XML_REPEAT_DEFINITION = "<block type=\"controls_repeat_ext\"";
    static final String XML_WHILE_DEFINITION = "<block type=\"controls_whileUntil\"";
    static final String XML_START_BLOCK = "<block";
    static final String XML_END_BLOCK = "</block>";
    static final String XML_LOOP_CONDITIONAL_TRUE = "<field name=\"BOOL\">TRUE</field>";
    static final String XML_LOOP_CONDITIONAL_FALSE = "<field name=\"BOOL\">FALSE</field>";
    static final String XML_IF_CONDITIONAL_CLAUSE = "<value name=\"IF0\">";
    static final String XML_WHILE_CONDITIONAL_CLAUSE = "<value name=\"BOOL\">";
    static final String XML_NEXT_STATEMENT_TAG = "<next>";
    static final String XML_NEXT_STATEMENT_END = "</next>";
    static final String XML_STATEMENT_BODY = "<statement name=\"DO";
    static final String XML_INIT_FUNCTION_BODY_START = "<statement name=\"init\">";
    static final String XML_WHILE_MODE_END = "<field name=\"MODE\">WHILE</field>";
    static final String XML_SHADOW_TAG = "</shadow>";

    static int getOccurencesOfSubstring(final String stringToFind, final String str) {
        int lastIndex = 0;
        int count = 0;
        while(lastIndex != -1){
            lastIndex = str.indexOf(stringToFind,lastIndex);
            if(lastIndex != -1){
                count++;
                lastIndex += stringToFind.length();
            }//end if
        }//end while
        return count;
    }//end getOccurencesOfSubstring()

    /**
     * Checks if the given line of code contains a compound statement declaration.
     * @param line The line to check for compound statements.
     * @return Returns true if compound statement exists, false otherwise.
     */
    static boolean lineContainsCompoundStatement(String line) {
        return (
                //A list of all the statement types (in XML) that are compound:
                line.contains(XML_IF_DEFINITION) ||
                        line.contains(XML_REPEAT_DEFINITION) ||
                        line.contains(XML_WHILE_DEFINITION));
    }//end lineContainsCompoundStatement()

}//end class ErrorFinderCommons
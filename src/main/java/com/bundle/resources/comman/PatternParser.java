package com.bundle.resources.comman;

public class PatternParser {

    /**
     * @param currentParseLine ["2 channels", "1 channel"]
     */

    String splitPattern1 = "\", \"";
    String splitPattern2 = "\", @";
    String splitPattern3 = ", @";
    String qxeSplitPattern = "##@@##QXE##@@##";

    public String[] parseArrayValues(String currentParseLine) {
        String[] splitLines =null;
        if(currentParseLine.contains("(styled string) \"")){
            splitLines= parseStyledArrayValues(currentParseLine);

        }else {
            //System.out.println("@@@@@@@ parseArrayValues()...");
            currentParseLine = currentParseLine.substring(currentParseLine.indexOf("[") + 1, currentParseLine.lastIndexOf("]"));
            currentParseLine = currentParseLine.replace(splitPattern1, "\"##@@##QXE##@@##\"");
            currentParseLine = currentParseLine.replace(splitPattern2, "\"##@@##QXE##@@##@");
            currentParseLine = currentParseLine.replace(splitPattern3, "##@@##QXE##@@##@");
            splitLines=currentParseLine.split(qxeSplitPattern);
            /*for (int count = 0; count < splitLines.length; count++) {
                System.out.println(" "+splitLines[count]);
            }*/
        }
        //System.out.println();
        return splitLines;
    }

    private String[] parseStyledArrayValues(String currentParseLine) {
            String[] splitLines =null;
            System.out.println(currentParseLine);
            currentParseLine = currentParseLine.substring(currentParseLine.indexOf("(styled string) ") + 16, currentParseLine.lastIndexOf("]"));
            currentParseLine = currentParseLine.replace(splitPattern1, "##@@##QXE##@@##\"");
            currentParseLine = currentParseLine.replace(splitPattern2, "\"##@@##QXE##@@##@");
            currentParseLine = currentParseLine.replace(splitPattern3, "##@@##QXE##@@##@");
            splitLines=currentParseLine.split(qxeSplitPattern);
            /*for (int count = 0; count < splitLines.length; count++) {
                System.out.println(""+splitLines[count]);
            }
            System.out.println("");*/
        return splitLines;
    }
}

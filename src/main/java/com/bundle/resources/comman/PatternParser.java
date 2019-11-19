package com.bundle.resources.comman;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser {

    public static String mQXESplitPattern = "##@@##QXE##@@##";

    public static String getString(String currentParseLine) {
        String mStrRegexPattern = "(\\)\\s\\\".*\\\"\\ssrc=\\/)";// ) "****************" src=/
        final Pattern pattern = Pattern.compile(mStrRegexPattern, Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(currentParseLine);
        String mOutputString = (matcher.find() ? matcher.group(0) : null);
        if (mOutputString != null && mOutputString.startsWith(") \"") && mOutputString.endsWith("\" src=/")) {
            mOutputString = mOutputString.substring(mOutputString.indexOf(") \"") + 3, mOutputString.lastIndexOf("\" src=/"));
        }
        mOutputString = StringEscapeUtils.escapeXml(mOutputString);
        return mOutputString;
    }

    public String[] getStringArray(String currentParseLine) {
        String splitPattern1 = "\", \"";
        String splitPattern2 = "\", @";
        String splitPattern3 = ", @";

        currentParseLine = currentParseLine.substring(currentParseLine.indexOf("[") + 1, currentParseLine.lastIndexOf("]"));
        currentParseLine = currentParseLine.replace(splitPattern1, "\""+mQXESplitPattern+"\"");
        currentParseLine = currentParseLine.replace(splitPattern2, "\""+mQXESplitPattern+"@");
        currentParseLine = currentParseLine.replace(splitPattern3, mQXESplitPattern+"@");
        Pattern pattern = Pattern.compile(mQXESplitPattern);
        String[] splitLines = pattern.split(currentParseLine);
        return splitLines;
    }

    public static String[] parseStyledArrayValues(String currentParseLine) {
        String styledFullPattern   = "(((\\s\\w+:\\d+,\\d+)+,)(\\s\\(styled\\sstring\\)\\s|\\s))";
        String mHtmlTagPattern     = "((\\s\\w+:\\d+,\\d+)+,)";
        String styledStringPattern = "\\s\\(styled\\sstring\\)\\s";
        String splitPattern        = "\", @";

        currentParseLine = currentParseLine.substring(currentParseLine.indexOf("[(styled string) \"") + 17, currentParseLine.lastIndexOf("]"));

        Pattern pattern = Pattern.compile(styledFullPattern, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(currentParseLine);
        while (matcher.find()) {
            String matchGroup=matcher.group(0);
            String matchGroupHtmlTag=matchGroup.replace(" (styled string) ","");
            currentParseLine = currentParseLine.replace(matchGroup, matchGroupHtmlTag+ mQXESplitPattern);
        }

        pattern = Pattern.compile(styledStringPattern, Pattern.MULTILINE | Pattern.DOTALL);
        matcher = pattern.matcher(currentParseLine);
        while (matcher.find()) {
            String matchGroup=matcher.group(0);
            currentParseLine = currentParseLine.replace(matchGroup, mQXESplitPattern);
        }

        // System.out.println(""+currentParseLine);
        // System.exit(0);
        currentParseLine = currentParseLine.replace(splitPattern, "\""+mQXESplitPattern+"@");

        Pattern finalPattern = Pattern.compile(mQXESplitPattern);
        String[] splitLines = finalPattern.split(currentParseLine);
        for(int count=0;count<splitLines.length;count++){
            //  System.out.println("bcvbcv "+splitLines[count]);
            splitLines[count]=applyHtmltags(splitLines[count],mHtmlTagPattern);
        }
        //System.exit(0);
        return splitLines;
    }

    public static String insertString(String originalString, String stringToBeInserted, int index) {
        StringBuffer newString = new StringBuffer(originalString);
        newString.insert(index + 1, stringToBeInserted);
        return newString.toString();
    }

    private static String applyHtmltags(String splitLine, String mHtmlTagsPattern) {
        //b:0,17 b:121,134 b:211,218 b:249,257 b:289,303 b:384,401 b:455,471,
        //System.out.println("@@@@@@@"+splitLine);
        int insertIndex=0;
        Pattern pattern = Pattern.compile(mHtmlTagsPattern, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(splitLine);
        if(matcher.find()){
            String matchGroup=matcher.group(0).trim();
            splitLine=splitLine.replace(matchGroup,"");

            String htmlTags[]= matchGroup.split(" ");
            for(int count=0;count<htmlTags.length;count++){
                String currentTag=htmlTags[count];
                if(currentTag.endsWith(",")){
                    currentTag=currentTag.substring(0,currentTag.length()-1);
                }
                //System.out.println(""+currentTag);

                String htmlValue=currentTag.substring(0,currentTag.indexOf(":"));
                String startIndex=currentTag.substring(currentTag.indexOf(":")+1,currentTag.indexOf(","));
                String endIndex=currentTag.substring(currentTag.indexOf(",")+1);
                String htmlValueStart="<"+htmlValue+">";
                String htmlValueEnd="</"+htmlValue+">";
                //System.out.println(""+insertIndex);
                int startIndexInt=Integer.parseInt(startIndex)+(insertIndex);
                int endIndexInt=Integer.parseInt(endIndex)+(insertIndex);


                splitLine=insertString(splitLine,htmlValueStart,startIndexInt);
                endIndexInt=endIndexInt+htmlValueEnd.length();
                splitLine=insertString(splitLine,htmlValueEnd,endIndexInt);
                insertIndex=insertIndex+(htmlValueStart.length()+htmlValueEnd.length());

            }
        }
        //System.out.println("@@@@@@@"+splitLine+"=======================================");
        return  splitLine;
    }
}


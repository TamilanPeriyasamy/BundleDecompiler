package com.bundle.resources.comman;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.bundle.utils.FilePaths;
import com.bundle.command.CommandExecutor;


public class FrameworkResources {

    File mApkFile = null;
    public static String mFrameworkId = null;
    public static HashMap<String,String> mFrameworkResourcesValues = new HashMap<String, String>();
    public static HashMap<String, String> mFrameworkAttributeValues = new HashMap<String, String>();
    public static HashMap<String,String> mFrameworkAttributeArrayTypes = new HashMap<String, String>();
    public static HashMap<String,String> mFrameworkAttributeArrayValues = new HashMap<String,String>();

    public FrameworkResources() {
        mApkFile = new File(FilePaths.mFrameworkPath);
    }

    public void parseFrameworkResources() throws Exception {
        System.out.println("load framework table...");
        ArrayList<String> dumpInputStream = new CommandExecutor(mApkFile).getResourcesTable(Resources.mDumpResources);
        ArrayList<ArrayList<String>> frameworkInputStream = new ArrayList<ArrayList<String>>(dumpInputStream.size());

        ArrayList<String> currentResourcesList = new ArrayList<String>();
        for (int lineCount = 0; lineCount < dumpInputStream.size(); lineCount++) {
            String currentLine = dumpInputStream.get(lineCount);
            //System.out.println(currentLine);
            if (currentLine.startsWith("Binary APK")) {
                //System.out.println(currentLine);

            }else if(currentLine.startsWith("warn: resource ")){
                //System.out.println(currentLine);

            } else if (currentLine.trim().startsWith("Package ") && currentLine.contains("id=")) {
                mFrameworkId ="0x"+currentLine.substring(currentLine.indexOf("id=") + 3).trim();

            } else if (currentLine.trim().startsWith("resource 0x")) {
                frameworkInputStream.add(currentResourcesList);
                currentResourcesList = new ArrayList<String>();
            }
            currentResourcesList.add(currentLine);
        }
        parseFrameworkResources(frameworkInputStream);
    }


    public void parseFrameworkResources(ArrayList<ArrayList<String>> frameworkInputStream) throws Exception {

        String mResourceId          = null;
        String mResourceName        = null;
        String mResourceType        = null;
        String mResourceEntry       = null;

        String mAttributeValue      = null;
        String mAttributeKey        = null;
        String mAttributeRawType    = null;


        String mFlagType            = null;
        String mArrayRawType        = null;
        String mArrAttributeValues  = "";

        for (int listCount = 0; listCount < frameworkInputStream.size(); listCount++) {
            ArrayList<String> resourcesList = frameworkInputStream.get(listCount);

            for (int parserCount = 0; parserCount < resourcesList.size(); parserCount++) {

                String currentParser = resourcesList.get(parserCount);
                //System.out.println(" "+currentParser);
                if (currentParser.trim().startsWith("resource 0x")) {
                    // System.out.println(" "+currentParser);
                    //resource 0x010a0000 anim/fade_in PUBLIC
                    mFlagType     = null;
                    String dumpValues[] = currentParser.trim().split(" ");
                    mResourceId = dumpValues[1].trim();
                    mResourceEntry = dumpValues[2].trim();
                    mResourceType = mResourceEntry.substring(0, mResourceEntry.indexOf("/"));
                    mResourceName = mResourceEntry.substring(mResourceEntry.indexOf("/") + 1);

                    if( mArrayRawType!=null && mArrAttributeValues.length()!=0 ) {
                        if(mArrAttributeValues.startsWith(",")){
                            mArrAttributeValues=mArrAttributeValues.substring(1);
                        }
                        //System.out.println("mAttArrayRawType "+mArrayRawType);
                        //System.out.println("mAttributeValues.length() "+mArrAttributeValues+"\n");
                        mFrameworkAttributeArrayValues.put(mArrayRawType, mArrAttributeValues);
                        mArrAttributeValues="";
                    }
                }

                if(currentParser.trim().startsWith("(") && currentParser.contains(") type=")){
                    String dumpValues[] = currentParser.trim().split(" ");
                    mFlagType=dumpValues[2].substring(dumpValues[2].indexOf("type=")+5);
                    mArrayRawType="android:" + mResourceName+"("+mResourceId+")";
                    mFrameworkAttributeArrayTypes.put(mArrayRawType,mFlagType);
                    //System.out.println(""+mArrayRawType);
                   //System.out.println(""+mFlagType+"\n");
                }

                if (currentParser.contains("(0x") && currentParser.contains(")=0x")) {
                    /* appop(0x010201b1)=0x00000040 */
                    //System.out.println(" "+currentParser);
                    mAttributeValue = currentParser.substring(0, currentParser.indexOf("(0x")).trim();
                    mAttributeKey = currentParser.substring(currentParser.lastIndexOf(")=0x") + 2).trim();
                    mAttributeRawType = "android:" + mResourceName + "(" + mResourceId + ")=" + mAttributeKey;
                    String mAttributeHexaValue  = currentParser.substring(currentParser.lastIndexOf(")=0x")+4);
                    mFrameworkAttributeValues.put(mAttributeRawType, mAttributeValue);
                    if (mFlagType != null)
                        mArrAttributeValues = mArrAttributeValues + "," + mAttributeHexaValue;
                    }
            }
        }
    }
}
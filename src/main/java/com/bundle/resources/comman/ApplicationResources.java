package com.bundle.resources.comman;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.bundle.main.BundleAnalyze;
import com.bundle.resources.values.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.bundle.command.CommandExecutor;
import com.bundle.xmlbuilder.XMLBuilder;

public class ApplicationResources {

    public static String mApplicationId  = null;
    public static String mAppPackageName = null;
    File mApkResourceDir    = null;
    File mMainValuesDir     = null;
    File mBaseApkFile       = null;
    public int mConfigCount = 0;

    ArrayList<String> mMainResourceTableStream=new ArrayList<String>();
    ArrayList<String> mValuesXmlList=new ArrayList<String>();
    public static HashMap<String,String> mApplicationResourcesValues = new HashMap<String, String>();
    public static HashMap<String,String> mApplicationAttributeValues = new HashMap<String, String>();

    public static HashMap<String,String> mApplicationAttributeArrayTypes = new HashMap<String, String>();
    public static HashMap<String,String> mApplicationAttributeArrayValues = new HashMap<String, String>();

    public ApplicationResources(String baseApkPath, String appBuildDirPath) {
        mConfigCount    = 0;
        mBaseApkFile    = new File(baseApkPath);
        mApkResourceDir = new File(appBuildDirPath + File.separator + "res");
        mMainValuesDir  = new File(mApkResourceDir + File.separator + "values");
    }

    private void updateCurrentXmlFile(File currentXmlFile, String xmlEntryStream) throws IOException {

        xmlEntryStream=xmlEntryStream.trim();// Ignore all space...!!!
        if (currentXmlFile != null && currentXmlFile.exists()) {
            FileWriter writer = new FileWriter(currentXmlFile, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            if (xmlEntryStream.startsWith(Resources.mParseResourceEntry +"0x")) { //added dummy line
                xmlEntryStream="=======================================================================\n"+xmlEntryStream;
            }
            bufferWriter.write(xmlEntryStream);
            bufferWriter.newLine();
            bufferWriter.close();
            writer.close();
        }
    }

    public void parseApplicationResources() throws Exception {
        System.out.println("load application table...");
        //String runCommand = FilePaths.mAAPT2Path + " dump -v " +mBaseApkFile.getAbsolutePath();
        mMainResourceTableStream= new CommandExecutor(mBaseApkFile).getResourcesTable(Resources.mDumpResources);
        parseApplicationResources(mMainResourceTableStream);
    }

    private void parseApplicationResources(ArrayList<String> mMainResourceTableStream) throws IOException {

        String mResourceId           = null;
        String mResourceName         = null;
        String mResourceType         = null;
        String mResourceEntry        = null;
        String mAttributeValue       = null;
        String mAttributeKey         = null;
        String mAttributeRawType     = null;

        String mFlagType            = null;
        String mArrayRawType        = null;
        String mArrAttributeValues  = "";

        for (int lineCount = 0; lineCount < mMainResourceTableStream.size(); lineCount++) {

            String currentLine = mMainResourceTableStream.get(lineCount);
            //System.out.println(currentLine);
            if (currentLine.trim().startsWith("Proto APK ") || currentLine.trim().startsWith("type ")) {
                //System.out.println(currentLine);

            }else if(currentLine.startsWith("warn: resource ")){
                //System.out.println(currentLine);

            } else if (currentLine.trim().startsWith(Resources.mParsePackageName) && currentLine.contains(Resources.mParsePackageId)) {
                //Package name=com.quixxi.myapplication id=7f
                ApplicationResources.mApplicationId = "0x" + currentLine.substring(currentLine.indexOf(Resources.mParsePackageId) + 4).trim();
                ApplicationResources.mAppPackageName = currentLine.substring(currentLine.indexOf(Resources.mParsePackageName)+13,currentLine.indexOf(Resources.mParsePackageId)).trim();

            } else if (currentLine.trim().startsWith(Resources.mParseResourceEntry +"0x")) {
                // resource 0x7f0d0067 style/Base.V7.Widget.AppCompat.Toolbar
                String dumpValues[] = currentLine.trim().split(" ");
                mResourceId = dumpValues[1].trim();
                mResourceEntry = dumpValues[2].trim();
               // mResourceType = mResourceEntry.substring(0, mResourceEntry.indexOf("/"));
                mResourceName = mResourceEntry.substring(mResourceEntry.indexOf("/") + 1);
              //  mCurrentResourceEntry = currentLine;
                mApplicationResourcesValues.put(mResourceId, mResourceEntry);

                if (mArrayRawType != null && mArrAttributeValues.length() != 0) {
                    if (mArrAttributeValues.startsWith(",")) {
                        mArrAttributeValues = mArrAttributeValues.substring(1);
                    }
                    //System.out.println("mAttArrayRawType "+mArrayRawType);
                    //System.out.println("mAttributeValues.length() "+mArrAttributeValues+"\n");
                    mApplicationAttributeArrayValues.put(mArrayRawType, mArrAttributeValues);
                    mArrAttributeValues = "";
                }

            }else if (currentLine.trim().startsWith("(") && currentLine.contains(") type=")) {
                String dumpValues[] = currentLine.trim().split(" ");
                mFlagType = dumpValues[2].substring(dumpValues[2].indexOf("type=") + 5);
                mArrayRawType = "android:" + mResourceName + "(" + mResourceId + ")";
                mApplicationAttributeArrayTypes.put(mArrayRawType, mFlagType);
                //System.out.println(""+mArrayRawType);
                //System.out.println(""+mFlagType+"\n");
            }else if (currentLine.contains("(0x") && currentLine.contains(")=0x")) {
                //System.out.println(" "+currentParser);
                //appop(0x010201b1)=0x00000040
                mAttributeValue = currentLine.substring(0, currentLine.indexOf("(0x")).trim();
                mAttributeKey = currentLine.substring(currentLine.lastIndexOf(")=0x") + 2).trim();
                mAttributeRawType = mResourceName + "(" + mResourceId + ")=" + mAttributeKey;
                String mAttributeHexaValue  = currentLine.substring(currentLine.lastIndexOf(")=0x")+4);
                mApplicationAttributeValues.put(mAttributeRawType, mAttributeValue);
                if (mFlagType != null) {
                    mArrAttributeValues = mArrAttributeValues + "," + mAttributeHexaValue;
                }
            }
        }
    }


    public void parseValuesXmlResources() throws Exception {
        System.out.println("decode values xmls...");
        createValuesXmlFiles(mMainResourceTableStream);
        parseOtherXmlFiles(mValuesXmlList);
        parsePublicXmlFile(mMainResourceTableStream);
    }

    private void createValuesXmlFiles(ArrayList<String> mMainResourceTableStream) throws IOException {

        String mResourceId           = null;
        String mResourceName         = null;
        String mResourceType         = null;
        String mResourceEntry        = null;

        boolean isValidResourceEntry = true;
        String mCurrentResourceEntry = null;
        File mCurrentXmlFile         = null;

        for (int lineCount = 0; lineCount < mMainResourceTableStream.size(); lineCount++) {
            String currentLine = mMainResourceTableStream.get(lineCount);

            if (currentLine.trim().startsWith("Proto APK ") || currentLine.trim().startsWith("type ")) {

            } else if (currentLine.trim().startsWith("Package ") && currentLine.contains("id=")) {
               // ApplicationResources.mApplicationId = "0x" + currentLine.substring(currentLine.indexOf("id=") + 3).trim();

            } else if (currentLine.trim().startsWith(Resources.mParseResourceEntry +"0x")) {
                // resource 0x7f0d0067 style/Base.V7.Widget.AppCompat.Toolbar
                String dumpValues[] = currentLine.trim().split(" ");
                mResourceId = dumpValues[1].trim();
                mResourceEntry = dumpValues[2].trim();
                mResourceType = mResourceEntry.substring(0, mResourceEntry.indexOf("/"));
                mResourceName = mResourceEntry.substring(mResourceEntry.indexOf("/") + 1);
                mCurrentResourceEntry = currentLine;
                mApplicationResourcesValues.put(mResourceId, mResourceEntry);

            } else if (currentLine.trim().startsWith("(") && !currentLine.contains("(file)") && isValidResourceEntry) {
                String configArr[] = currentLine.trim().split(" ");
                String configType = configArr[0].replace("(", "").replace(")", "");
                String valuesDirName = "values";
                if (configType.length() >= 1) {
                    valuesDirName = valuesDirName + "-" + configType;
                }

                if(mResourceType.endsWith("s")){
                    mResourceType=mResourceType.substring(0,mResourceType.length()-1);
                }
                String xmlFileName = mResourceType+"s.xml";

                mCurrentXmlFile = new File(mApkResourceDir.getAbsolutePath() + File.separator + valuesDirName + File.separator + xmlFileName);
                if (!mCurrentXmlFile.exists( )) {
                    new File(mCurrentXmlFile.getParent()).mkdirs();
                    mCurrentXmlFile.createNewFile();
                    BundleAnalyze.mResXmlList.add("base/res/" + valuesDirName + File.separator + xmlFileName);
                    mValuesXmlList.add(mCurrentXmlFile.getAbsolutePath());
                }
                updateCurrentXmlFile(mCurrentXmlFile, mCurrentResourceEntry + "\n" + currentLine);

            } else {
                if (!currentLine.contains("(file)")) {
                    updateCurrentXmlFile(mCurrentXmlFile, currentLine);
                }
            }
        }
    }

    private void parsePublicXmlFile(ArrayList<String> mainResourcesInputStream) throws Exception {
        /** attr ,drawable ,layout ,anim ,animator ,interpolator ,raw ,plurals ,string ,dimen ,style ,bool ,integer ,color ,array ,id ,menu */
        File publicXml = new File(mMainValuesDir + File.separator + "public.xml");
        BundleAnalyze.mResXmlList.add("base/res/values/public.xml");
        XMLBuilder mXMLBuilder = new XMLBuilder();
        Document document = mXMLBuilder.newDocument();
        Element mRootElement = document.createElement("resources");
        document.appendChild(mRootElement);
        for (int lineCount = 0; lineCount < mainResourcesInputStream.size(); lineCount++) {

            String currentLine = mainResourcesInputStream.get(lineCount);
            if (currentLine.trim().startsWith(Resources.mParseResourceEntry +"0x") && !currentLine.endsWith("PUBLIC")) { // resource 0x7f030000 string/app_name
                String dumpValues[] = currentLine.trim().split(" ");
                String resId = dumpValues[1];
                String  resRawType = dumpValues[2];
                mApplicationAttributeValues.put(resId, resRawType);
                Element childElement = document.createElement("public");
                String resType = resRawType.substring(0, resRawType.lastIndexOf("/"));
                String resName = resRawType.substring(resRawType.lastIndexOf("/") + 1);
                childElement.setAttribute("id", resId);
                childElement.setAttribute("name", resName);
                childElement.setAttribute("type", resType);
                mRootElement.appendChild(childElement);
            }
        }
        mXMLBuilder.createXMLFile(document, publicXml, false);
    }

    private void parseOtherXmlFiles(ArrayList<String> valuesXmlList) throws Exception {

        for (int fileCount = 0; fileCount < valuesXmlList.size() ; fileCount++) {
            File currentXmlFile=new File(valuesXmlList.get(fileCount));

            if(currentXmlFile.getAbsolutePath().endsWith("/attrs.xml")) {
                new AttrResources().parseAttrResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/bools.xml") ){
                new BooleanResources().parseBooleanResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/colors.xml")) {
                new ColorResources().parseColorResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/dimens.xml") ) {
                new DimenResources().parseDimenResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/drawables.xml")) {
                new DrawableResources().parseDrawableResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/ids.xml")) {
                new IdResources().parseIdResources(currentXmlFile);

            }else if( currentXmlFile.getAbsolutePath().endsWith("/integers.xml")){
                new IntegerResources().parseIntegerResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/strings.xml") ) {
                new StringResources().parseStringResources(currentXmlFile);

            } else if(currentXmlFile.getAbsolutePath().endsWith("/styles.xml")) {
               new StyleResources().parseStyleResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/arrays.xml") ){
                new ArrayResources().parseArrayResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/plurals.xml") ){
                new PluralsResources().parsePluralsResources(currentXmlFile);

            }else if(currentXmlFile.getAbsolutePath().endsWith("/styleables.xml") ){
                FileUtils.forceDelete(currentXmlFile);

            }else {
                System.err.println("Error  Error Error"+currentXmlFile);

            }
        }
    }
}

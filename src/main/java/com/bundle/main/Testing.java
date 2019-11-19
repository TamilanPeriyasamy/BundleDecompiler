package com.bundle.main;

import com.bundle.command.CommandExecutor;
import com.bundle.utils.FilePaths;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class Testing {

    public void testBuild(String args[]) throws Exception {
        //Decompile
        String decodeEncodeMode = args[0];
        String mInputFileDir    = args[1];
        String mOutputFileDir   = args[2];
        BundleDecompiler.doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);

        //Build
        String inputFileDir = mInputFileDir.substring(mInputFileDir.indexOf("--in=") + 5).trim();
        String outputFileDir=mOutputFileDir.substring(mOutputFileDir.indexOf("--out=") + 6).trim();
        String inputFileName=new File(inputFileDir).getName();
        String inputFileRawName=inputFileName.replace(".aab","");
        String debugParentDir=new File(outputFileDir).getParent();
        String outputParentDir=new File(debugParentDir).getParent();
        decodeEncodeMode = "build";
        mInputFileDir    = "--in="+debugParentDir+"/"+inputFileRawName;
        mOutputFileDir   = "--out="+outputParentDir+"/output/"+inputFileName;
        BundleDecompiler.doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);

        //Sign
        decodeEncodeMode="sign";
        mInputFileDir  = "--in="+outputParentDir+"/output/"+inputFileName;
        mOutputFileDir = "--out="+outputParentDir+"/output/signed_"+inputFileName;
        BundleDecompiler.doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);
    }
}

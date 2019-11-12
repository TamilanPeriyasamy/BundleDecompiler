package com.bundle.main;

import com.bundle.command.CommandExecutor;
import com.bundle.utils.FilePaths;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class Testing {

    public void testBuild(String args[]) throws Exception {
        String decodeEncodeMode = args[0];
        String mInputFileDir    = args[1];
        String mOutputFileDir   = args[2];
        BundleDecompiler.doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);

        String inputFileName=new File(mInputFileDir).getName();
        String inputFileRawName=inputFileName.replace(".aab","");
        decodeEncodeMode = "build";
        mInputFileDir = "--in=/home/lkb-l-045/workspace/LakebaSecurityFrameworkMobile/BundleDecompiler/debug/"+inputFileRawName;
        mOutputFileDir = "--out=/home/lkb-l-045/workspace/LakebaSecurityFrameworkMobile/BundleDecompiler/output/"+inputFileName;
        BundleDecompiler.doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);
    }
}

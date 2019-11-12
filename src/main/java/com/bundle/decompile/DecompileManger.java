package com.bundle.decompile;

import com.bundle.main.BundleDecompiler;
import com.bundle.resources.comman.ApplicationResources;
import com.bundle.resources.comman.FrameworkResources;
import com.bundle.smaliConversion.SmaliManager;
import com.bundle.utils.FilePaths;
import com.bundle.xmlbuilder.DecodeResXMLFiles;
import java.io.*;
import org.apache.commons.io.FileUtils;

public class DecompileManger {

    public void decompile() throws Exception {
        if(BundleDecompiler.mEnableSourceDecode) {
            decompileSmali();
        }
        if(BundleDecompiler.mEnableXmlDecode){
            decompileResources();
        }
    }


    private void decompileSmali() throws Exception {
            System.out.println("decompile Smali sources...");
            File baseApkFile=new File(FilePaths.mDebugBaseApkPath);
            File outputDir=new File(FilePaths.mDebugBaseDexDir);
            String dexFileList[]=outputDir.list();
            for(int fileCount=0;fileCount<dexFileList.length;fileCount++) {
                new SmaliManager().decodeSourcesSmali(baseApkFile, outputDir, dexFileList[fileCount], true, 15);
                File currentDexFile=new File(outputDir.getAbsolutePath()+File.separator+dexFileList[fileCount]);
                FileUtils.forceDelete(currentDexFile);

            }
    }

    private void decompileResources() throws Exception {
        System.out.println("decompile bundle Resources...");
        FrameworkResources frameworkResources=new FrameworkResources();
        frameworkResources.parseFrameworkResources();

        ApplicationResources mApplicationResources=new ApplicationResources(FilePaths.mDebugBaseApkPath,FilePaths.mDebugAppBaseDirPath);
        mApplicationResources.parseApplicationResources();

        DecodeResXMLFiles DecodeResXMLFiles = new DecodeResXMLFiles(FilePaths.mDebugBaseApkPath, FilePaths.mDebugAppBaseDirPath);
        DecodeResXMLFiles.decodeResourcesXMLFiles();
        mApplicationResources.parseValuesXmlResources();
    }
}

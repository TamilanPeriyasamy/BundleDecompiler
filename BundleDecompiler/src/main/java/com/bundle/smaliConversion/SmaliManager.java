package com.bundle.smaliConversion;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class SmaliManager {

    private static final String SMALI_DIRNAME = "smali";
    private static final String APK_DIRNAME = "/home/lakeba13/workspace/LakebaSecurityFrameworkMobile/SecurityFramework/SecurityFramework/output/";

    public void decodeSourcesSmali(File apkFile, File outDir, String filename, boolean bakdeb, int api) throws Exception {
        try {
            File smaliDir;
            if (filename.equalsIgnoreCase("classes.dex")) {
                smaliDir = new File(outDir, SMALI_DIRNAME);
            } else {
                smaliDir = new File(outDir, SMALI_DIRNAME + "_" + filename.substring(0, filename.indexOf(".")));
            }
            FileUtils.deleteDirectory(smaliDir);
            smaliDir.mkdirs();
            SmaliDecoder.decode(apkFile, smaliDir, filename, bakdeb, api);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }


    public void buildSourcesSmali(File inputBaseDexDir, File outputBuildDexDir) throws Exception {
        try {

            if(!outputBuildDexDir.exists()){
                outputBuildDexDir.mkdirs();
            }

            String smaliDirList[]=inputBaseDexDir.list();
            for (int count=0; count<smaliDirList.length; count++) {
                String smaliDirName = smaliDirList[count];
                if (smaliDirName.startsWith("smali_")) {
                    String dexFilename = smaliDirName.substring(smaliDirName.indexOf("_") + 1) + ".dex";
                    SmaliBuilder.build(new File(inputBaseDexDir, smaliDirName), new File(outputBuildDexDir, dexFilename), 0);

                }else{
                    String dexFilename ="classes.dex";
                    SmaliBuilder.build(new File(inputBaseDexDir, smaliDirName), new File(outputBuildDexDir, dexFilename), 0);

                }
            }
        } catch(Exception ex) {
            throw new Exception(ex);
        }
    }

}

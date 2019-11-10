package com.bundle.main;

import com.bundle.command.CommandExecutor;
import com.bundle.utils.FilePaths;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class Testing {

   String mTempDirPath         = null;
   String mInputFilePath       = null;
   String mTestingDirPath      = null;
   String mOutputAppBundle     = null;
   String mSignedAppBundle     = null;
   String mBundleToolJarPath   = null;
   String mKeyStorePath        = null;
   String mTestingAppDir       = null;
   String mInstallApksPath     = null;
   String mTestingApksPath     = null;
   String mInputFileName       = null;
   String mBaseApkPath         = null;


    public Testing(){
    }

    public void createTestingFiles(String mInputFilePath) throws Exception {
        mBaseApkPath        = mTestingDirPath +"/base.apk";
        mTestingApksPath    = mTestingDirPath +"/universal.apks";
        mTestingAppDir      =  mTestingDirPath +"/"+ mInputFileName;
        buildInstallApks(mInputFilePath);
       //getBaseApkTable();
    }

    public void  buildInstallApks(String appBundlePath) throws Exception {
        //java -jar ./bundletool-all.jar build-apks --bundle=./MyApp.aab --overwrite --output=./MyApp.apks
        String runCommand = "java -jar "+mBundleToolJarPath+" build-apks --bundle="+appBundlePath+" --overwrite --output="+mTestingApksPath+" --mode=universal";
        System.out.println(""+runCommand);
        new CommandExecutor().executeCommand(runCommand, true);
        unzipBuildApks(mTestingApksPath);
    }

    private void unzipBuildApks(String mInputFilePath) throws Exception {
        String runCommand = "unzip " + mInputFilePath + " -d " + mTestingDirPath;
        System.out.println("\nUnzip App Bundle\n" + runCommand);
        new CommandExecutor().executeCommand(runCommand, true);
    }

    private void getBaseApkTable() throws Exception {
        unzipAppBundle(mInputFilePath);
        getBaseApkFile( mTestingAppDir);
        createAppTable(mBaseApkPath);
    }

    private void createAppTable(String mBaseApkPath) throws Exception {
        System.out.println(""+mBaseApkPath);
        System.out.println(" "+mTestingDirPath+"/AppTable.java");
        //new CommandExecutor(new File(mBaseApkPath)).writeResourcesTable("dump -v",mTestingDirPath+"/AppTable.java");
        String tempShellScript = mTempDirPath + File.separator + "temp.sh";
        String runCommand = FilePaths.mAAPT2Path +" dump -v " +mBaseApkPath+" &> "+mTestingDirPath+"/app_table.java";
        //String runCommand = "cd " + mUnpackDirPath+"/base"+"; zip -r ../base.apk *";
        System.out.println("\nCompress base apk file \n" + runCommand);
        //new CommandExecutor().executeCommandShellScript(runCommand, tempShellScript);
        //FileUtils.forceDelete(new File(tempShellScript));

    }

    private void unzipAppBundle(String mInputFilePath) throws Exception {
        String runCommand = "unzip " + mInputFilePath + " -d "+mTestingDirPath+"/"+mInputFileName;
        System.out.println("\nUnzip App Bundle\n" + runCommand);
        new CommandExecutor().executeCommand(runCommand, false);
    }


    private String getBaseApkFile(String mUnpackDirPath) throws Exception {
        String tempShellScript = FilePaths.mTempDirPath + File.separator + "temp.sh";
        String runCommand = "cd " + mUnpackDirPath+"/base"+"; zip -r ../base.apk *";
        System.out.println("\nCompress base apk file \n" + runCommand);
        new CommandExecutor().getStreamDefaultExecutor(runCommand,  false);
        System.out.println(" "+mUnpackDirPath);
        FileUtils.copyFile(new File(mUnpackDirPath + "/base.apk"), new File(FilePaths.mDebugBaseApkPath));
        FileUtils.forceDelete(new File(mUnpackDirPath + "/base.apk"));
        FileUtils.forceDelete(new File(tempShellScript));
        return FilePaths.mDebugBaseApkPath;
    }

}

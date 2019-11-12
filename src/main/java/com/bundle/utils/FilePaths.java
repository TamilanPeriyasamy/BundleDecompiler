/**
 * 
 */
package com.bundle.utils;


import com.bundle.main.BundleDecompiler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author ${Periyasamy C}
 *
 * 08-Oct-2018
 */

public class FilePaths {

	public static String  mResName      ="res";
	public static String  mBaseName     ="base";
	public static String  apk           =".apk";
	public static String  aab           =".aab";
	public static String  dexDir        ="dex";
	public static String  mBaseApkName  ="base.apk";
	public static String  mProtoApkName ="proto.apk";
	public static String  manifestDir   ="manifest";
	public static String  buildName     ="build";
	public static String  signedName    ="singed_";
	public static String mTempName      = "tmp";
	public static String mUniversal     = "universal";

	public static String mInputFilePath         = null;
	public static String mInputFileName         = null;
	public static String mDebugDirPath          = null;
	public static String mTempDirPath           = null;
	public static String mDebugAppDirPath       = null;
	public static String mDebugApksPath         = null;
	public static String mOutputDirPath         = null;
	public static String mDebugBuildDirPath     = null;
 	public static String mDebugBaseApkPath      = null;
	public static String mDebugAppBaseDirPath   = null;
	public static String mDebugBaseResPath      = null;
	public static String mProtoApkPath          = null;
	public static String mOutputAppDir          = null;
	public static String mDebugBaseDexDir       = null;
	public static String mDebugBaseManifestPath = null;
	public static String mDebugBuildDexDir      = null;
	public static String mDebugBuildResDirPath  = null;
	public static String mOutputAppBaseDir      = null;
	public static String mOutputDexDir          = null;
	public static String mOutputAppBundle       = null;
	public static String mSignedAppBundle       = null;
	public static String mInstallApksPath       = null;
	public static String mOutputManifestDir     = null;
	public static String mOutputResDir          = null;

	public static  String sharedLinks[] = {
			"https://www.dropbox.com/s/f4yo09tdvkgs75j/aapt2",
			"https://www.dropbox.com/s/6sca4mouyjql4dh/android.jar",
			"https://www.dropbox.com/s/jkrldlbdneixeaq/libc%2B%2B.so",
			"https://www.dropbox.com/s/sv0dern26i3pike/bundletool.jar",
			"https://www.dropbox.com/s/krdnoj2ia1iea11/framework.apk",
			"https://www.dropbox.com/s/pudf2jum77mqifp/key-store.jks"};

	public static String mAndroidToolsDir       = "/tmp/BundleDecompiler";
	public static String mToolsVersionDir       = mAndroidToolsDir+"/29.0.2";
	public static String mToolsLib64Dir         = mToolsVersionDir+"/lib64";

	public static  String toolsList[] = {
			mAndroidToolsDir+"/bundletool.jar",
			mAndroidToolsDir+"/framework.apk",
			mAndroidToolsDir+"/key-store.jks",
			mToolsVersionDir+"/aapt2",
			mToolsVersionDir+"/android.jar",
			mToolsLib64Dir +"/libc++.so"};

	public static String mFrameworkPath     = mAndroidToolsDir+"/framework.apk";
	public static String mBundleToolJarPath = mAndroidToolsDir+"/bundletool.jar";
	public static String mKeyStorePath      = mAndroidToolsDir+"/key-store.jks";
	public static String mAAPT2Path         = mToolsVersionDir+"/aapt2";
	public static String mAndroidJarPath    = mToolsVersionDir+"/android.jar";

	FilePaths(){
		mInputFilePath         = null;
		mInputFileName         = null;
		mDebugDirPath          = null;
		mTempDirPath           = null;
		mDebugAppDirPath       = null;
		mDebugApksPath         = null;
		mOutputDirPath         = null;
		mDebugBuildDirPath     = null;
		mDebugBaseApkPath      = null;
		mDebugAppBaseDirPath   = null;
		mDebugBaseResPath      = null;
		mProtoApkPath          = null;
		mOutputAppDir          = null;
		mDebugBaseDexDir       = null;
		mDebugBaseManifestPath = null;
		mDebugBuildDexDir      = null;
		mDebugBuildResDirPath  = null;
		mOutputAppBaseDir      = null;
		mOutputDexDir          = null;
		mOutputAppBundle       = null;
		mSignedAppBundle       = null;
		mInstallApksPath       = null;
		mOutputManifestDir     = null;
		mOutputResDir          = null;
	}


	private static void cleanUpFileDirectory(String mTempDirPath, String mDebugAppDirPath, String mOutputAppBundle) throws IOException {

		if(!new File(mTempDirPath).exists()){
			new File(mTempDirPath).mkdirs();
		}else{
			FileUtils.cleanDirectory(new File(mTempDirPath));
		}

		if(BundleDecompiler.mBundleDecompile && new File(mDebugAppDirPath).exists()) {
			System.out.println("mBundleDecompile");
			FileUtils.deleteDirectory(new File(mDebugAppDirPath));
		}
		if(BundleDecompiler.mBundleBuild) {
			if(new File(mOutputAppBundle).exists()){
				FileUtils.forceDelete(new File(mOutputAppBundle));
			}

			if(new File(mInstallApksPath).exists()){
				FileUtils.forceDelete(new File(mInstallApksPath));
			}
		}

	}

	public static void setFilePath(String mInput_File_Dir, String mOutput_File_Dir) throws Exception {

		if(BundleDecompiler.mBundleDecompile){
			mInputFilePath    = mInput_File_Dir;
			mDebugDirPath     = mOutput_File_Dir;
			mInputFileName    = new File(mInputFilePath).getName().replace(aab, "");
			mTempDirPath      = mDebugDirPath + File.separator + mTempName;
			mDebugAppDirPath  = mDebugDirPath + File.separator + mInputFileName;
		}

		if(BundleDecompiler.mBundleBuild){
			mDebugDirPath     = mInput_File_Dir;
			mOutputAppBundle  = mOutput_File_Dir;
			mDebugAppDirPath  = mDebugDirPath;
			mInputFileName    = new File(mOutputAppBundle).getName().replace(aab, "");
			mTempDirPath      = new File(mOutputAppBundle).getParent()+File.separator+mTempName;
			mOutputDirPath    = new File(mOutputAppBundle).getParent();
			mInstallApksPath      = mOutputDirPath + File.separator +mUniversal+ apk+"s";
			mSignedAppBundle      = mOutputDirPath + File.separator + signedName +mInputFileName+ aab;
		}

		mDebugAppBaseDirPath   = mDebugAppDirPath + File.separator +mBaseName;
		mDebugBaseResPath      = mDebugAppBaseDirPath + File.separator + mResName;
		mDebugBaseManifestPath = mDebugAppBaseDirPath + File.separator+ manifestDir;
		mDebugBaseDexDir       = mDebugAppBaseDirPath + File.separator+ dexDir;
		mDebugBaseApkPath      = mTempDirPath + File.separator + mBaseApkName;
		mDebugApksPath         = mTempDirPath + File.separator + mUniversal + apk+"s";

		mDebugBuildDirPath    = mDebugAppDirPath + File.separator  + buildName;
		mDebugBuildDexDir     = mDebugBuildDirPath + File.separator +dexDir;
		mDebugBuildResDirPath = mDebugBuildDirPath + File.separator +mResName;

		mOutputAppDir         = mTempDirPath + File.separator + mInputFileName;
		mProtoApkPath         = mTempDirPath + File.separator + mProtoApkName;
		mOutputAppBaseDir     = mOutputAppDir + File.separator + mBaseName;
		mOutputDexDir         = mOutputAppBaseDir + File.separator + dexDir;
		mOutputManifestDir    = mOutputAppBaseDir + File.separator + manifestDir;
		mOutputResDir         = mOutputAppBaseDir + File.separator + mResName;
		cleanUpFileDirectory(mTempDirPath,mDebugAppDirPath,mOutputAppBundle);
	}
}

package com.bundle.main;

import com.bundle.decompile.DecompileManger;
import com.bundle.exception.EncodeDecodeException;
import com.bundle.recompile.BuildManager;
import com.bundle.signbundle.SignAppBundle;
import com.bundle.utils.FilePaths;
import com.bundle.utils.LogFileGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class BundleDecompiler {

    public static String mOutput_File_Dir      = null;
    public static String mInput_File_Dir       = null;
    public static boolean mTestingMode         = false;
    public static boolean mEnableXmlDecode     = true;
    public static boolean mEnableSourceDecode  = true;
    public static boolean mBundleDecompile     = false;
    public static boolean mBundleBuild         = false;
    public static boolean mBundleSign          = false;


    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.ARGS_MISMATCHED);
        }
        if(mTestingMode) {
            LogFileGenerator.toStartWriteLogFile();
            Testing testing = new Testing();
            testing.testBuild(args);
            LogFileGenerator.toStopWriteLogFile();
        }else {
            String decodeEncodeMode = args[0];
            String mInputFileDir    = args[1];
            String mOutputFileDir   = args[2];
            doRunBundleDecompiler(decodeEncodeMode, mInputFileDir, mOutputFileDir);
        }
    }

    public static void doRunBundleDecompiler(String decodeEncodeMode, String mInputFileDir, String mOutputFileDir) throws Exception {

        if (decodeEncodeMode.trim().equals("decompile") || decodeEncodeMode.trim().equals("d")) {
            mBundleDecompile = true;
            mBundleBuild     = false;
            mBundleSign      = false;

        } else if (decodeEncodeMode.trim().equals("build") || decodeEncodeMode.trim().equals("b")) {
            mBundleBuild     = true;
            mBundleDecompile = false;
            mBundleSign      = false;

        } else if (decodeEncodeMode.trim().equals("sign")) {
            mBundleSign      = true;
            mBundleBuild     = false;
            mBundleDecompile = false;

        } else {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }

        if (!mInputFileDir.trim().startsWith("--in=")) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }else{
            mInput_File_Dir = mInputFileDir.substring(mInputFileDir.indexOf("--in=")+5).trim();
        }

        if (!mOutputFileDir.trim().startsWith("--out=")) {
            throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
        }else {
            mOutput_File_Dir = mOutputFileDir.substring(mOutputFileDir.indexOf("--out=")+6).trim();
        }

        if (mBundleDecompile) {
            System.out.println("Input AppBundle path: "+mInput_File_Dir);
            if (!new File(mInput_File_Dir).exists() || !new File(mInput_File_Dir).isFile() || !new File(mInput_File_Dir).getName().endsWith(".aab")) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }
            String mParentDirPath = new File(mOutput_File_Dir).getParent();
            if (!new File(mParentDirPath).exists()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if (!new File(mOutput_File_Dir).exists()) {
                new File(mOutput_File_Dir).mkdirs();
            }else{
                FileUtils.cleanDirectory(new File(mOutput_File_Dir));
            }

            BundleAnalyze bundleAnalyze = new BundleAnalyze(mInput_File_Dir, mOutput_File_Dir);
            bundleAnalyze.analyze();
            DecompileManger decompiler = new DecompileManger();
            decompiler.decompile();
            FileUtils.deleteDirectory(new File(FilePaths.mTempDirPath));
        }


        if (mBundleBuild) {
            if (!new File(mInput_File_Dir).exists() || !new File(mInput_File_Dir).isDirectory()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            String mParentDirPath = new File(mOutput_File_Dir).getParent();
            if (!new File(mParentDirPath).exists()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if (!mOutput_File_Dir.endsWith(".aab")) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            System.out.println("Input Decompile dir: "+mInput_File_Dir);
            BundleAnalyze bundleAnalyze = new BundleAnalyze(mInput_File_Dir, mOutput_File_Dir);
            bundleAnalyze.analyze();
            BuildManager buildManager = new BuildManager();
            buildManager.buildAppBundle();
            FileUtils.deleteDirectory(new File(FilePaths.mTempDirPath));
        }


        if (mBundleSign) {
            if (!new File(mInput_File_Dir).isFile() || !new File(mInput_File_Dir).getName().endsWith(".aab") ) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            String mParentDirPath = new File(mOutput_File_Dir).getParent();
            if (!new File(mParentDirPath).exists()) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if (!mOutput_File_Dir.endsWith(".aab")) {
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }

            if(mInput_File_Dir.equals(mOutput_File_Dir)){
                throw new EncodeDecodeException(EncodeDecodeException.ExceptionCode.INVALID_ARGS);
            }
            SignAppBundle signAppBundle = new SignAppBundle();
            signAppBundle.signedAppBundle(mInput_File_Dir,mOutput_File_Dir);
        }
    }
}

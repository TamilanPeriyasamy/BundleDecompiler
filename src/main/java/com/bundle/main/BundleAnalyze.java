package com.bundle.main;

import com.bundle.command.CommandExecutor;
import com.bundle.resources.comman.Resources;
import com.bundle.signbundle.SignAppBundle;
import com.bundle.utils.FilePaths;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BundleAnalyze {

    public static ArrayList<String> mAppBundleFileList = new ArrayList<String>();
    public static ArrayList<String> mResorceXmlFileList = new ArrayList<>();
    public static ArrayList<String> mResorceFileList = new ArrayList<>();

    public BundleAnalyze(String mInput_File_Dir, String mOutput_File_Dir) throws Exception {
        mAppBundleFileList.clear();
        mResorceXmlFileList.clear();
        mResorceFileList.clear();
        installAndroidTools();
        FilePaths.setFilePath(mInput_File_Dir, mOutput_File_Dir);
    }

    public void analyze() throws Exception {
        if (BundleDecompiler.mBundleDecompile) {
            getBundleFileList(FilePaths.mInputFilePath);
            UnpackAppBundle(FilePaths.mInputFilePath);
            createBundleCompilerYML();
        }
        if (BundleDecompiler.mBundleBuild) {
            getBundleFileList(FilePaths.mInputFilePath);
            copyDebugBundleFiles();
        }
    }

    private void createBundleCompilerYML() throws Exception {

        ArrayList<String> dumpInputStream = new CommandExecutor(new File(FilePaths.mDebugBaseApkPath)).getApkBadging(Resources.mDumpBadging);
        File mDecompileConfig=new File(FilePaths.mDebugAppDirPath+"/"+FilePaths.mDecompileConfig);
        mDecompileConfig.createNewFile();

        FileWriter writer = new FileWriter(mDecompileConfig);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        String currentLine=null;
        bufferWriter.write("aabFileName: '"+FilePaths.mInputFileName+"'");
        bufferWriter.newLine();
        for(int count=0;count<dumpInputStream.size();count++) {
            currentLine = dumpInputStream.get(count);

            if (currentLine.trim().startsWith("package: name='") && currentLine.contains("versionCode='")  && currentLine.contains("versionName='")) {
               currentLine=currentLine.substring(currentLine.indexOf("package: name='")+9);
               String configValues[]=currentLine.split(" ");
                bufferWriter.write("package"+configValues[0].replace("name=","name: "));
                bufferWriter.newLine();//versionInfo:
                bufferWriter.write("versionInfo:");
                bufferWriter.newLine();
                bufferWriter.write("  "+configValues[1].replace("versionCode=","versionCode: "));
                bufferWriter.newLine();
                bufferWriter.write("  "+configValues[2].replace("versionName=","versionName: "));
                bufferWriter.newLine();
            }

            if (currentLine.trim().startsWith("sdkVersion:")){
                bufferWriter.write("sdkInfo:  ");
                bufferWriter.newLine();
                bufferWriter.write("  "+currentLine.replace("sdkVersion:","minSdkVersion: "));
                bufferWriter.newLine();
            }
            if (currentLine.trim().startsWith("targetSdkVersion:")){
                bufferWriter.write("  "+currentLine.replace("targetSdkVersion:","targetSdkVersion: "));
                bufferWriter.newLine();
            }

        }
		bufferWriter.close();
		writer.close();
    }

    private void copyDebugBundleFiles() throws IOException {
        File sourceDir = new File(FilePaths.mDebugDirPath);
        File designationDir = new File(FilePaths.mOutputAppDir);
        FileUtils.copyDirectory(sourceDir, designationDir);
        if (BundleDecompiler.mEnableXmlDecode) {
            FileUtils.forceDelete(new File(FilePaths.mOutputManifestDir + File.separator + "AndroidManifest.xml"));
            FileUtils.forceDelete(new File(FilePaths.mOutputAppBaseDir + File.separator + "resources.pb"));
            FileUtils.cleanDirectory(new File(FilePaths.mOutputResDir));
        }

        if (BundleDecompiler.mEnableSourceDecode) {
            FileUtils.cleanDirectory(new File(FilePaths.mOutputDexDir));
        }
    }


    private void getBundleFileList(String mInputFilePath) throws IOException {
        if (BundleDecompiler.mBundleDecompile) {
            ZipFile zipFile = new ZipFile(mInputFilePath);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                BundleAnalyze.mAppBundleFileList.add(entry.getName());
                if (entry.getName().endsWith(".xml")) {
                    BundleAnalyze.mResorceXmlFileList.add(entry.getName());
                }
                if (entry.getName().startsWith("base/res/")) {
                    BundleAnalyze.mResorceFileList.add(entry.getName());
                }
            }
            zipFile.close();
        } else {
            addedXMLFilesToList(FilePaths.mDebugDirPath);
        }
    }


    public static void addedXMLFilesToList(String mUnPackDirPath) throws IOException {
        File inputPath = new File(mUnPackDirPath);
        if (inputPath.isDirectory()) {

            File[] listofFiles = inputPath.listFiles();
            for (File file : listofFiles) {
                if (file.isFile() && (file.getAbsolutePath().endsWith(".xml"))) {
                    if (file.getAbsolutePath().contains("/base/")) {
                        String xmlPath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("/base/") + 1);
                        BundleAnalyze.mResorceXmlFileList.add(xmlPath);
                    }
                    BundleAnalyze.mResorceFileList .add(file.getAbsolutePath());
                } else {
                    addedXMLFilesToList(file.getAbsolutePath());
                }
            }
        }
    }

    private void UnpackAppBundle(String mInputFilePath) throws Exception {
        unzipAppBundle(mInputFilePath);
        getBaseApkFile(FilePaths.mDebugAppBaseDirPath);
    }


    private String getBaseApkFile(String mBaseDirPath) throws Exception {
        //copy files for apk format
        File sourceManifestFile = new File(FilePaths.mDebugBaseManifestPath + File.separator + "AndroidManifest.xml");
        FileUtils.copyFile(sourceManifestFile, new File(mBaseDirPath + File.separator + "AndroidManifest.xml"));
        String classesDexList[] = new File(FilePaths.mDebugBaseDexDir).list();
        for (int count = 0; count < classesDexList.length; count++) {
            File sourceDexFile = new File(FilePaths.mDebugBaseDexDir + File.separator + classesDexList[count]);
            FileUtils.copyFile(sourceDexFile, new File(mBaseDirPath + File.separator + classesDexList[count]));
        }

        String runCommand = "cd " + mBaseDirPath + "; zip -r ../base.apk *";
        new CommandExecutor().getStreamDefaultExecutor(runCommand, false);
        FileUtils.copyFile(new File(FilePaths.mDebugAppDirPath + "/base.apk"), new File(FilePaths.mDebugBaseApkPath));
        FileUtils.forceDelete(new File(FilePaths.mDebugAppDirPath + "/base.apk"));

        String tempFileList[] = new File(FilePaths.mDebugAppBaseDirPath).list();

        for (int count = 0; count < tempFileList.length; count++) {
            File currentFile = new File(FilePaths.mDebugAppBaseDirPath + File.separator + tempFileList[count]);
            if (tempFileList[count].endsWith(".dex") || tempFileList[count].endsWith("AndroidManifest.xml")) {
                FileUtils.forceDelete(currentFile);
                ;
            }
        }
        return FilePaths.mDebugBaseApkPath;
    }

    private void unzipAppBundle(String mInputFilePath) throws Exception {
        if (new File(FilePaths.mDebugAppDirPath).exists()) {
            FileUtils.deleteDirectory(new File(FilePaths.mDebugAppDirPath));
        }
        String runCommand = "unzip " + mInputFilePath + " -d " + FilePaths.mDebugAppDirPath;
        new CommandExecutor().executeCommand(runCommand, false);
    }

    public String downloadFile(String link, String filename) throws IOException, FileNotFoundException, URISyntaxException {
        System.out.println("filename " + filename);
        URL url = new URL(link);
        //URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        //url = uri.toURL();
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Server returned HTTP " +
                        connection.getResponseCode() + " " + connection.getResponseMessage() + "for URL:" + url);
                throw new FileNotFoundException();
            }
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(filename);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (fileLength > 0) {
                    if (total <= count) {
                        System.out.println("Download progress: " + (int) (total * 100 / fileLength) + "%");
                    } else {
                        System.out.println("Download progress: " + (int) (total * 100 / fileLength) + "%");
                    }
                }
                output.write(data, 0, count);
            }
        } catch (Exception exception) {
            System.out.println("error occured while downloading .." + exception);
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            System.out.println("exceptionAsString " + exceptionAsString);
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return filename;
    }


    public void installAndroidTools() throws Exception {

        boolean alreadyInstalled = true;
        for (int fileCount = 0; fileCount < FilePaths.toolsList.length; fileCount++) {
            if (!new File(FilePaths.toolsList[fileCount]).exists()) {
                alreadyInstalled = false;
                break;
            }
        }

        if (!alreadyInstalled) {
            System.out.println("tools installing..,"+FilePaths.mAndroidToolsDir);
            if(new File(FilePaths.mAndroidToolsDir).exists()){
                FileUtils.cleanDirectory(new File(FilePaths.mAndroidToolsDir));
            }else{
                new File(FilePaths.mAndroidToolsDir).mkdir();
            }
            for (int linkCount = 0; linkCount < FilePaths.sharedLinks.length; linkCount++) {
                String sharedLink = FilePaths.sharedLinks[linkCount];
                System.out.println(sharedLink.substring(sharedLink.lastIndexOf("/")+1)+"...,");
                if (sharedLink.endsWith("/aapt2") || sharedLink.endsWith("/android.jar")) {
                    String command = "wget "+sharedLink+" --directory-prefix="+FilePaths.mToolsVersionDir ;
                    new CommandExecutor().executeCommand(command);

                } else if (sharedLink.endsWith("/libc%2B%2B.so")) {
                    String command = "wget "+sharedLink+" --directory-prefix="+FilePaths.mToolsLib64Dir ;
                    new CommandExecutor().executeCommand(command);

                } else {
                    String command = "wget "+sharedLink+" --directory-prefix="+FilePaths.mAndroidToolsDir ;
                    new CommandExecutor().executeCommand(command);

                }
            }
            for (int fileCount = 0; fileCount < FilePaths.toolsList.length; fileCount++) {
                if (new File(FilePaths.toolsList[fileCount]).exists()) {
                    new File(FilePaths.toolsList[fileCount]).setReadable(true);
                    new File(FilePaths.toolsList[fileCount]).setWritable(true);
                    new File(FilePaths.toolsList[fileCount]).setExecutable(true);
                }
            }
        }
    }
}

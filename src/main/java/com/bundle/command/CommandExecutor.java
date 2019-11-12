package com.bundle.command;

import com.bundle.utils.FilePaths;

import java.io.*;
import java.util.ArrayList;

import java.io.IOException;

import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;

import static java.lang.System.out;

public class CommandExecutor {

    File mBaseApkFile       = null;
    String mTempShellScript = "/tmp/temp.sh";

    public CommandExecutor() {
    }

    public CommandExecutor(File apkfile) {
        mBaseApkFile = apkfile;
    }

    public boolean getStreamDefaultExecutor(String runCommand, boolean printLog) throws Exception {

        FileWriter writer = new FileWriter(mTempShellScript, false);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        bufferWriter.write(runCommand);
        bufferWriter.newLine();
        bufferWriter.close();
        writer.close();

        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec("sh " + mTempShellScript);
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String infoLine = null;
        while ((infoLine = inputStreamReader.readLine()) != null) {
            if (printLog) {
                out.println(infoLine);
            }
        }
        inputStreamReader.close();
        BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine = null;
        while ((errorLine = errorStreamReader.readLine()) != null) {
            out.println(errorLine);
        }
        errorStreamReader.close();
        process.waitFor();
        process.destroy();
        final int exitValue = process.waitFor();
        process.destroy();
        if (exitValue == 0) {
            return true;
        }
        FileUtils.forceDelete(new File(mTempShellScript));
        return false;
    }

    public boolean executeCommand(String runCommand) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runCommand);
       /*BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String infoLine = null;
        while ((infoLine = inputStreamReader.readLine()) != null) {
                out.println(infoLine);
        }
        inputStreamReader.close();
        BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine = null;
        while ((errorLine = errorStreamReader.readLine()) != null) {
            out.println(errorLine);
        }
        errorStreamReader.close();*/
        process.waitFor();
        process.destroy();
        final int exitValue = process.waitFor();
        process.destroy();
        if (exitValue == 0) {
            return true;
        }
        return false;
    }

    public boolean executeCommand(String runCommand, boolean printLog) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runCommand);
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String infoLine = null;
        while ((infoLine = inputStreamReader.readLine()) != null) {
            if (printLog) {
                out.println(infoLine);
            }
        }
        inputStreamReader.close();
        BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine = null;
        while ((errorLine = errorStreamReader.readLine()) != null) {
                out.println(errorLine);
        }
        errorStreamReader.close();
        process.waitFor();
        process.destroy();
        final int exitValue = process.waitFor();
        process.destroy();
        if (exitValue == 0) {
            return true;
        }
        return false;
    }

    public boolean executeCommand(String[] runcommands, boolean printLog) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runcommands);

        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String infoLine = null;
        while ((infoLine = inputStreamReader.readLine()) != null) {
            if (printLog) {
                out.println(infoLine);
            }
        }
        inputStreamReader.close();

        BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine = null;
        while ((errorLine = errorStreamReader.readLine()) != null) {
            System.err.println(errorLine);
        }
        errorStreamReader.close();
        process.waitFor();
        process.destroy();
        final int exitValue = process.waitFor();
        process.destroy();
        if (exitValue == 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getInputStream(String runCommand) throws IOException, InterruptedException {
        ArrayList<String> inputStream = new ArrayList<String>();
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runCommand);
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = inputStreamReader.readLine()) != null) {
            inputStream.add(line);
        }
        process.waitFor();
        process.destroy();
        inputStreamReader.close();
        return inputStream;
    }

    public ArrayList<String> getInputStream(String command, File mApkFile) throws IOException, InterruptedException {
        ArrayList<String> inputStream = new ArrayList<String>();
        String runCommand = FilePaths.mAAPT2Path + " " + command + " " + mApkFile.getAbsolutePath();
        out.println("" + runCommand);
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runCommand);
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = inputStreamReader.readLine()) != null) {
            System.out.println("" + line);
            inputStream.add(line);
        }
        process.waitFor();
        process.destroy();
        inputStreamReader.close();
        final int exitValue = process.waitFor();
        process.destroy();
        if (exitValue == 0) {
            System.err.println("Failed...,");
        }
        return inputStream;
    }


    public ArrayList<String> getStreamDefaultExecutor(String command, File mApkFile) throws IOException, InterruptedException {
        String mTempFile = FilePaths.mTempDirPath + File.separator + "temp.java";
        ArrayList<String> inputStream = new ArrayList<String>();
        String runCommand = FilePaths.mAAPT2Path + " " + command + " " + mApkFile.getAbsolutePath();
        CommandLine commandLine = CommandLine.parse(runCommand);
        OutputStream out = new FileOutputStream(new File(mTempFile));
        ExecuteStreamHandler stream = new PumpStreamHandler(out, out, null);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setStreamHandler(stream);
        int exitValue = exec.execute(commandLine);
        if (exitValue == 0) {
            FileReader reader = new FileReader(mTempFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                inputStream.add(line);
            }
            bufferedReader.close();
            reader.close();
        } else {
            System.err.println("Build failed...,");
        }
        return inputStream;
    }

    private ArrayList<String> getInputStream(String command1, String resName, File mBaseApkFile) throws IOException, InterruptedException {
        ArrayList<String> inputStream = new ArrayList<String>();
        String runCommand = FilePaths.mAAPT2Path+ " "+command1+" "+mBaseApkFile.getAbsolutePath()+" --file "+resName;
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(runCommand);
        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = inputStreamReader.readLine()) != null) {
            inputStream.add(line);
        }
        process.waitFor();
        process.destroy();
        inputStreamReader.close();
        return inputStream;
    }

    public ArrayList<String> getXmlTree(String command1, String command2) throws IOException, InterruptedException {
        return getInputStream(command1, command2, mBaseApkFile);
    }

    public ArrayList<String> getResourcesTable(String command) throws IOException, InterruptedException {
        return getStreamDefaultExecutor(command, mBaseApkFile);
    }

    public ArrayList<String> getStringsValues(String command) throws IOException, InterruptedException {
        return getInputStream(command);
    }

    public ArrayList<String> getAppConfig(String command) throws IOException, InterruptedException {
        return getInputStream(command);
    }
}

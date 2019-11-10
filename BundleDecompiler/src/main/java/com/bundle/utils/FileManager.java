package com.bundle.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileManager {

	public static void copyFileUsingStream(File source, File dest) throws IOException {

		if(!source.exists()) { 
			System.err.println("Source file does not exists "+source.getAbsolutePath());
		}

		if(source.getAbsolutePath().equals(dest.getAbsolutePath())){
			System.err.println("Source and dest file are same...!!");
			System.err.println("Source "+source.getAbsolutePath());
			System.err.println("dest "+dest.getAbsolutePath());
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			//System.out.println("Source "+source.getAbsolutePath());
			//System.out.println("dest   "+dest.getAbsolutePath());
		} finally {
			is.close();
			os.close();
		}
	}

	public static ArrayList<String> getFileList(File folder,ArrayList<String> files) {
		if(files == null){
			files = new ArrayList<String>();
		}
		File[] listofFiles = folder.listFiles();
		if (listofFiles != null) {
			for (File file : listofFiles) {
				if (file.isFile()) {

					files.add(file.getAbsolutePath().toString());

				} else
					getFileList(file,files);

			}
		}
		return files;
	}

	public static String getRelativePath(String sourceDir, File file) {
		// Trim off the start of source dir path...
		String path = file.getPath().substring(sourceDir.length());
		if (path.startsWith(File.pathSeparator)) {
			path = path.substring(1);
		}
		return path;
	}


	public String readFile(String path) throws IOException
	{
		String everything = null;
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
		}
		return everything;
	}


	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName,MessageListener listener) throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + File.separator + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			listener.onNewMessage("+" + zipEntryName);
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName,listener);
			}
		} else {
			listener.onNewMessage("   " + zipEntryName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}

	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		//System.out.println("deleting file "+f.getAbsolutePath());
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	public interface MessageListener{
		void onNewMessage(String msg);
	}

	/**
	 * This function copies enter folder to another folder
	 * @param sourceFolder - Location of the directory to be copied
	 * @param destinationFolder - Destination location of the directory to copy source folder.
	 * @throws IOException when source folder is not found or unable to create destination folder
	 */
	public static void copyDirectory(String sourceFolder, String destinationFolder) throws IOException{
		File source = new File(sourceFolder);
		if(!source.exists()){
			throw new IOException("Source folder doesn't exists");
		}
		File desc = new File(destinationFolder);
		if(!desc.exists()){
			boolean created = desc.mkdirs();
			if(!created){
				throw new IOException("Unable to create destination folder");
			}
		}
		FileUtils.copyDirectory(source, desc);	
	}

	public static void extractingZipFile(String zipFilePath,String destinationFolder) throws Exception {
		String zipPath = destinationFolder;
		File temp = new File(zipPath);
		temp.mkdir();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath);
			// get an enumeration of the ZIP file entries
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				File destinationPath = new File(zipPath, entry.getName());
				//create parent directories
				destinationPath.getParentFile().mkdirs();
				// if the entry is a file extract it
				if (entry.isDirectory()) {
					continue;
				}else {
					//System.out.println("Extracting file: " + destinationPath);
					BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(entry));
					int b;
					byte buffer[] = new byte[1024];
					FileOutputStream fileOutputStream = new FileOutputStream(destinationPath);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);

					while ((b = bufferedInputStream.read(buffer, 0, 1024)) != -1) {

						bufferedOutputStream.write(buffer, 0, b);
					}
					bufferedOutputStream.close();
					bufferedInputStream.close();
				}
			}
		}catch (IOException ioe) {
			System.out.println("Error opening zip file" + ioe);
		}finally {
			try {
				if (zipFile!=null) {
					zipFile.close();
				}
			}catch (IOException ioe) {
				System.out.println("Error while closing zip file" + ioe);
			}
		}
	}
	
	public  void zipDir(String apkBuildDir,String mOutPutFilePath) throws Exception{
		String sourcedir = apkBuildDir;
		String zipFile = mOutPutFilePath;
		compressDirectory(sourcedir, zipFile);
	}
	
	public List<String> mFileList = new ArrayList<>();
	public void compressDirectory(String sourceFolder, String zipFilePath) {
		File directory = new File(sourceFolder);
		getFileList(directory);
		try {
			FileOutputStream fileoutputstream  = new FileOutputStream(zipFilePath);
			ZipOutputStream zipoutputstream = new ZipOutputStream(fileoutputstream);
			for (String filePath : mFileList) {
				// Creates a zip entry.
				String name = filePath.substring(directory.getAbsolutePath().length() + 1,filePath.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zipoutputstream.putNextEntry(zipEntry);

				// Read file content and write to zip output stream.
				FileInputStream fileInputStream = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = fileInputStream.read(buffer)) > 0) {
					zipoutputstream.write(buffer, 0, length);
				}
				// Close the zip entry and the file input stream.
				zipoutputstream.closeEntry();
				fileInputStream.close();
			}
			// Close zip output stream and file output stream. This will
			// complete the compression process.
			zipoutputstream.close();
			fileoutputstream.close();
		} catch (IOException zipexception) {
			zipexception.printStackTrace();
		}
	}
	
	private void getFileList(File directory) {
		File[] files = directory.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					mFileList.add(file.getAbsolutePath());
				}else {
					getFileList(file);
				}
			}
		}
	}

	public  static Object[] getFileList(String rootDirPath){
		//Object[] fileList=FileUtils.listFiles(new File(rootDirPath), null, true).toArray();

	/*	for(int count=0;count<fileList.length;count++){
			String file=new String(fileList[count].toString());
			System.out.println("dsdfssss "+file);
		}*/

		return FileUtils.listFiles(new File(rootDirPath), null, true).toArray();
	}
}


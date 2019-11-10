package com.bundle.xmlbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bundle.resources.comman.Resources;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bundle.command.CommandExecutor;

public class DecodeResXMLFiles {

	File mBaseApkFile =null;
	File mAppkBuildDirPath =null;

	public static String  mCurrentXmlName=null;
	public DecodeResXMLFiles(String baseApkPath,String appBuildDirPath) {
		mBaseApkFile =new File(baseApkPath);
		mAppkBuildDirPath =new File(appBuildDirPath);
	}

	public void decodeResourcesXMLFiles() throws Exception {
		System.out.println("decode AndroidManifest.xml...");
		ZipFile zipFile = new ZipFile(mBaseApkFile.getAbsolutePath());
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		while(enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			if(!zipEntry.isDirectory()){ 
				if(zipEntry.getName().equals("manifest/AndroidManifest.xml")){
					decodeResourcesXmlFiles(zipEntry.getName(), mAppkBuildDirPath);
				}
			}
		} 
		zipFile.close();

		System.out.println("decode resources xmls...");
		zipFile = new ZipFile(mBaseApkFile.getAbsolutePath());
		enumeration = zipFile.entries();
		while(enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
			if(!zipEntry.isDirectory()){
				if(zipEntry.getName().startsWith("res/") && zipEntry.getName().endsWith(".xml") ){
				//if(zipEntry.getName().startsWith("res/layout/activity_main.xml") ){
					decodeResourcesXmlFiles(zipEntry.getName(), mAppkBuildDirPath);
				}
			}
		} 
		zipFile.close();
	}

	private void decodeResourcesXmlFiles(String zipEntryName, File appBaseDir) throws Exception {
		mCurrentXmlName=zipEntryName;
		//System.out.println(""+zipEntryName);
		HashMap<Integer,Element> mElementList = new HashMap<Integer,Element>();
		ArrayList<String> mXmlnsLinks=new ArrayList<String>();
		ArrayList<String> inputStream=new CommandExecutor(mBaseApkFile).getXmlTree(Resources.mDumpXmltree,zipEntryName);
		XMLBuilder XMLBuilder=new XMLBuilder();
		Document document =XMLBuilder.newDocument();

		Element mRootElement    = null;
		Element	mNewElement     = null;
		int mCurrentIndent   = 0;
		String currentline      = null;
		for(int lineCount=0;lineCount<inputStream.size();lineCount++) {
			currentline =inputStream.get(lineCount);
			//System.out.println(""+currentline);

			if(currentline.contains("E: uses-sdk ") || currentline.contains("android:minSdkVersion(") || currentline.contains("android:targetSdkVersion(") || currentline.contains("android:versionCode(") || currentline.contains("android:versionName(") ) {
				//continue;
			}

			if(currentline.trim().startsWith("N:")) {
				/** N: android=http://schemas.android.com/apk/res/android  (line=*/
				String xmlnsLink=currentline.substring(currentline.indexOf("N:")+2, currentline.indexOf(" (line=")).trim();
				//String xmlnsLink=currentline.replace("N: ","xmlns:").trim();
				mXmlnsLinks.add(xmlnsLink);
				//System.out.println(""+xmlnsLink);

			}else if(currentline.trim().startsWith("E:")) {
				String mTagName = null;
				if(mRootElement==null) {
					mCurrentIndent=currentline.indexOf("E:");
					mTagName=currentline.substring(currentline.indexOf("E:")+2, currentline.indexOf(" (line=")).trim();
					mRootElement= mNewElement = document.createElement(mTagName);
					if(mXmlnsLinks.size()!=0) {
						//System.out.println("mXmlnsLinks "+mXmlnsLinks);
						XMLBuilder.setXmlnskAttributes(mNewElement,mXmlnsLinks);
					}
					document.appendChild(mNewElement);
					mElementList.put(new Integer(mCurrentIndent), mNewElement);

				}else {
					mCurrentIndent=currentline.indexOf("E:");
					mTagName=currentline.substring(currentline.indexOf("E:")+2, currentline.indexOf(" (line=")).trim();
					mNewElement = document.createElement(mTagName);
					Element mPreElement= getParentElement(mElementList,mCurrentIndent);
					if(mXmlnsLinks.size()!=0) {
						XMLBuilder.setXmlnskAttributes(mNewElement,mXmlnsLinks);
					}
					mPreElement.appendChild(mNewElement);
					mElementList.put(new Integer(mCurrentIndent), mNewElement);
				}
				mXmlnsLinks.clear();

			}else if(currentline.trim().startsWith("C:")) {
				if(document!=null && mNewElement!=null) {
					Comment comment = document.createComment(currentline.trim());
					mNewElement.getParentNode().insertBefore(comment, mNewElement);
				}

			}else if(currentline.trim().startsWith("A:") ) {
				XMLBuilder.setAttributes(mNewElement,currentline.replace("A:", "").trim());

			}else {
				if(!currentline.trim().startsWith("T: '") && !currentline.trim().startsWith("") && !currentline.trim().startsWith("'") ){
					System.err.println("Missing Element " + currentline.trim() + " for " + mCurrentXmlName);
				}
			}
		}
		XMLBuilder.createXMLFile(document,new File(appBaseDir+File.separator+zipEntryName),false);
	}

	private Element getParentElement(HashMap<Integer, Element> mElementList, int mPreElementIndent) {
		int indentValue=mPreElementIndent-2;
		while(mElementList.get(indentValue)==null) {
			indentValue=indentValue-2;
		}
		return mElementList.get(indentValue);
	}
}

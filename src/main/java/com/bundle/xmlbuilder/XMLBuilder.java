package com.bundle.xmlbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.bundle.resources.comman.ApplicationResources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bundle.conversion.Constant;
import com.bundle.conversion.Convert;

public class XMLBuilder {

	public XMLBuilder(){	
	}

	public void createXMLFile(Document document, File xmlFile,boolean printSource) throws TransformerException, IOException{

		if(xmlFile.exists()) {
			xmlFile.delete();
		}
		if(printSource) {
			//System.out.println(xmlFile+":");
		}
		
		document.setXmlStandalone(true);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Properties outFormat = new Properties();
		outFormat.setProperty(OutputKeys.INDENT, "yes");
		outFormat.setProperty(OutputKeys.METHOD, "xml");
		outFormat.setProperty(OutputKeys.DOCTYPE_PUBLIC,"no");
		outFormat.setProperty(OutputKeys.VERSION, "1.0");
		outFormat.setProperty(OutputKeys.ENCODING, "utf-8");
		outFormat.setProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperties(outFormat);
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
		
		/*DOMSource source = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Properties outFormat = new Properties();
		outFormat.setProperty(OutputKeys.INDENT, "yes");
		outFormat.setProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperties(outFormat);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);*/

		/*TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource =new DOMSource(document.getDocumentElement());
		OutputStream output = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(output);
		transformer.transform(domSource, result);
		String xmlString = output.toString();
		FileWriter writer = new FileWriter(xmlFile);  
		BufferedWriter bufferWriter = new BufferedWriter(writer);
		bufferWriter.write(xmlString);
		bufferWriter.close();
		writer.close();
		if(printSource) {
			System.out.println(""+xmlString);
		}*/
	}

	public Document newDocument() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document newDocument=documentBuilder.newDocument();
		return newDocument;
	}

	public void setXmlnskAttributes(Element mElement, ArrayList<String> mXmlnsLinks) {

		for(int count=0;count<mXmlnsLinks.size();count++) {
			String currentLink=mXmlnsLinks.get(count);
			if(currentLink!="" && currentLink!=null) {
				//[android=http://schemas.android.com/apk/res/android, app=http://schemas.android.com/apk/res-auto]
				String xmlnsName="xmlns:"+currentLink.substring(0, currentLink.indexOf("="));
				String xmlnsValue=currentLink.substring(currentLink.indexOf("=")+1);
				//System.out.println(xmlnsName+"@@@@ "+xmlnsValue);
				mElement.setAttribute(xmlnsName.trim(), xmlnsValue.trim());
			}
		}
	}

	public HashMap<Integer, String> parseXmlnsLinks(String currentline, HashMap<Integer, String> mAppXmlnsLinksMap) {
		int elementIndent=currentline.indexOf("N:");
		String xmlnsLink=currentline.replace("N: ","xmlns:");
		if(mAppXmlnsLinksMap.get(elementIndent)==null) {
			mAppXmlnsLinksMap.put(elementIndent, xmlnsLink);
		}else {
			String xmlnsValue=mAppXmlnsLinksMap.get(elementIndent);	
			xmlnsValue=xmlnsValue+","+xmlnsLink;
		}
		return mAppXmlnsLinksMap;
	}

	private void setAttribute(Element mCurrentElement, String currentTag, String attributeTag, String attributeValue, boolean printLog) {
		if(printLog) {
			System.out.println("A: "+currentTag);
			System.out.println(attributeTag+"="+attributeValue+"\n");
		}
		mCurrentElement.setAttribute(attributeTag.trim(), attributeValue.trim());
	}

	public void setAttributes(Element mCurrentElement,String currentTag) {

		//System.out.println("aaaa "+currentTag);
		if(currentTag.trim().startsWith("http://")) {
			//System.out.println(" "+currentTag);
			String xmlnsName=null;
			String xmlnsLink=null;
			/***  A: http://schemas.android.com/apk/res/android:interpolator(0x01010141)=@0x10a0006 */
			if(currentTag.contains("/android:")) {
				//System.out.println(" "+currentTag);
				xmlnsName="xmlns:android";
				xmlnsLink=currentTag.substring(0,currentTag.indexOf("/android:")+8);
				currentTag=currentTag.substring(currentTag.indexOf("/android:")+1);
			}
			/** A: http://schemas.android.com/apk/res-auto:alpha(0x7f020027)=?0x1010033 */
			if(currentTag.contains("/res-auto:")) {
				//System.out.println(" "+currentTag);
				xmlnsName="xmlns:app";
				xmlnsLink=currentTag.substring(0,currentTag.indexOf("/res-auto:")+9);
				currentTag="app:"+currentTag.substring(currentTag.indexOf("/res-auto:")+10);
			}

			// A: http://schemas.android.com/apk/lib/com.google.android.gms.plus:annotation="inline" (Raw: "inline")
			if(currentTag.contains("/com.google.android.gms.plus:")) {
				//System.out.println(" "+currentTag);
				xmlnsName="xmlns:plus";
				xmlnsLink=currentTag.substring(0,currentTag.indexOf(".plus:")+5);
				currentTag=currentTag.substring(currentTag.indexOf(".plus:")+1);
			}

           //A: http://schemas.android.com/apk/res/com.voicepro:maxValue(0x7f040220)=10 (Raw: "10")
			if(currentTag.contains("/"+ ApplicationResources.mAppPackageName +":")) {
				//System.out.println(" "+currentTag);
				xmlnsName="xmlns:example";
				xmlnsLink=currentTag.substring(0,currentTag.indexOf(":")+1);
				currentTag=currentTag.substring(currentTag.indexOf(ApplicationResources.mAppPackageName+":"));
				currentTag=currentTag.replace(ApplicationResources.mAppPackageName +":","example:");
				//System.out.println(""+xmlnsName);
				//System.out.println(""+currentTag);
			}
			//System.out.println(" "+xmlnsName);
			//System.out.println(" "+xmlnsLink);
			//System.out.println(" "+currentTag);
			mCurrentElement.setAttribute(xmlnsName.trim(),xmlnsLink.trim());
		}

		if(currentTag.contains("=") && currentTag.contains(" (Raw: \"")) {
			/** A: android:versionName(0x0101021c)="1.0" (Raw: "1.0") 
			    A: platformBuildVersionName="6.0-2704002" (Raw: "6.0-2704002")
			    A: style=@0x7f090091 (Raw: "@style/Widget.AppCompat.NotificationActionContainer") */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("=")).trim();	
			if(attributeTag.contains("(0x")) {
				attributeTag=attributeTag.substring(0, attributeTag.indexOf("(0x")).trim();
			}
			String attributeValue=currentTag.substring(currentTag.indexOf(" (Raw: \"")+8,currentTag.indexOf("\")")).trim();
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);


		}else if(currentTag.endsWith(")=@"+Constant.NULL) || currentTag.endsWith(")=@"+Constant.NULL1)) {
			/**   A: android:background(0x010100d4)=@0x0 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String attributeValue=currentTag.substring(currentTag.indexOf(")=@0x")+3).trim();
			attributeValue="@null";
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if( currentTag.endsWith(")=?"+Constant.NULL) || currentTag.endsWith(")=?"+Constant.NULL1)) {
			/**   A: android:background(0x010100d4)=?0x0 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String attributeValue=currentTag.substring(currentTag.indexOf(")=?0x")+3).trim();
			attributeValue="?null";
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if(currentTag.contains("(0x") && currentTag.contains(")=@0x")) {
			/**  A: android:label(0x01010001)=@0x7f030000 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String attributeValue=currentTag.substring(currentTag.indexOf(")=@0x")+3).trim();
			String hexaValue=Convert.getHexFormat(attributeValue);
			//attributeValue="@"+ResourcesManager.getResValues(hexaValue);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(currentTag.contains("(0x") && currentTag.contains(")=?0x")) {
			/** A: android:color(0x010101a5)=?0x7f010084
			    A: android:color(0x010101a5)=?0x1010038 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String attributeValue=currentTag.substring(currentTag.indexOf(")=?0x")+3).trim();
			String hexaValue=Convert.getHexFormat(attributeValue);
			//attributeValue="?"+ ResourcesManager.getResValues(hexaValue);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(!currentTag.contains("(0x")  && currentTag.contains("=@0x")) {
			/** A: layout=@0x7f090024  */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("="));
			String attributeValue=currentTag.substring(currentTag.indexOf("=@0x")+2).trim();
			String hexaValue=Convert.getHexFormat(attributeValue);
			//attributeValue="@"+ResourcesManager.getResValues(hexaValue);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(!currentTag.contains("(0x")  && currentTag.contains("=?0x")) {
			/** A: style=?0x7f0200ab */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("="));
			String attributeValue=currentTag.substring(currentTag.indexOf("=?0x")+2).trim();
			String hexaValue=Convert.getHexFormat(attributeValue);
			//attributeValue="?"+ResourcesManager.getResValues(hexaValue);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if(currentTag.contains("=(type "+Constant.INT_BOOLEAN+")0x")) {
			/** A: android:debuggable(0x0101000f)=(type 0x12)0xffffffff */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String hexaDecimal=currentTag.substring(currentTag.indexOf(")0x")+1); 
			String attributeValue=""+Convert.hex2Boolean(hexaDecimal);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(currentTag.contains("=(type "+Constant.FRACTION+")0x") || currentTag.contains("=(type "+Constant.FRACTION1+")0x")) {
			/** A: android:angle(0x010101a0)=(type 0x6)0x43870000 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String hexaDecimal=currentTag.substring(currentTag.indexOf(")0x")+1); 
			String attributeValue=""+Convert.hex2Fraction(hexaDecimal);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(currentTag.contains("=(type "+Constant.FLOAT+")0x") || currentTag.contains("=(type "+Constant.FLOAT1+")0x")) {
			/** A: android:angle(0x010101a0)=(type 0x4)0x43870000 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String hexaDecimal=currentTag.substring(currentTag.indexOf(")0x")+1); 
			String attributeValue=""+Convert.hex2Float(hexaDecimal);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if(currentTag.contains("=(type "+Constant.DIMENSION+")0x") || currentTag.contains("=(type "+Constant.DIMENSION1+")0x") ) {
			/** A: android:angle(0x010101a0)=(type 0x4)0x43870000 */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String hexaDecimal=currentTag.substring(currentTag.indexOf(")0x")+1); 
			String attributeValue=Convert.hex2Dimension(hexaDecimal);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if(currentTag.contains("=(type "+Constant.INT_COLOR_ARGB8+")0x")   || currentTag.contains("=(type "+Constant.INT_COLOR_RGB8+")0x")
				|| currentTag.contains("=(type "+Constant.INT_COLOR_ARGB4+")0x") || currentTag.contains("=(type "+Constant.INT_COLOR_RGB4+")0x")){
			/** A: android:angle(0x010101a0)=(type 0x1c)0x43870000
			    A: android:color(0x010101a5)=(type 0x1d)0xffff0000
			    A: android:background(0x010100d4)=(type 0x1e)0x0
			    A: android:textColor(0x01010098)=(type 0x1f)0xffcccccc  */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String hexaDecimal=currentTag.substring(currentTag.indexOf(")0x")+1); 
			String attributeValue=Convert.hex2Color(hexaDecimal);
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	

		}else if(currentTag.contains("=(type "+Constant.INT_HEX+")0x") ) {
			/** A: android:layout_height(0x010100f5)=(type 0x10)0xfffffffe */
			String attributeValue="";
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String mRawType=currentTag.substring(0, currentTag.indexOf("=(type 0x"));
			String hexaValue=currentTag.substring(currentTag.lastIndexOf(")0x")+1);
			//attributeValue=ResourcesManager.getResValues(mRawType, hexaValue);
			if( attributeValue==null) {
			//	attributeValue=ResourcesManager.getResFlagValues(mRawType, hexaValue);
				if(attributeValue==null){
					attributeValue=""+Convert.getHexFormat(hexaValue);
				}
			} 
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);	
			
		}else if(currentTag.contains("=(type "+Constant.INT_DEC+")0x")) {
			/** A: android:layout_height(0x010100f5)=(type 0x10)0xfffffffe */
			String attributeTag=currentTag.substring(0, currentTag.indexOf("(0x"));
			String mRawType=currentTag.substring(0, currentTag.indexOf("=(type 0x"));
			String attributeValue=currentTag.substring(currentTag.lastIndexOf(")0x")+1); 
			String hexaValue=Convert.getHexFormat(attributeValue);
			//attributeValue=ResourcesManager.getResValues(mRawType, hexaValue);
			if(attributeValue==null){
				String currentType=currentTag.substring( currentTag.indexOf("=(type 0x")+7);
				currentType=currentType.substring(0, currentType.indexOf(")"));
				attributeValue=""+Convert.getTypedValue(currentType, hexaValue);
			}
			setAttribute(mCurrentElement,currentTag,attributeTag,attributeValue,false);

		}else if(currentTag.endsWith(")=\"\"")) {
			/** A: android:versionName(0x0101021c)="1.0" (Raw: "1.0")
			 * android:label(0x01010001)="" **/
			String attributeTag=currentTag.substring(0, currentTag.indexOf("=")).trim();
			if(attributeTag.contains("(0x")) {
				attributeTag=attributeTag.substring(0, attributeTag.indexOf("(0x")).trim();
			}
			//String attributeValue=currentTag.substring(currentTag.indexOf("=")+1).trim();
			setAttribute(mCurrentElement,currentTag,attributeTag,"",false);

		}else {
			System.err.println("Missing Attribute "+currentTag.trim()+" for "+DecodeResXMLFiles.mCurrentXmlName);
		}
	}
}

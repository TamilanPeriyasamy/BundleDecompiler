/**
 * 
 */
package com.bundle.resources.values;

import com.bundle.resources.comman.ApplicationResources;
import com.bundle.resources.comman.Resources;
import com.bundle.xmlbuilder.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bundle.conversion.Convert;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author ${Periyasamy C}
 *
 * 21-Sep-2018
 */
public class AttrResources  extends Resources {

	XMLBuilder mXMLBuilder =null;
	Element mRootElement =null;
	Element childElement =null;
	Document mDocument =null;

	public AttrResources() throws ParserConfigurationException {
		mXMLBuilder = new XMLBuilder();
		mDocument = mXMLBuilder.newDocument();
		mRootElement = mDocument.createElement("resources");
		mDocument.appendChild(mRootElement);
		childElement = null;
	}

	private String getAttrResourcesValue(String parseLine,String arrayType) {
		// TODO Auto-generated method stub
		//System.out.println(" "+parseLine);
		String itemValue="";
		if(parseLine.contains("(color)") ) {
			/**  #0 (Key=0x01010097): (color) #00000000 */
			parseLine=parseLine.substring(parseLine.indexOf(": (")+2);
			itemValue=parseLine.substring(parseLine.indexOf(") #")+2).trim();
			itemValue=itemValue.replace("#", "0x");
			itemValue=Convert.getHexFormat(itemValue);
			if(arrayType.contains("enum")) {
				itemValue=""+Convert.hex2Decimal(itemValue);
			}
		}else {
			System.err.println("AttrResources: "+parseLine);
		}
		return itemValue;
	}

	public void parseAttrResources(File currentXmlFile) throws Exception {

		FileReader reader = new FileReader(currentXmlFile);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String currentLine = null;
		String resourceResId = null;
		String resourceRawType = null;
		String resourceResType = null;
		String resourceResName = null;
		String attributeResType = null;

		while((currentLine =bufferedReader.readLine()) != null){
			if (currentLine.trim().startsWith(Resources.mParseResourceEntry +ApplicationResources.mApplicationId)) {
				//resource 0x7f020000 attr/actionBarDivider
				String dumpValues[] = currentLine.trim().split(" ");
				resourceRawType = dumpValues[2];
				resourceResType = resourceRawType.substring(0, resourceRawType.lastIndexOf("/"));
				resourceResName = resourceRawType.substring(resourceRawType.lastIndexOf("/") + 1);

			}else if (currentLine.trim().startsWith("(") && currentLine.contains("type=")) {
				//() (attr) type=reference src=/home/lakeba13/workspace
					String dumpValues[] = currentLine.trim().split(" ");
					attributeResType = dumpValues[2].substring(dumpValues[2].indexOf("type=")+5);
					childElement = mDocument.createElement(resourceResType);
				   if(attributeResType.trim().equals("any")){
					 attributeResType="reference|string|integer|boolean|color|float|dimension|fraction";
				    }
					childElement.setAttribute("format", attributeResType);
					childElement.setAttribute("name", resourceResName);
					mRootElement.appendChild(childElement);

			}else if(currentLine.trim().startsWith("===========================================")){
				//System.out.println(""+currentLine);

			}else if(!currentLine.trim().startsWith("(") && attributeResType.contains("enum")) {
				//wrap_content(0x7f0700b5)=0x00000000
				String enumName = currentLine.substring(0, currentLine.indexOf("(")).trim();
				String enumValue = currentLine.substring(currentLine.indexOf("=") + 1).trim();
				Element newElement = mDocument.createElement("enum");
				newElement.setAttribute("name", enumName);
				newElement.setAttribute("value", enumValue);
				childElement.appendChild(newElement);

			}else if(!currentLine.trim().startsWith("(") && attributeResType.contains("flags")){
					String enumName=currentLine.substring(0,currentLine.indexOf("(")).trim();
					String enumValue=currentLine.substring(currentLine.indexOf("=")+1).trim();
					Element newElement = mDocument.createElement("flag");
					newElement.setAttribute("name", enumName);
					newElement.setAttribute("value", enumValue);
					childElement.appendChild(newElement);

			}else{
				System.err.println(AttrResources.class.getCanonicalName()+" "+currentLine);
			}
		}
		bufferedReader.close();
		reader.close();
		mXMLBuilder.createXMLFile(mDocument,currentXmlFile,false);
	}
}

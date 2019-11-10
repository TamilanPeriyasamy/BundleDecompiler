/**
 * 
 */
package com.bundle.resources.values;

import com.bundle.conversion.Convert;
import com.bundle.resources.comman.*;
import com.bundle.xmlbuilder.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

/**
 * @author ${Periyasamy C}
 *
 * 21-Sep-2018
 */
public class StyleResources extends Resources {

	XMLBuilder mXMLBuilder =null;
	Element mRootElement =null;
	Element childElement =null;
	Document mDocument =null;

	public StyleResources() throws ParserConfigurationException {
		mXMLBuilder = new XMLBuilder();
		mDocument = mXMLBuilder.newDocument();
		mRootElement = mDocument.createElement("resources");
		mDocument.appendChild(mRootElement);
		childElement = null;
	}

	public void parseStyleResources(File currentXmlFile)  throws Exception {

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
				resourceResId = dumpValues[1];
				resourceRawType = dumpValues[2];
				resourceResType = resourceRawType.substring(0, resourceRawType.lastIndexOf("/"));
				resourceResName = resourceRawType.substring(resourceRawType.lastIndexOf("/") + 1);


			}else if (currentLine.trim().startsWith("(") && currentLine.contains("parent=")) {
				//() (attr) type=reference src=/home/lakeba13/workspace
				String dumpValues[] = currentLine.trim().split(" ");
				attributeResType = dumpValues[3].substring(dumpValues[3].indexOf("parent=")+7);
				childElement = mDocument.createElement(resourceResType);
				childElement.setAttribute("parent", "@"+attributeResType);
				childElement.setAttribute("name", resourceResName);
				mRootElement.appendChild(childElement);
				//System.out.println("\n\n=========================\n"+resourceResName);

			}else if (currentLine.trim().startsWith("(") && !currentLine.contains(" parent=")) {
				//() (attr) type=reference src=/home/lakeba13/workspace
				childElement = mDocument.createElement(resourceResType);
				childElement.setAttribute("parent", "");
				childElement.setAttribute("name", resourceResName);
				mRootElement.appendChild(childElement);
				//System.out.println("\n\n=========================\n"+resourceResName);
			}else if(currentLine.trim().startsWith("===========================================")){
				//System.out.println(""+currentLine);

			}else if(!currentLine.trim().startsWith("(")) {
				//android:textStyle(0x01010097)=0x00000000;
				//System.out.println(" currentLine       : "+currentLine.trim());
				String attributeResName = currentLine.substring(0, currentLine.indexOf("(")).trim();
				String attributeResValue = currentLine.substring(currentLine.indexOf("=") + 1).trim();
				String attributeId = currentLine.substring(currentLine.indexOf("(")+1,currentLine.indexOf(")=")).trim();
				Element newElement = mDocument.createElement("item");
				newElement.setAttribute("name", attributeResName);


				if(Pattern.compile("[+-]?[0-9][0-9]*").matcher(attributeResValue).matches()){
					attributeResValue=Convert.getHexFormat(Integer.parseInt(attributeResValue.trim()));
				}

				String findAttributeKey = attributeResName+"("+attributeId+")="+attributeResValue;
				String findAttributeValue=ResourcesManager.getAttributeValue(findAttributeKey);

				if(findAttributeValue==null){
					findAttributeKey = attributeResName+"("+attributeId+")";
					findAttributeValue= ResourcesManager.getFlagTypeValues(findAttributeKey,attributeResValue);
					attributeResValue=(findAttributeValue!=null)?findAttributeValue:attributeResValue;
				}else {
					attributeResValue=findAttributeValue;
				}
				newElement.setTextContent(attributeResValue);
				childElement.appendChild(newElement);

			}else{
				System.err.println(StyleResources.class.getCanonicalName()+" "+currentLine);
			}
		}
		bufferedReader.close();
		reader.close();
		mXMLBuilder.createXMLFile(mDocument,currentXmlFile,false);
	}

}

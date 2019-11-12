/**
 * 
 */
package com.bundle.resources.values;

import com.bundle.resources.comman.ApplicationResources;
import com.bundle.resources.comman.Resources;
import com.bundle.xmlbuilder.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author ${Periyasamy C}
 *
 * 23-Sep-2018
 */
public class DrawableResources extends Resources {


	XMLBuilder mXMLBuilder =null;
	Element mRootElement =null;
	Element childElement =null;
	Document mDocument =null;

	public DrawableResources() throws ParserConfigurationException {
		mXMLBuilder = new XMLBuilder();
		mDocument = mXMLBuilder.newDocument();
		mRootElement = mDocument.createElement("resources");
		mDocument.appendChild(mRootElement);
		childElement = null;
	}

	public void parseDrawableResources(File currentXmlFile)  throws Exception {

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

			}else if (currentLine.trim().startsWith("(")) {
				String dumpValues[] = currentLine.trim().split(" ");
				attributeResType = dumpValues[1];
				childElement = mDocument.createElement("item");
				childElement.setAttribute("name", resourceResName);
				childElement.setAttribute("type",resourceResType);
				childElement.setTextContent(attributeResType);
				mRootElement.appendChild(childElement);

			}else if(currentLine.trim().startsWith("===========================================")){
				//System.out.println(""+currentLine);

			}else{
				System.err.println(DrawableResources.class.getCanonicalName()+" "+currentLine);
			}
		}
		bufferedReader.close();
		reader.close();
		mXMLBuilder.createXMLFile(mDocument,currentXmlFile,false);
	}
}

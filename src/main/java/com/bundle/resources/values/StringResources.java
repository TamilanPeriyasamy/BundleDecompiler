/**
 *
 */
package com.bundle.resources.values;


import com.bundle.resources.comman.ApplicationResources;
import com.bundle.resources.comman.Resources;
import com.bundle.xmlbuilder.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author ${Periyasamy C}
 *
 * 22-Sep-2018
 */
public class StringResources extends Resources {

    Element mRootElement = null;
    Element childElement = null;
    Document mDocument = null;
    XMLBuilder mXMLBuilder = null;

    public StringResources() throws ParserConfigurationException {
        mXMLBuilder = new XMLBuilder();
        mDocument = mXMLBuilder.newDocument();
        mRootElement = mDocument.createElement("resources");
        mDocument.appendChild(mRootElement);
        childElement = null;
    }

    public void parseStringResources(File currentXmlFile) throws Exception {

        FileReader reader = new FileReader(currentXmlFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String currentLine = null;
        String resourceResId = null;
        String resourceRawType = null;
        String resourceResType = null;
        String resourceResName = null;
        String attributeResType = null;

        while ((currentLine = bufferedReader.readLine()) != null) {

            if (currentLine.trim().startsWith(Resources.mParseResourceEntry +ApplicationResources.mApplicationId)) { //resource 0x7f020000 attr/actionBarDivider
                String dumpValues[] = currentLine.trim().split(" ");
                resourceRawType = dumpValues[2];
                resourceResType = resourceRawType.substring(0, resourceRawType.lastIndexOf("/"));
                resourceResName = resourceRawType.substring(resourceRawType.lastIndexOf("/") + 1);

            } else if (currentLine.trim().startsWith("(")) {
                attributeResType = "";
                while (currentLine!=null && !currentLine.startsWith("===========================================")) {
                    if(currentLine.trim().contains(") \"") && currentLine.contains(" src=")) {
                        String tmpCurrentLine = currentLine.substring(currentLine.indexOf(") \"") + 2, currentLine.lastIndexOf(" src=") + 1);
                        attributeResType = attributeResType + tmpCurrentLine.trim();

                    }else if(currentLine.trim().contains(") \"")) {
                        String tmpCurrentLine=currentLine.substring(currentLine.indexOf(") \"") + 2);
                        attributeResType = attributeResType+tmpCurrentLine.trim();

                    } else if(currentLine.contains(" src=")) {
						String tmpCurrentLine = currentLine.substring(0, currentLine.lastIndexOf(" src="));
						attributeResType = attributeResType+tmpCurrentLine.trim();

                    } else {
                        attributeResType = attributeResType + currentLine.trim();
                    }
                    currentLine = bufferedReader.readLine();
                }

                //resourceResType=resourceResType.substring(resourceResType.indexOf("\""),resourceResType.lastIndexOf("\""));
                //attributeResType = StringEscapeUtils.escapeXml(attributeResType);
                childElement = mDocument.createElement(resourceResType);
                childElement.setAttribute("name", resourceResName);
                childElement.setTextContent(attributeResType);
                mRootElement.appendChild(childElement);
            } else if (currentLine.trim().startsWith("===========================================")) {
                //System.out.println(""+currentLine);

            } else {
                System.err.println(DrawableResources.class.getCanonicalName() + " " + currentLine);
            }
        }
        bufferedReader.close();
        reader.close();
        mXMLBuilder.createXMLFile(mDocument, currentXmlFile, false);
    }
}

/**
 *
 */
package com.bundle.resources.values;

import com.bundle.resources.comman.*;
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
 * 25-Sep-2018
 */
public class ArrayResources extends Resources {

    Element mRootElement = null;
    Element childElement = null;
    Document mDocument = null;
    XMLBuilder mXMLBuilder = null;

    public ArrayResources() throws ParserConfigurationException {
        mXMLBuilder = new XMLBuilder();
        mDocument = mXMLBuilder.newDocument();
        mRootElement = mDocument.createElement("resources");
        mDocument.appendChild(mRootElement);
        childElement = null;
    }


    public void parseArrayResources(File currentXmlFile) throws Exception {

        FileReader reader = new FileReader(currentXmlFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String currentLine = null;
        String resourceRawType = null;
        String resourceResType = null;
        String resourceResName = null;
        String attributeResType = "";

        while ((currentLine = bufferedReader.readLine()) != null) {

            //resource 0x7f020000 attr/actionBarDivider
            if (currentLine.trim().startsWith(Resources.mParseResourceEntry +ApplicationResources.mApplicationId)) {
                String dumpValues[] = currentLine.trim().split(" ");
                resourceRawType = dumpValues[2];
                resourceResType = resourceRawType.substring(0, resourceRawType.lastIndexOf("/"));
                resourceResName = resourceRawType.substring(resourceRawType.lastIndexOf("/") + 1);

            } else if (currentLine.trim().contains(" (array) ")) {
                //System.out.println(" "+currentLine);

            } else if ((currentLine.trim().startsWith("[\"") || currentLine.trim().startsWith("[@")) && !currentLine.startsWith("[(styled string)")) {
                attributeResType = "";
                if (currentLine.trim().startsWith("[\"")) {
                    resourceResType = "string-" + resourceResType;
                }
                while (currentLine!=null && !currentLine.startsWith("===========================================")) {
                    attributeResType = attributeResType+currentLine+" ";
                    currentLine = bufferedReader.readLine();
                }
                String[] arrayValues =new PatternParser().getStringArray(attributeResType.trim());
                childElement = mDocument.createElement(resourceResType);
                childElement.setAttribute("name", resourceResName);
                mRootElement.appendChild(childElement);
                for (int count = 0; count < arrayValues.length; count++) {
                    Element newElement = mDocument.createElement("item");
                    newElement.setTextContent(arrayValues[count]);
                    childElement.appendChild(newElement);
                }


            } else if (currentLine.startsWith("[(styled string)") ) {
               // System.out.println("\n======================");
                attributeResType="";
                resourceResType = "string-" + resourceResType;
                while(currentLine!=null && !currentLine.trim().startsWith("=============================================================") ){
                    attributeResType = attributeResType + currentLine+"\n";
                    currentLine = bufferedReader.readLine();
                }
                String[] arrayValues=new PatternParser().parseStyledArrayValues(attributeResType);
                //System.out.println(""+arrayValues.length);
                childElement = mDocument.createElement(resourceResType);
                childElement.setAttribute("name", resourceResName);
                mRootElement.appendChild(childElement);
                for (int count = 0; count < arrayValues.length; count++) {
                    //if(arrayValues[count].length()!=0) {
                        //System.out.println(""+arrayValues[count]);
                        Element newElement = mDocument.createElement("item");
                        newElement.setTextContent(arrayValues[count]);
                        childElement.appendChild(newElement);
                   //}
                }

            } else if (currentLine.trim().startsWith("===========================================")) {
                //System.out.println(""+currentLine);

            } else {
                System.err.println(ArrayResources.class.getCanonicalName() + " " + currentLine);
            }
        }
        bufferedReader.close();
        reader.close();
        mXMLBuilder.createXMLFile(mDocument, currentXmlFile, false);
    }
}

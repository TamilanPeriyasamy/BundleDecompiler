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

            } else if (currentLine.trim().startsWith("[\"") || currentLine.trim().startsWith("[@") || currentLine.startsWith("[(styled string)") ) {
                attributeResType="";
                String[] arrayValues =null;
                boolean styledString =false;
                if(currentLine.startsWith("[(styled string)")){
                    styledString=true;
                }

                if (currentLine.trim().startsWith("[\"") || currentLine.startsWith("[(styled string)") ) {
                    resourceResType = "string-" + resourceResType;
                }

                if (currentLine.trim().startsWith("[") && currentLine.trim().endsWith("]")) {
                    arrayValues =new PatternParser().parseArrayValues(currentLine);

                }else{
                    while(currentLine!=null && !currentLine.trim().startsWith("=============================================================") ){
                        if(attributeResType.endsWith(",") && (currentLine.startsWith("\"") || currentLine.startsWith("@"))){
                            attributeResType = attributeResType+" "+ currentLine;

                        }else{
                            if(styledString) {
                                attributeResType = attributeResType + currentLine + "\n";
                            }else{
                                attributeResType = attributeResType + currentLine;
                            }

                        }
                        currentLine = bufferedReader.readLine();
                    }
                    arrayValues =new PatternParser().parseArrayValues(attributeResType);

                }
                childElement = mDocument.createElement(resourceResType);
                childElement.setAttribute("name", resourceResName);
                mRootElement.appendChild(childElement);
                for (int count = 0; count < arrayValues.length; count++) {
                    Element newElement = mDocument.createElement("item");
                    //arrayValues[count] = StringEscapeUtils.escapeXml(arrayValues[count]);
                    newElement.setTextContent(arrayValues[count]);
                    childElement.appendChild(newElement);
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

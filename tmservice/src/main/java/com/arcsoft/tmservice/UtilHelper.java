package com.arcsoft.tmservice;

import java.io.*;
import javax.servlet.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;


/**
 * @author Morgan Wang
 * @version 1.0
 */

public class UtilHelper
{
	static public boolean outputXml2Response(Node srcXmlNode, String templetFilePath, ServletOutputStream out)
	{
		if (srcXmlNode != null) 
        {
			try 
            {
				Source srcXsl = null;
				if (templetFilePath != null)
				{
					try 
					{
						InputSource isXsl = new InputSource(new FileReader(templetFilePath));
						srcXsl = new SAXSource(isXsl);
					}
					catch (Exception e) 
		            {
						srcXsl = null;
					}
				}
				
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer trans = (srcXsl==null) ? factory.newTransformer() : factory.newTransformer(srcXsl);

				Source srcXml = new DOMSource(srcXmlNode);
				trans.transform(srcXml, new StreamResult(out));
			}
			catch (Exception e) 
            {
				return false;
			}
		}
		else 
        {
			return false;
		}
		return true;
	}

	// Note: this static method is just only used to debug Node value
	static void debugNode2File(Node srcNode, String dstFileName) {
		boolean isReleased = false;
		if (isReleased) return;
		
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(srcNode);
			FileOutputStream fos = new FileOutputStream(dstFileName);
			StreamResult result = new StreamResult(fos);
			transformer.transform(source, result);
			fos.close();
		}
		catch (Exception e) {
		}
	}

}

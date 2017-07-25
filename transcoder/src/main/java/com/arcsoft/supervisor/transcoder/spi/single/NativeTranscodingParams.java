package com.arcsoft.supervisor.transcoder.spi.single;

import com.arcsoft.supervisor.transcoder.TranscodingParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Bing
 */
public class NativeTranscodingParams extends TranscodingParams {
    
    private static Logger logger = LoggerFactory.getLogger(NativeTranscodingParams.class);


    /**
     * {index,duration;...}
     */
    protected Map<Integer, Integer> durations = new Hashtable<Integer, Integer>();

    /**
     * internal used
     */
    protected NativeTranscodingParams() {

    }

    /**
     * @param nativeTranscoderXmlParam
     */
    public NativeTranscodingParams(String nativeTranscoderXmlParam) {
        this.xmlparam = nativeTranscoderXmlParam;
    }

    @Override
    public void checkTranscoderXmlReady() throws Exception {

    }

    public String getInputType() {
        if (this.inputType == null) {
            this.inputType = uGetValInXml(xmlparam, "/TranscoderTask/Inputs/Input[1]/Type");
        }
        return this.inputType;
    }

//	protected String getInputURI(int index){
//		try {
//			String path = uGetValInXml(xmlparam, "/TranscoderTask/Inputs/Input[1]/URI");
//			if(path.startsWith("/"))
//				return new File(path).toURI().toString();
//		} catch (Exception e) {			
//			logger.info(e.getMessage());
//		}
//		return null;
//	}	

    public int getOutputGroupCount() {
        if (this.outputGroupCount == -1) {
            String valPath = "/TranscoderTask/OutputGroups/@Count";
            try {
                this.outputGroupCount = Integer.parseInt(uGetValInXml(xmlparam, valPath));
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error("Exception: ", e);
                this.outputGroupCount = 0;
            }
            logger.info(valPath + this.outputGroupCount);
        }
        return this.outputGroupCount;
    }

    public int getHDOuputCount() {
        if (this.hdOuputCount == -1) {
            String valPath = "count(//VideoSetting[Height>=720])";
            try {
                this.hdOuputCount = Integer.parseInt(uGetValInXml(xmlparam, valPath));
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error("Exception: ", e);
                this.hdOuputCount = 0;
            }
            logger.info(valPath + this.hdOuputCount);
        }
        return this.hdOuputCount;
    }

    public int getSDOuputCount() {
        if (this.sdOuputCount == -1) {
            String valPath = "count(//VideoSetting[Height<720])";
            try {
                this.sdOuputCount = Integer.parseInt(uGetValInXml(xmlparam, valPath));
            } catch (Exception e) {
                //e.printStackTrace();
                logger.error("Exception: ", e);
                this.sdOuputCount = 0;
            }
            logger.info(valPath + this.sdOuputCount);
        }
        return this.sdOuputCount;
    }

    protected static String uGetValInXml(String xmlstr, String xpathExp) {
        XPathFactory f = XPathFactory.newInstance();
        XPath p = f.newXPath();
        try {
            return p.evaluate(xpathExp, new InputSource(new StringReader(xmlstr)));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    protected static String uGet1stElemTextVal(String xmlstr, String tagName) {
        String val = null;
        XMLStreamReader r = null;
        try {
            r = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xmlstr));
            while (r.hasNext()) {
                int e = r.next();
                if (e != XMLStreamReader.START_ELEMENT)
                    continue;
                String tag = r.getName().getLocalPart();
                if (tag.equals(tagName)) {
                    val = r.getElementText().trim();
                    break;
                }
            }// ~while
        } catch (XMLStreamException e) {
            logger.error("XMLStreamException: ", e);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (XMLStreamException e) {
                logger.error("XMLStreamException: ", e);
            }
        }
        return val;
    }
}

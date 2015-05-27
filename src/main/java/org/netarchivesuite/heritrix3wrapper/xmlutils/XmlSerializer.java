package org.netarchivesuite.heritrix3wrapper.xmlutils;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlSerializer {

    public static String toString(Document xml) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        //serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount", "2");
        Source xmlSource = new DOMSource(xml);
        StreamResult res = new StreamResult(new ByteArrayOutputStream());            
        serializer.transform(xmlSource, res);
        return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());
    }

}

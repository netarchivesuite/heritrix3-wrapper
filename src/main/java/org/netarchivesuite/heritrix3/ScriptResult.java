package org.netarchivesuite.heritrix3;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.netarchivesuite.heritrix3.jaxb.Script;
import org.netarchivesuite.xmlutils.XmlValidationResult;
import org.netarchivesuite.xmlutils.XmlValidator;

public class ScriptResult {

    public int responseCode;

    public byte[] response;

    public XmlValidationResult result = new XmlValidationResult();

    public Script script;

    public void parse(XmlValidator xmlValidator) throws JAXBException, XMLStreamException {
        ByteArrayInputStream bIn;
        result = new XmlValidationResult();
        bIn = new ByteArrayInputStream(response);
        xmlValidator.testStructuralValidity(bIn, null, null, result);
        bIn = new ByteArrayInputStream(response);
        script = Script.unmarshall(bIn);
    }

}

package org.netarchivesuite.heritrix3wrapper;

import java.io.ByteArrayInputStream;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.netarchivesuite.heritrix3wrapper.jaxb.Script;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidationResult;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidator;

public class ScriptResult extends ResultAbstract {

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

package org.netarchivesuite.heritrix3wrapper.jaxb;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@XmlRootElement
public class Script {

    @XmlElement(required=true)
    public String crawlJobUrl;

    @XmlElement(required=true)
    public String crawlJobShortName;

    @XmlElementWrapper(name="availableScriptEngines")
    @XmlElement(name="value", required=true)
    public List<ScriptEngine> availableScriptEngines;

    @XmlElement(required=true)
    public String script;

    @XmlElement(required=true)
    public Integer linesExecuted;

    @XmlElement(required=false)
    public String exception;

    @XmlElement(required=true)
    public String htmlOutput;

    @XmlElement(required=false)
    public String rawOutput;

    @XmlElementWrapper(name="availableGlobalVariables")
    @XmlElement(name="value", required=true)
    public List<GlobalVariable> availableGlobalVariables;

    @XmlElement(required=true)
    public Boolean failure;

    @XmlElement(required=false)
    public String stackTrace;

    public static final XMLInputFactory inputFactory;

    public static final JAXBContext jaxbContext;

    static {
        inputFactory = XMLInputFactory.newFactory();
        try {
            jaxbContext = JAXBContext.newInstance( Script.class );
        }
        catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public static void marshall(Script script, OutputStream out) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        jaxbMarshaller.marshal( script, out );
    }

    public static Script unmarshall(InputStream in) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XMLStreamReader reader = inputFactory.createXMLStreamReader( in );
        return jaxbUnmarshaller.unmarshal( reader, Script.class ).getValue();
    }

}

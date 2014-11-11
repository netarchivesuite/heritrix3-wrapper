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
public class Engine {

    @XmlElement(required=true)
    public String heritrixVersion;

    @XmlElement(required=true)
    public HeapReport heapReport;

    @XmlElement(required=true)
    public String jobsDir;

    @XmlElement(required=true)
    public String jobsDirUrl;

    @XmlElementWrapper(name="availableActions")
    @XmlElement(name="value", required=true)
    public List<String> availableActions;

    @XmlElementWrapper(name="jobs")
    @XmlElement(name="value", required=true)
    public List<JobShort> jobs;

    public static final XMLInputFactory inputFactory;

    public static final JAXBContext jaxbContext;

    static {
        inputFactory = XMLInputFactory.newFactory();
        try {
            jaxbContext = JAXBContext.newInstance( Engine.class );
        }
        catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public static void marshall(Engine engine, OutputStream out) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        jaxbMarshaller.marshal( engine, out );
    }

    public static Engine unmarshall(InputStream in) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XMLStreamReader reader = inputFactory.createXMLStreamReader( in );
        return jaxbUnmarshaller.unmarshal( reader, Engine.class ).getValue();
    }

}

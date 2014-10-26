package org.netarchivesuite.heritrix3.jaxb;

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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.netarchivesuite.heritrix3.jaxb.DateFormatLastLaunch.LastLaunchAdadapter;

@XmlRootElement
public class Job {

    @XmlElement(required=true)
    public String shortName;

    @XmlElement(required=false)
    public String crawlControllerState;

    @XmlElement(required=false)
    public String crawlExitStatus;

    @XmlElement(required=true)
    public String statusDescription;

    @XmlElementWrapper(name="availableActions")
    @XmlElement(name="value", required=true)
    public List<String> availableActions;

    @XmlElement(required=true)
    public Integer launchCount;

    // 2014-10-22T16:37:34.654+02:00
    @XmlElement(required=true)
    @XmlJavaTypeAdapter(LastLaunchAdadapter.class)
    public Long lastLaunch;

    @XmlElement(required=true)
    public Boolean isProfile;

    @XmlElement(required=true)
    public String primaryConfig;

    @XmlElement(required=true)
    public String primaryConfigUrl;

    @XmlElement(required=true)
    public String url;

    @XmlElementWrapper(name="jobLogTail")
    @XmlElement(name="value", required=true)
    public List<String> jobLogTail;

    @XmlElement(required=true)
    public UriTotalsReport uriTotalsReport;

    @XmlElement(required=true)
    public SizeTotalsReport sizeTotalsReport;

    @XmlElement(required=true)
    public RateReport rateReport;

    @XmlElement(required=true)
    public LoadReport loadReport;

    @XmlElement(required=true)
    public ElapsedReport elapsedReport;

    @XmlElement(required=true)
    public ThreadReport threadReport;

    @XmlElement(required=true)
    public FrontierReport frontierReport;

    @XmlElementWrapper(name="crawlLogTail")
    @XmlElement(name="value", required=true)
    public List<String> crawlLogTail;

    @XmlElementWrapper(name="configFiles")
    @XmlElement(name="value", required=true)
    public List<ConfigFile> configFiles;

    @XmlElement(required=true)
    public Boolean isLaunchInfoPartial;

    @XmlElement(required=true)
    public Boolean isRunning;

    @XmlElement(required=true)
    public Boolean isLaunchable;

    @XmlElement(required=true)
    public Boolean hasApplicationContext;

    @XmlElement(required=true)
    public Integer alertCount;

    // TODO H3 does not seem willing to fill this at all.
    public List<String> checkpointFiles;

    @XmlElement(required=false)
    public String alertLogFilePath;

    @XmlElement(required=false)
    public String crawlLogFilePath;

    @XmlElementWrapper(name="reports")
    @XmlElement(name="value", required=true)
    public List<Report> reports;

    @XmlElement(required=true)
    public HeapReport heapReport;

    public static final XMLInputFactory inputFactory;

    public static final JAXBContext jaxbContext;

    static {
        inputFactory = XMLInputFactory.newFactory();
        try {
            jaxbContext = JAXBContext.newInstance( Job.class );
        }
        catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public static void marshall(Job job, OutputStream out) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        jaxbMarshaller.marshal( job, out );
    }

    public static Job unmarshall(InputStream in) throws JAXBException, XMLStreamException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XMLStreamReader reader = inputFactory.createXMLStreamReader( in );
        return jaxbUnmarshaller.unmarshal( reader, Job.class ).getValue();
    }

}

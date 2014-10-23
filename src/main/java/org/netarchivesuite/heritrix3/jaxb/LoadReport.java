package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoadReport {

    @XmlElement(required=true)
    public int busyThreads;

    @XmlElement(required=true)
    public int totalThreads;

    @XmlElement(required=true)
    public double congestionRatio;

    @XmlElement(required=true)
    public int averageQueueDepth;

    @XmlElement(required=true)
    public int deepestQueueDepth;

}

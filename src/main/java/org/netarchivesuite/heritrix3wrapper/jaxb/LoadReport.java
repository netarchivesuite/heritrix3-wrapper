package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoadReport {

    @XmlElement(required=true)
    public Integer busyThreads;

    @XmlElement(required=true)
    public Integer totalThreads;

    @XmlElement(required=true)
    public Double congestionRatio;

    @XmlElement(required=true)
    public Integer averageQueueDepth;

    @XmlElement(required=true)
    public Integer deepestQueueDepth;

}

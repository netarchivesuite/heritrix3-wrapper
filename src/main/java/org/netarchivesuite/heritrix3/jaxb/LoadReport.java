package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

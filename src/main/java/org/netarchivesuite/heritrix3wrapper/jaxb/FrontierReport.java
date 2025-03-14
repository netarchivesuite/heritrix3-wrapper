package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FrontierReport {

    @XmlElement(required=true)
    public Integer totalQueues;

    @XmlElement(required=true)
    public Integer inProcessQueues;

    @XmlElement(required=true)
    public Integer readyQueues;

    @XmlElement(required=true)
    public Integer snoozedQueues;

    @XmlElement(required=true)
    public Integer activeQueues;

    @XmlElement(required=true)
    public Integer inactiveQueues;

    @XmlElement(required=true)
    public Integer ineligibleQueues;

    @XmlElement(required=true)
    public Integer retiredQueues;

    @XmlElement(required=true)
    public Integer exhaustedQueues;

    @XmlElement(required=true)
    public String lastReachedState;

}

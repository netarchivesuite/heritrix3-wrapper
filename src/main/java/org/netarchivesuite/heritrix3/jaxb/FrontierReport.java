package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FrontierReport {

    @XmlElement(required=true)
    public int totalQueues;

    @XmlElement(required=true)
    public int inProcessQueues;

    @XmlElement(required=true)
    public int readyQueues;

    @XmlElement(required=true)
    public int snoozedQueues;

    @XmlElement(required=true)
    public int activeQueues;

    @XmlElement(required=true)
    public int inactiveQueues;

    @XmlElement(required=true)
    public int ineligibleQueues;

    @XmlElement(required=true)
    public int retiredQueues;

    @XmlElement(required=true)
    public int exhaustedQueues;

    @XmlElement(required=true)
    public String lastReachedState;

}

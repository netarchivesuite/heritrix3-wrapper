package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HeapReport {

    @XmlElement(required=true)
    public Long usedBytes;

    @XmlElement(required=true)
    public Long totalBytes;

    @XmlElement(required=true)
    public Long maxBytes;

}

package org.netarchivesuite.heritrix3wrapper.jaxb;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UriTotalsReport {

    @XmlElement(required=true)
    public Long downloadedUriCount;

    @XmlElement(required=true)
    public Long queuedUriCount;

    @XmlElement(required=true)
    public Long totalUriCount;

    @XmlElement(required=true)
    public Long futureUriCount;

}

package org.netarchivesuite.heritrix3wrapper.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

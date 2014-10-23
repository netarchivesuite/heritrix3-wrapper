package org.netarchivesuite.heritrix3.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UriTotalsReport {

    @XmlElement(required=true)
    public long downloadedUriCount;

    @XmlElement(required=true)
    public long queuedUriCount;

    @XmlElement(required=true)
    public long totalUriCount;

    @XmlElement(required=true)
    public long futureUriCount;

}

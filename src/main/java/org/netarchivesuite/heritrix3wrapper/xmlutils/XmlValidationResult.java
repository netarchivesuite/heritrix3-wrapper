package org.netarchivesuite.heritrix3wrapper.xmlutils;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;

/**
 * A class containing various information produced by the XML validator.
 */
public class XmlValidationResult {

    /** Parsed XML document. */
    public Document document = null;

    /** XML document systemId, sometimes referred to as the DTD. */
    public String systemId = null;

    /** xmlns:xsi namespace(s). */
    public List<String> xsiNamespaces = new LinkedList<String>();

    /** Schemas referred to in the XML document. */
    public List<String> schemas = new LinkedList<String>();

    /** Does the document refer to a DTD. */
    public boolean bDtdUsed = false;

    /** Does the document refer to any XSD. */
    public boolean bXsdUsed = false;

    /** Is the document wellformed. */
    public boolean bWellformed = false;

    /** Was the document successfully validated against DTD/XSD. */
    public boolean bValid = false;

    /**
     * Reset fields.
     */
    public void reset() {
        document = null;
        systemId = null;
        xsiNamespaces.clear();
        schemas.clear();
        bDtdUsed = false;
        bXsdUsed = false;
        bWellformed = false;
        bValid = false;
    }

}

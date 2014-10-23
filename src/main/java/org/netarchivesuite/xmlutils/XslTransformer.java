package org.netarchivesuite.xmlutils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Implements an XSL transformer wrapper to make XSL transformation even simpler.
 */
public class XslTransformer {

    /** XSL transformer factory object. */
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /** XSL transformer implementation for a specific stylesheet. */
    private Transformer transformerImpl;

    /**
     * Private constructor.
     */
    private XslTransformer() {
    }

    /**
     * Get a wrapped XSL transformer instance for supplied stylesheet source.
     * @param source XSL source
     * @return XSL transformer instance
     * @throws TransformerConfigurationException if an exception occurs while processing
     */
    public static XslTransformer getTransformer(Source source) throws TransformerConfigurationException {
    	if (source == null) {
    		throw new IllegalArgumentException("source is null");
    	}
        XslTransformer transformer = new XslTransformer();
        transformer.transformerImpl = transformerFactory.newTransformer(source);
        return transformer;
    }

    /**
     * Get a wrapped XSL transformer instance for supplied stylesheet file.
     * @param xslFile XSL file
     * @return XSL transformer instance
     * @throws TransformerConfigurationException if an error occurs while processing
     */
    public static XslTransformer getTransformer(File xslFile) throws TransformerConfigurationException {
    	if (xslFile == null) {
    		throw new IllegalArgumentException("xslFile is null");
    	}
        return getTransformer(new StreamSource(xslFile));
    }

    /**
     * Get XSL transformer instance.
     * @return XSL transformer instance
     */
    public Transformer getTransformerImpl() {
        return transformerImpl;
    }

    /**
     * Transform XML source using the wrapped XSL transformer of this instance and output to supplied target.
     * @param xmlSource source XML
     * @param uriResolver URI resolver or null
     * @param errorListener error listener or null
     * @param outputTarget output result target to use
     * @throws TransformerException if an exception occurs while transforming
     */
    public void transform(Source xmlSource, URIResolver uriResolver, ErrorListener errorListener, Result outputTarget) throws TransformerException {
    	if (xmlSource == null) {
    		throw new IllegalArgumentException("xmlSource is null");
    	}
    	if (outputTarget == null) {
    		throw new IllegalArgumentException("outputTarget is null");
    	}
        transformerImpl.reset();
        transformerImpl.setOutputProperty(OutputKeys.INDENT, "yes");
        transformerImpl.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformerImpl.setURIResolver(uriResolver);
        transformerImpl.setErrorListener(errorListener);
        // Feature: Add parameters, if required.
        transformerImpl.transform(xmlSource, outputTarget);
    }

    /**
     * Transform XML source using the wrapped XSL transformer of this instance and return a byte array of the result.
     * @param xmlSource source XML
     * @param uriResolver URI resolver or null
     * @param errorListener error listener or null
     * @return byte array of the result
     * @throws TransformerException if an exception occurs while transforming
     */
    public byte[] transform(Source xmlSource, URIResolver uriResolver, ErrorListener errorListener) throws TransformerException {
    	if (xmlSource == null) {
    		throw new IllegalArgumentException("xmlSource is null");
    	}
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        transform(xmlSource, uriResolver, errorListener, result);
        return out.toByteArray();
    }

}

package org.netarchivesuite.heritrix3wrapper.xmlutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements an XML validator for well-formed-ness and against DTD/XSD if they are defined.
 */
public class XmlValidator {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(XmlValidator.class.getName());

    /** Schema validation enabler name. */
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /** Schema validation enabler value. */
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    /** Cached document builder factory without DTD/Scheme validation. */
    private DocumentBuilderFactory factoryParsing;
    /** Cached document builder without DTD/Scheme validation. */
    private DocumentBuilder builderParsing;

    /** Cached document builder factory with DTD/Schema validation. */
    private DocumentBuilderFactory factoryValidating;
    /** Cached document builder with DTD/Schema validation. */
    private DocumentBuilder builderValidating;

    /**
     * Construct an <code>XmlValidator</code> instance.
     */
    public XmlValidator() {
        factoryParsing = DocumentBuilderFactory.newInstance();
        factoryParsing.setNamespaceAware(true);
        factoryParsing.setValidating(false);
        factoryValidating = DocumentBuilderFactory.newInstance();
        factoryValidating.setNamespaceAware(true);
        factoryValidating.setValidating(true);
        factoryValidating.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        try {
            builderParsing = factoryParsing.newDocumentBuilder();
            builderValidating = factoryValidating.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not create a new 'DocumentBuilder'!");
        }
    }

    /**
     * Validate XML document for well-formed-ness and also against any DTD/XSD found.
     * @param xmlFile xml file
     * @param entityResolver XML entity resolver or null
     * @param errorHandler error handler or null
     * @return XML validation result
     */
    public XmlValidationResult validate(File xmlFile, EntityResolver entityResolver, XmlErrorHandlerAbstract errorHandler) {
    	if (xmlFile == null) {
    		throw new IllegalArgumentException("xmlFile is null");
    	}
        XmlValidationResult result = new XmlValidationResult();
        if (errorHandler == null) {
            errorHandler = new XmlErrorHandler();
        }
        errorHandler.reset();
        InputStream in = null;
        try {
            /*
             * Test for well-formed-ness.
             */
            in = new FileInputStream(xmlFile);
            builderParsing.reset();
            builderParsing.setErrorHandler(errorHandler);
            result.document = builderParsing.parse(in);
            in.close();
            in = null;
            result.bWellformed = !errorHandler.hasErrors();
            /*
             * Look for references to DTD or XSD.
             */
            DocumentType documentType = result.document.getDoctype();
            if (documentType != null) {
                result.systemId = documentType.getSystemId();
            }
            if (result.systemId != null) {
                result.bDtdUsed = true;
            } else {
                XPathFactory xpf = XPathFactory.newInstance();
                XPath xp = xpf.newXPath();
                NodeList nodes;
                Node node;
                // JDK6 XPath engine supposedly only implements v1.0 of the specs.
                nodes = (NodeList)xp.evaluate("//*", result.document.getDocumentElement(), XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    node = nodes.item(i).getAttributes().getNamedItem("xmlns:xsi");
                    if (node != null) {
                        result.xsiNamespaces.add(node.getNodeValue());
                        result.bXsdUsed = true;
                    }
                    node = nodes.item(i).getAttributes().getNamedItem("xsi:schemaLocation");
                    if (node != null) {
                        // debug
                        result.schemas.add(node.getNodeValue());
                        result.bXsdUsed = true;
                    }
                }
            }
            /*
             * Validate against DTD/XSD.
             */
            if (result.bDtdUsed || result.bXsdUsed) {
                in = new FileInputStream(xmlFile);
                builderValidating.reset();
                builderValidating.setEntityResolver(entityResolver);
                builderValidating.setErrorHandler(errorHandler);
                result.document = builderValidating.parse(in);
                in.close();
                in = null;
                result.bValid = !errorHandler.hasErrors();
            }
        } catch (Throwable t) {
            logger.error("Exception validating XML stream!", t.toString(), t);
            try {
                String publicId = "";
                String systemId = "";
                int lineNumber = -1;
                int columnNumber = -1;
                Exception sillyApiException = new Exception("XML validation exception!", t);
                errorHandler.error(new SAXParseException(t.toString(), publicId, systemId, lineNumber, columnNumber, sillyApiException));
            } catch (SAXException e) {
                throw new IllegalStateException("Failed to add error to ErrorHandler!", e);
            }
            result.bWellformed = false;
            result.bValid = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Exception closing stream!", e.toString(), e);
                }
                in = null;
            }
        }
        return result;
    }

    /**
     * Test XML document for well-formed-ness and look for DTD/XSD references.
     * Closes the input stream before returning.
     * @param in XML input stream
     * @param entityResolver XML entity resolver or null
     * @param errorHandler error handler or null
     * @param result validation results
     * @return result of testing, true if the document is well-formed
     */
    public boolean testStructuralValidity(InputStream in, EntityResolver entityResolver, XmlErrorHandlerAbstract errorHandler, XmlValidationResult result) {
    	if (in == null) {
    		throw new IllegalArgumentException("in is null");
    	}
    	if (result == null) {
    		throw new IllegalArgumentException("result is null");
    	}
        if (errorHandler == null) {
            errorHandler = new XmlErrorHandler();
        }
        errorHandler.reset();
        result.reset();
        try {
            /*
             * Test for well-formed-ness.
             */
            builderParsing.reset();
            builderParsing.setErrorHandler(errorHandler);
            result.document = builderParsing.parse(in);
            in.close();
            in = null;
            /*
             * Look for references to DTD or XSD.
             */
            DocumentType documentType = result.document.getDoctype();
            if (documentType != null) {
                result.systemId = documentType.getSystemId();
            }
            if (result.systemId != null) {
                result.bDtdUsed = true;
            } else {
                XPathFactory xpf = XPathFactory.newInstance();
                XPath xp = xpf.newXPath();
                NodeList nodes;
                Node node;
                // JDK6 XPath engine supposedly only implements v1.0 of the specs.
                nodes = (NodeList)xp.evaluate("//*", result.document.getDocumentElement(), XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    node = nodes.item(i).getAttributes().getNamedItem("xmlns:xsi");
                    if (node != null) {
                        result.xsiNamespaces.add(node.getNodeValue());
                        result.bXsdUsed = true;
                    }
                    node = nodes.item(i).getAttributes().getNamedItem("xsi:schemaLocation");
                    if (node != null) {
                        result.schemas.add(node.getNodeValue());
                        result.bXsdUsed = true;
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Exception validating XML stream!", t.toString(), t);
            try {
                String publicId = "";
                String systemId = "";
                int lineNumber = -1;
                int columnNumber = -1;
                Exception sillyApiException = new Exception("XML validation exception!", t);
                errorHandler.error(new SAXParseException(t.toString(), publicId, systemId, lineNumber, columnNumber, sillyApiException));
            } catch (SAXException e) {
                throw new IllegalStateException("Failed to add error to ErrorHandler!", e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Exception closing stream!", e.toString(), e);
                }
                in = null;
            }
        }
        result.bWellformed = !errorHandler.hasErrors();
        return result.bWellformed;
    }

    /**
     * Validate XML document against any DTD/XSD found. Closes the input stream before returning.
     * @param in XML input stream
     * @param entityResolver XML entity resolver or null
     * @param errorHandler error handler or null
     * @param result validation results
     * @return result of testing, true if the document was validated against DTD/XSD(s).
     */
    public boolean testDefinedValidity(InputStream in, EntityResolver entityResolver, XmlErrorHandlerAbstract errorHandler, XmlValidationResult result) {
    	if (in == null) {
    		throw new IllegalArgumentException("in is null");
    	}
    	if (result == null) {
    		throw new IllegalArgumentException("result is null");
    	}
        if (errorHandler == null) {
            errorHandler = new XmlErrorHandler();
        }
        try {
            /*
             * Validate against DTD/XSD.
             */
            builderValidating.reset();
            builderValidating.setEntityResolver(entityResolver);
            builderValidating.setErrorHandler(errorHandler);
            result.document = builderValidating.parse(in);
            in.close();
            in = null;
        } catch (Throwable t) {
            logger.error("Exception validating XML stream!", t.toString(), t);
            try {
                String publicId = "";
                String systemId = "";
                int lineNumber = -1;
                int columnNumber = -1;
                Exception sillyApiException = new Exception("XML validation exception!", t);
                errorHandler.error(new SAXParseException(t.toString(), publicId, systemId, lineNumber, columnNumber, sillyApiException));
            } catch (SAXException e) {
                throw new IllegalStateException("Failed to add error to ErrorHandler!", e);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Exception closing stream!", e.toString(), e);
                }
                in = null;
            }
        }
        result.bValid = !errorHandler.hasErrors();
        if (result.bValid) {
            result.bWellformed = true;
        }
        return result.bValid;
    }

}

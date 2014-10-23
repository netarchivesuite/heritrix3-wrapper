package org.netarchivesuite.xmlutils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

/**
 * Small command line utility to transform an XML file.
 */
public class XslTransform {

    /**
     * Run method.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: transform <input.xml> <transformer.xsl> <output.xml>");
        } else {
            try {
                File xslFile = new File(args[1]);
                XslTransformer transformer = XslTransformer.getTransformer(xslFile);

                XslUriResolver uriResolver = new XslUriResolver();
                XslErrorListener errorListener = new XslErrorListener();

                File xmlFile = new File(args[0]);
                Source source = new StreamSource(xmlFile);
                byte[] bytes = transformer.transform(source, uriResolver, errorListener);

                // Un-comment to enable DTD/XSL caching.
                /*
                File cacheDir = new File(new File(url.getFile()), "entity_cache");
                if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                    Assert.fail("Could not make entity_cache directory!");
                }
                XmlEntityResolver entityResolver = new XmlEntityResolver(cacheDir);
                */

                XmlEntityResolver entityResolver = null;
                XmlErrorHandler errorHandler = new XmlErrorHandler();

                XmlValidator xmlValidator = new XmlValidator();
                XmlValidationResult result;

                File outputFile = new File(args[2]);
                RandomAccessFile raf = new RandomAccessFile(outputFile, "rw");
                raf.seek(0);
                raf.setLength(0);
                raf.write(bytes);
                raf.close();
                result = xmlValidator.validate(outputFile, entityResolver, errorHandler);
                if (result != null) {
                    System.out.println("       bDtd: " + result.bDtdUsed);
                    System.out.println("       bXsd: " + result.bXsdUsed);
                    System.out.println("bWellformed: " + result.bWellformed);
                    System.out.println("     bValid: " + result.bValid);
                } else {
                    System.out.println("Unable to validate file!");
                }
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

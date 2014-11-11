package org.netarchivesuite.heritrix3wrapper.xmlutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implements a caching XML entity resolver. Resolves and caches DTD and XSD.
 */
public class XmlEntityResolver implements EntityResolver {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(XmlEntityResolver.class.getName());

    /** Path to cache directory. */
    protected File cache_dir;

    /**
     * Construct a caching XML entity resolver instance.
     * @param cache_dir path to cache directory
     */
    public XmlEntityResolver(File cache_dir) {
        this.cache_dir = cache_dir;
        if (!cache_dir.exists()) {
            cache_dir.mkdirs();
        }
    }

    @Override
    public synchronized InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        logger.info("Resolving: " + systemId + " (\"" + publicId + "\")");
        if (systemId != null) {
            int pos = systemId.lastIndexOf("/");
            if (pos != -1) {
                String res = systemId.substring(pos + 1);
                File file = new File(cache_dir, res);
                if (file.exists() && file.isFile()) {
                    /*
                     * Load cached dtd.
                     */
                    try {
                        logger.info(" Loading cached: " + file.getAbsolutePath());
                        return new InputSource(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
                    } catch (FileNotFoundException e) {
                        logger.warn(e.toString(), e);
                        return null;
                    }
                } else {
                    /*
                     * Attempt to cache dtd.
                     */
                    URL url;
                    InputStream in = null;
                    FileOutputStream out = null;
                    try {
                        byte[] buffer = new byte[1024];
                        int len;
                        url = new URL(systemId);
                        in = url.openStream();
                        out = new FileOutputStream(file);
                        while ((len = in.read( buffer, 0, 1024)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        out.close();
                        in.close();
                        logger.info(" Saving cached: " + file.getAbsolutePath());
                        return new InputSource(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
                    } catch (MalformedURLException e) {
                        logger.warn(e.toString(), e);
                        return null;
                    } catch (IOException e) {
                        logger.warn(e.toString(), e);
                        return null;
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {}
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {}
                    }
                }
            }
        }
        return null;
    }

}

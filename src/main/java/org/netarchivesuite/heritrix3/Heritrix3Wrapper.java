package org.netarchivesuite.heritrix3;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.netarchivesuite.xmlutils.XmlValidator;

public class Heritrix3Wrapper {

    protected DefaultHttpClient httpClient;

    protected String baseUrl;

    protected XmlValidator xmlValidator = new XmlValidator();

    protected Heritrix3Wrapper() {
    }

    public static Heritrix3Wrapper getInstance(String hostname, int port, File keystoreFile, String keyStorePassword,
            String userName, String password) {
        Heritrix3Wrapper h3 = new Heritrix3Wrapper();
        InputStream instream = null;
        KeyStore ks;
        SSLSocketFactory socketFactory;
        Scheme sch;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(keystoreFile);
            ks.load(instream, keyStorePassword.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyStorePassword.toCharArray());

            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), new TrustManager[] {tm}, null);

            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            sch = new Scheme("https", socketFactory, port);
            h3.httpClient = new DefaultHttpClient();
            h3.httpClient.getConnectionManager().getSchemeRegistry().register(sch);
            h3.httpClient.getCredentialsProvider().setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(userName, password));
            h3.baseUrl = "https://" + hostname + ":" + Integer.toString(port) + "/engine/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException e) {
                }
            }
        }
        return h3;
    }

    /**
     * Send JVM shutdown to Heritrix 3.
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public EngineResult exitJavaProcess() throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "Exit Java Process"));
        nvp.add(new BasicNameValuePair("im_sure", "on"));
        StringEntity postEntity = new UrlEncodedFormEntity(nvp);
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        try {
            return engineResult(postRequest);
        } catch (NoHttpResponseException e) {
            return null;
        }
    }

    /**
     * Invoke GarbageCollector in JVM running Heritrix 3.
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public EngineResult gc() throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl);
        StringEntity postEntity = new StringEntity(new String("action=gc"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * Returns engine state and a list of registered jobs.
     * @throws ClientProtocolException
     * @throws IOException
     * @return engine state and a list of registered jobs
     */
    public EngineResult rescanJobDirectory() throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl);
        StringEntity postEntity = new StringEntity(new String("action=rescan"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * Creates a new job with the default cxml file which must be modified before launch.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public EngineResult createNewJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "create"));
        nvp.add(new BasicNameValuePair("createpath", jobname));
        StringEntity postEntity = new UrlEncodedFormEntity(nvp);
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    public EngineResult addJobDirectory(String path) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "add"));
        nvp.add(new BasicNameValuePair("addpath", path));
        StringEntity postEntity = new UrlEncodedFormEntity(nvp);
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    public EngineResult engineResult(HttpRequestBase request) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        EngineResult engineResult = null;
        HttpResponse response = httpClient.execute(request);
        if (response != null) {
            engineResult = new EngineResult();
            engineResult.responseCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            long contentLength = responseEntity.getContentLength();
            if (contentLength < 0) {
                contentLength = 0;
            }
            ByteArrayOutputStream bOut = new ByteArrayOutputStream((int) contentLength);
            InputStream in = responseEntity.getContent();
            int read;
            byte[] tmpBuf = new byte[8192];
            while ((read = in.read(tmpBuf)) != -1) {
                bOut.write(tmpBuf, 0, read);
            }
            in.close();
            bOut.close();
            engineResult.response = bOut.toByteArray();
            switch (engineResult.responseCode) {
            case 200:
                engineResult.parse(xmlValidator);
                break;
            case 404:
                break;
            case 500:
                break;
            default:
                break;
            }
        }
        return engineResult;
    }

    public JobResult jobResult(HttpRequestBase request) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        JobResult jobResult = null;
        HttpResponse response = httpClient.execute(request);
        if (response != null) {
            jobResult = new JobResult();
            jobResult.responseCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            long contentLength = responseEntity.getContentLength();
            if (contentLength < 0) {
                contentLength = 0;
            }
            ByteArrayOutputStream bOut = new ByteArrayOutputStream((int) contentLength);
            InputStream in = responseEntity.getContent();
            int read;
            byte[] tmpBuf = new byte[8192];
            while ((read = in.read(tmpBuf)) != -1) {
                bOut.write(tmpBuf, 0, read);
            }
            in.close();
            bOut.close();
            jobResult.response = bOut.toByteArray();
            switch (jobResult.responseCode) {
            case 200:
                jobResult.parse(xmlValidator);
                break;
            case 404:
                break;
            case 500:
                break;
            default:
                break;
            }
        }
        return jobResult;
    }

    /**
     * Returns job object given a jobname.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult job(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpGet getRequest = new HttpGet(baseUrl + "job/" + jobname);
        getRequest.addHeader("Accept", "application/xml");
        return jobResult(getRequest);
    }

    public JobResult copyJob(String srcJobname, String dstJobName, boolean bAsProfile) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + srcJobname);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("copyTo", dstJobName));
        if (bAsProfile) {
            nvp.add(new BasicNameValuePair("asProfile", "on"));
        }
        StringEntity postEntity = new UrlEncodedFormEntity(nvp);
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Build an existing job.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult buildJobConfiguration(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=build"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Teardown job and return it's state to unbuild. Note the job object will not include all the information present before calling this method.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult teardownJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=teardown"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Launch a built job in pause state.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult launchJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=launch"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Pause running job.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult pauseJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=pause"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Un-pause job.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult unpauseJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=unpause"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Checkpoint job.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult checkpointJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=checkpoint"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Terminate running job.
     * @param jobname
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JAXBException
     * @throws XMLStreamException
     */
    public JobResult terminateJob(String jobname) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = new StringEntity(new String("action=terminate"));
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    public JobResult test(String url) throws ClientProtocolException, IOException, JAXBException, XMLStreamException {
        HttpGet getRequest = new HttpGet(url);
        getRequest.addHeader("Accept", "application/xml");
        return jobResult(getRequest);
    }

    /*
     * [beanshell,js,groovy,AppleScriptEngine]
     */
    public void ExecuteShellScriptInJob(String jobname, String engine, String script) throws ClientProtocolException, IOException {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname + "/script");
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("engine", engine));
        nvp.add(new BasicNameValuePair("script", script));
        StringEntity postEntity = new UrlEncodedFormEntity(nvp);
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);

        HttpResponse response = httpClient.execute(postRequest);
        if (response != null) {
            int responseCode = response.getStatusLine().getStatusCode();
            System.out.println(responseCode);
            HttpEntity responseEntity = response.getEntity();
            InputStream in = responseEntity.getContent();
            BufferedReader sr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = sr.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
        }
    }

    public static void copyFile(File srcFile, File dstDir) throws IOException {
        RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
        FileChannel srcChannel = srcRaf.getChannel();
        File dstFile = new File(dstDir, srcFile.getName());
        RandomAccessFile dstRaf = new RandomAccessFile(dstFile, "rw");
        dstRaf.seek(0);
        dstRaf.setLength(0);
        FileChannel dstChannel = dstRaf.getChannel();
        long position = 0;
        long count = srcRaf.length();
        long transferred;
        while (count > 0) {
            transferred = srcChannel.transferTo(position, count, dstChannel);
            position += transferred;
            count -= transferred;
        }
        dstRaf.close();
        srcRaf.close();
        dstFile.setLastModified(srcFile.lastModified());
    }

}

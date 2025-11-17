package org.netarchivesuite.heritrix3wrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.netarchivesuite.heritrix3wrapper.jaxb.Engine;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.netarchivesuite.heritrix3wrapper.jaxb.Script;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Heritrix3Wrapper {
	
	private static Logger logger = LoggerFactory.getLogger(Heritrix3Wrapper.class);

	/** Wrapped <code>HttpClient</code> object used to communicate with Heritrix 3. */
    protected HttpClient httpClient;

    /** Heritrix 3 host name. */
    public String hostname;

    /** Heritrix 3 hpst port. */
    public int port;

    /** Heritrix 3 engine base url. */
    protected String baseUrl;

    protected XmlValidator xmlValidator = new XmlValidator();

    /** GC action request form string. */
    private static final String GC_ACTION = "action=gc";
    /** Rescan action request form string. */
    private static final String RESCAN_ACTION = "action=rescan";
    /** Build action request form string. */
    private static final String BUILD_ACTION = "action=build";
    /** Launch action request form string. */
    private static final String LAUNCH_ACTION = "action=launch";
    /** Teardown action request form string. */
    private static final String TEARDOWN_ACTION = "action=teardown";
    /** Pause action request form string. */
    private static final String PAUSE_ACTION = "action=pause";
    /** Unpause action request form string. */
    private static final String UNPAUSE_ACTION = "action=unpause";
    /** Terminate action request form string. */
    private static final String TERMINATE_ACTION = "action=terminate";
    /** Checkpoint action request form string. */
    private static final String CHECKPOINT_ACTION = "action=checkpoint";

    public static enum CrawlControllerState {
        NASCENT, RUNNING, EMPTY, PAUSED, PAUSING, STOPPING, FINISHED, PREPARING
    }

    protected Heritrix3Wrapper() {
    }

    public static Heritrix3Wrapper getInstance(String hostname, int port, File keystoreFile, String keyStorePassword, String userName, String password) {
        Heritrix3Wrapper h3 = new Heritrix3Wrapper();
        InputStream instream = null;
        KeyManager[] keyManagers = null;
        try {
            if (keystoreFile != null) {
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                instream = new FileInputStream(keystoreFile);
                ks.load(instream, keyStorePassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, keyStorePassword.toCharArray());
                keyManagers = kmf.getKeyManagers();
            }

            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(keyManagers, new TrustManager[] {tm}, null);

            X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            //SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);
            //socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            //Scheme sch = new Scheme("https", socketFactory, port);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(userName, password));

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            //httpClientBuilder.setSSLSocketFactory(sslSocketFactory).setHostnameVerifier(hostnameVerifier);
            httpClientBuilder.setSslcontext(sslcontext);
            httpClientBuilder.setHostnameVerifier(hostnameVerifier);
            // Add retry handler in case of NoHttpResponseException
            httpClientBuilder.setRetryHandler((exception, executionCount, context) -> {
                if (executionCount >= 3) { return false; }
                if (exception instanceof org.apache.http.NoHttpResponseException) {
                    logger.warn("Retry after NoHttpResponseException: try #{}", executionCount);
                    return true;
                }
                return false;
            });
            h3.hostname = hostname;
            h3.port = port;
            h3.httpClient = httpClientBuilder.setDefaultCredentialsProvider(credsProvider).build();
            //h3.httpClient.getConnectionManager().getSchemeRegistry().register(sch);
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
     * Send JVM shutdown action to Heritrix 3.
     * If there are any running jobs they must be included in the ignored jobs list passed as argument.
     * Otherwise the shutdown action is ignored.
     * @return <code>EngineResult</code>, but only in case the shutdown was not performed
     */
    public EngineResult exitJavaProcess(List<String> ignoreJobs) {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        // 3.2.x
        //nvp.add(new BasicNameValuePair("action", "Exit Java Process"));
        // 3.3.x
        nvp.add(new BasicNameValuePair("action", "exit java process"));
        nvp.add(new BasicNameValuePair("im_sure", "on"));
        // ignore__${jobname}=on
        if (ignoreJobs != null && ignoreJobs.size() > 0) {
            for (int i=0; i<ignoreJobs.size(); ++i) {
                nvp.add(new BasicNameValuePair("ignore__" + ignoreJobs.get(i), "on"));
            }
        }
        StringEntity postEntity = null;
        try {
            postEntity = new UrlEncodedFormEntity(nvp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * TODO
     * @param url
     * @param action
     * @return
     */
    public EngineResult postEngineRequest(String url, String action) {
        HttpPost postRequest = new HttpPost(url);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(action);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * Scans the job directories and returns the engine state and a list of registered jobs.
     * @return engine state and a list of registered jobs
     */
    public EngineResult rescanJobDirectory() {
        return postEngineRequest(baseUrl, RESCAN_ACTION);
    }

    /**
     * Invoke GarbageCollector in JVM running Heritrix 3.
     * @return engine state and a list of registered jobs
     */
    public EngineResult gc() {
        return postEngineRequest(baseUrl, GC_ACTION);
    }
    
    /**
     * TODO
     * @param tries
     * @param interval
     * @return engine state and a list of registered jobs
     */
    public EngineResult waitForEngineReady(int tries, int interval) {
        EngineResult engineResult = null;
        if (tries <= 0) {
            tries = 1;
        }
        if (interval <= 99) {
            interval = 1000;
        }
        boolean bLoop = true;
        while (bLoop && tries > 0) {
            engineResult = rescanJobDirectory();
            // debug
            //System.out.println(engineResult.status + " - " + ResultStatus.OK);
            if (engineResult.status == ResultStatus.OK) {
                bLoop = false;
            }
            --tries;
            if (bLoop && tries > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                }
            }
        }
        return engineResult;
    }

    /**
     * Creates a new job and initialises it with the default cxml file which must be modified before launch.
     * @param jobname name of the new job
     * @return engine state and a list of registered jobs
     */
    public EngineResult createNewJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "create"));
        nvp.add(new BasicNameValuePair("createpath", jobname));
        StringEntity postEntity = null;
        try {
            postEntity = new UrlEncodedFormEntity(nvp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * Add a new job directory to Heritrix 3.
     * @param path new job directory
     * @return engine state and a list of registered jobs
     */
    public EngineResult addJobDirectory(String path) {
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "add"));
        nvp.add(new BasicNameValuePair("addpath", path));
        StringEntity postEntity= null;
        try {
            postEntity = new UrlEncodedFormEntity(nvp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return engineResult(postRequest);
    }

    /**
     * Process the engine result XML and turn it into a Java object.
     * @param request HTTP request
     * @return engine state and a list of registered jobs
     */
    public EngineResult engineResult(HttpRequestBase request) {
        EngineResult engineResult = new EngineResult();
        try {
            HttpResponse response = httpClient.execute(request);
            if (response != null) {
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
                    in = new ByteArrayInputStream(engineResult.response);
                    engineResult.engine = Engine.unmarshall(in);
                    in.close();
                    engineResult.status = ResultStatus.OK;
                    break;
                case 404:
                    engineResult.status = ResultStatus.NOT_FOUND;
                    break;
                case 500:
                    engineResult.status = ResultStatus.INTERNAL_ERROR;
                    break;
                default:
                    engineResult.status = ResultStatus.NO_RESPONSE;
                    break;
                }
            } else {
                engineResult.status = ResultStatus.NO_RESPONSE;
            }
        } catch (NoHttpResponseException e) {
            engineResult.status = ResultStatus.OFFLINE;
            engineResult.t = e;
        } catch (ClientProtocolException e) {
            engineResult.status = ResultStatus.RESPONSE_EXCEPTION;
            engineResult.t = e;
        } catch (IOException e) {
            engineResult.status = ResultStatus.RESPONSE_EXCEPTION;
            engineResult.t = e;
        } catch (JAXBException e) {
            engineResult.status = ResultStatus.JAXB_EXCEPTION;
            engineResult.t = e;
        } catch (XMLStreamException e) {
            engineResult.status = ResultStatus.XML_EXCEPTION;
            engineResult.t = e;
        }
        return engineResult;
    }

    /**
     * Send a job request and process the XML response and turn it into a Java object.
     * @param request HTTP request
     * @return job state
     */
    public JobResult jobResult(HttpRequestBase request) {
        JobResult jobResult = new JobResult();
        try {
            HttpResponse response = httpClient.execute(request);
            if (response != null) {
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
                    in = new ByteArrayInputStream(jobResult.response);
                    jobResult.job = Job.unmarshall(in);
                    in.close();
                    jobResult.status = ResultStatus.OK;
                    break;
                case 404:
                    jobResult.status = ResultStatus.NOT_FOUND;
                    break;
                case 500:
                    jobResult.status = ResultStatus.INTERNAL_ERROR;
                    break;
                default:
                    jobResult.status = ResultStatus.NO_RESPONSE;
                    break;
                }
            } else {
                jobResult.status = ResultStatus.NO_RESPONSE;
            }
        } catch (NoHttpResponseException e) {
            jobResult.status = ResultStatus.OFFLINE;
            jobResult.t = e;
        } catch (ClientProtocolException e) {
            jobResult.status = ResultStatus.RESPONSE_EXCEPTION;
            jobResult.t = e;
        } catch (IOException e) {
            jobResult.status = ResultStatus.RESPONSE_EXCEPTION;
            jobResult.t = e;
        } catch (JAXBException e) {
            jobResult.status = ResultStatus.JAXB_EXCEPTION;
            jobResult.t = e;
        } catch (XMLStreamException e) {
            jobResult.status = ResultStatus.XML_EXCEPTION;
            jobResult.t = e;
        }
        return jobResult;
    }

    /**
     * Returns the job state object given a jobname.
     * @param jobname job name
     * @return job state
     */
    public JobResult job(String jobname) {
        HttpGet getRequest = new HttpGet(baseUrl + "job/" + jobname);
        getRequest.addHeader("Accept", "application/xml");
        return jobResult(getRequest);
    }

    /**
     * TODO
     * @param jobname
     * @param state
     * @param tries
     * @param interval
     * @return
     */
    public JobResult waitForJobState(String jobname, CrawlControllerState state, int tries, int interval) {
        JobResult jobResult = null;
        if (tries <= 0) {
            tries = 1;
        }
        if (interval <= 99) {
            interval = 1000;
        }
        boolean bLoop = true;
        while (bLoop && tries > 0) {
            jobResult = job(jobname);
            // debug
            //System.out.println(jobResult.status + " - " + ResultStatus.OK);
            if (jobResult.status == ResultStatus.OK) {
                // debug
                //System.out.println(jobResult.job.crawlControllerState + " - " + state.name());
                if ((state == null && jobResult.job.crawlControllerState == null) || (state != null && state.name().equals(jobResult.job.crawlControllerState))) {
                    bLoop = false;
                }
            }
            --tries;
            if (bLoop && tries > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                }
            }
        }
        return jobResult;
    }

    /**
     * Copy a job.
     * @param srcJobname source job name
     * @param dstJobName destination job name
     * @param bAsProfile define if the job should be copied as a profile or not
     * @return job state of new job
     */
    public JobResult copyJob(String srcJobname, String dstJobName, boolean bAsProfile) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + srcJobname);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("copyTo", dstJobName));
        if (bAsProfile) {
            nvp.add(new BasicNameValuePair("asProfile", "on"));
        }
        StringEntity postEntity = null;
        try {
            postEntity = new UrlEncodedFormEntity(nvp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Build an existing job.
     * @param jobname job name
     * @return job state
     */
    public JobResult buildJobConfiguration(String jobname){
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(BUILD_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Teardown job and return it's state to unbuild. Note the job object will not include all the information present after calling this method.
     * @param jobname job name
     * @return job state
     */
    public JobResult teardownJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(TEARDOWN_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Launch a built job in pause state.
     * @param jobname job name
     * @return job state
     */
    public JobResult launchJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(LAUNCH_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Pause running job.
     * @param jobname job name
     * @return job state
     */
    public JobResult pauseJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(PAUSE_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Un-pause job.
     * @param jobname job name
     * @return job state
     */
    public JobResult unpauseJob(String jobname){
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(UNPAUSE_ACTION);
        } catch (UnsupportedEncodingException e) {
            // This should never happen
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Checkpoint job.
     * @param jobname job name
     * @return job state
     */
    public JobResult checkpointJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(CHECKPOINT_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return jobResult(postRequest);
    }

    /**
     * Terminate running job.
     * @param jobname job name
     * @return job state
     */
    public JobResult terminateJob(String jobname) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname);
        StringEntity postEntity = null;
        try {
            postEntity = new StringEntity(TERMINATE_ACTION);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
    public ScriptResult ExecuteShellScriptInJob(String jobname, String engine, String script) {
        HttpPost postRequest = new HttpPost(baseUrl + "job/" + jobname + "/script");
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("engine", engine));
        nvp.add(new BasicNameValuePair("script", script));
        // GZip data
        ByteArrayEntity postEntity = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvp);
            postEntity = new ByteArrayEntity(compressData(entity));
            postEntity.setContentType("application/x-www-form-urlencoded");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postRequest.addHeader("Accept", "application/xml");
        postRequest.addHeader("Content-Encoding", "gzip");
        postRequest.setEntity(postEntity);
        return scriptResult(postRequest);
    }

    private byte[] compressData(UrlEncodedFormEntity entity) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            entity.writeTo(gzip);
        } catch (IOException e) {
            logger.error("Cannot gzip data", e);
        }
        return bos.toByteArray();
    }

    public ScriptResult scriptResult(HttpRequestBase request) {
        ScriptResult scriptResult = new ScriptResult();
        try {
            HttpResponse response = httpClient.execute(request);
            if (response != null) {
                scriptResult.responseCode = response.getStatusLine().getStatusCode();
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
                scriptResult.response = bOut.toByteArray();
                switch (scriptResult.responseCode) {
                case 200:
                    scriptResult.parse(xmlValidator);
                    in = new ByteArrayInputStream(scriptResult.response);
                    scriptResult.script = Script.unmarshall(in);
                    in.close();
                    scriptResult.status = ResultStatus.OK;
                    break;
                case 404:
                    scriptResult.status = ResultStatus.NOT_FOUND;
                    break;
                case 500:
                    scriptResult.status = ResultStatus.INTERNAL_ERROR;
                    break;
                default:
                    scriptResult.status = ResultStatus.NO_RESPONSE;
                    break;
                }
            } else {
                scriptResult.status = ResultStatus.NO_RESPONSE;
            }
        } catch (NoHttpResponseException e) {
            logger.error("Error NoHttpResponseException execute httpClient", e);
            scriptResult.status = ResultStatus.OFFLINE;
            scriptResult.t = e;
        } catch (ClientProtocolException e) {
            logger.error("Error ClientProtocolException execute httpClient", e);
            scriptResult.status = ResultStatus.RESPONSE_EXCEPTION;
            scriptResult.t = e;
        } catch (IOException e) {
            logger.error("Error IOException execute httpClient", e);
            scriptResult.status = ResultStatus.RESPONSE_EXCEPTION;
            scriptResult.t = e;
        } catch (JAXBException e) {
            logger.error("Error JAXBException execute httpClient", e);
            scriptResult.status = ResultStatus.JAXB_EXCEPTION;
            scriptResult.t = e;
        } catch (XMLStreamException e) {
            logger.error("Error XMLStreamException execute httpClient", e);
            scriptResult.status = ResultStatus.XML_EXCEPTION;
            scriptResult.t = e;
        }
        return scriptResult;
    }

    public StreamResult anypath(String path, Long from, Long to, boolean bHeadRequest) {
        return path("anypath/" + path, from, to, bHeadRequest);
    }

    public StreamResult anypath(String path, Long from, Long to) {
        return path("anypath/" + path, from, to, false);
    }

    public StreamResult path(String path, Long from, Long to) {
        return path(path, from, to, false);
    }

    public StreamResult path(String path, Long from, Long to, boolean bHeadRequest) {
        StreamResult anypathResult = new StreamResult();
        HttpRequestBase request;
        if (bHeadRequest) {
            request = new HttpHead(baseUrl + path);
        } else {
            request = new HttpGet(baseUrl + path);
        }
        if (from != null) {
            request.addHeader("Accept-Ranges", "bytes");
            if (to != null) {
                request.addHeader("Range", "bytes=" + Long.toString(from) + "-" + Long.toString(to));
            } else {
                request.addHeader("Range", "bytes=" + Long.toString(from) + "-");
            }
        }
        try {
            HttpResponse response = httpClient.execute(request);
            if (response != null) {
                anypathResult.responseCode = response.getStatusLine().getStatusCode();
                Header header;
                HttpEntity responseEntity = response.getEntity();
                long contentLength;
                if (responseEntity != null) {
                    contentLength = responseEntity.getContentLength();
                    if (contentLength < 0) {
                        contentLength = 0;
                    }
                } else {
                    header = response.getFirstHeader("Content-Length");
                    if (header != null) {
                    	try {
                            contentLength = Long.parseLong(header.getValue());
                    	} catch (NumberFormatException e) {
                            contentLength = 0;
                    	}
                    } else {
                        contentLength = 0;
                    }
                }
                anypathResult.contentLength = contentLength;
                if (from != null) {
                    header = response.getFirstHeader("Content-Range");
                    if (header != null) {
                        anypathResult.byteRange = ByteRange.parse(header.getValue());
                    }
                }
                if (!bHeadRequest) {
                    anypathResult.in = responseEntity.getContent();
                }
                switch (anypathResult.responseCode) {
                case 200:
                    anypathResult.status = ResultStatus.OK;
                    break;
                case 206:
                	if (!bHeadRequest && anypathResult.byteRange != null) {
                        anypathResult.status = ResultStatus.OK;
                	} else {
                        anypathResult.status = ResultStatus.INTERNAL_ERROR;
                	}
                	break;
                case 404:
                    anypathResult.status = ResultStatus.NOT_FOUND;
                    break;
                case 500:
                    anypathResult.status = ResultStatus.INTERNAL_ERROR;
                    break;
                default:
                    anypathResult.status = ResultStatus.NO_RESPONSE;
                    break;
                }
            } else {
                anypathResult.status = ResultStatus.NO_RESPONSE;
            }
        } catch (NoHttpResponseException e) {
            anypathResult.status = ResultStatus.OFFLINE;
            anypathResult.t = e;
        } catch (ClientProtocolException e) {
            anypathResult.status = ResultStatus.RESPONSE_EXCEPTION;
            anypathResult.t = e;
        } catch (IOException e) {
            anypathResult.status = ResultStatus.RESPONSE_EXCEPTION;
            anypathResult.t = e;
        }
        return anypathResult;
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

    public static void copyFileAs(File srcFile, File dstDir, String dstFName) throws IOException {
        RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
        FileChannel srcChannel = srcRaf.getChannel();
        File dstFile = new File(dstDir, dstFName);
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

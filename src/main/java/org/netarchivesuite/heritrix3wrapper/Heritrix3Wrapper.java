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

import javax.net.ssl.KeyManager;
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
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.netarchivesuite.heritrix3wrapper.jaxb.Engine;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.netarchivesuite.heritrix3wrapper.jaxb.Script;
import org.netarchivesuite.heritrix3wrapper.xmlutils.XmlValidator;

public class Heritrix3Wrapper {

    protected HttpClient httpClient;

    protected String baseUrl;

    protected XmlValidator xmlValidator = new XmlValidator();

    private static final String GC_ACTION= "action=gc";
	private static final String RESCAN_ACTION = "action=rescan";
	private static final String BUILD_ACTION = "action=build";
	private static final String LAUNCH_ACTION = "action=launch";
	private static final String TEARDOWN_ACTION = "action=teardown";
	private static final String PAUSE_ACTION = "action=pause";
	private static final String UNPAUSE_ACTION = "action=unpause";
	private static final String TERMINATE_ACTION = "action=terminate";
	private static final String CHECKPOINT_ACTION = "action=checkpoint";
    
    public static enum CrawlControllerState {
        NASCENT, RUNNING, EMPTY, PAUSED, PAUSING, 
        STOPPING, FINISHED, PREPARING 
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

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagers, new TrustManager[] {tm}, null);

            X509HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);
            //socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            //Scheme sch = new Scheme("https", socketFactory, port);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(userName, password));

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setSSLSocketFactory(sslSocketFactory).setHostnameVerifier(hostnameVerifier);
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
     * Send JVM shutdown to Heritrix 3. Also requres marked 'checkbox' for each active job.
     * @return
     * @throws UnsupportedEncodingException 
     */
    public EngineResult exitJavaProcess(List<String> ignoreJobs){
        HttpPost postRequest = new HttpPost(baseUrl);
        List<NameValuePair> nvp = new LinkedList<NameValuePair>();
        nvp.add(new BasicNameValuePair("action", "Exit Java Process"));
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
     * Returns engine state and a list of registered jobs.
     * @return engine state and a list of registered jobs
     * @throws UnsupportedEncodingException 
     */
    public EngineResult rescanJobDirectory() {
    	return postEngineRequest(baseUrl, RESCAN_ACTION);
    }

    /**
     * Invoke GarbageCollector in JVM running Heritrix 3.
     * @return
     * @throws UnsupportedEncodingException 
     */
    public EngineResult gc() {
    	return postEngineRequest(baseUrl, GC_ACTION);
    }
    
    /**
     * 
     * @param tries
     * @param interval
     * @return
     * @throws UnsupportedEncodingException
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
            System.out.println(engineResult.status + " - " + ResultStatus.OK);
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
     * Creates a new job with the default cxml file which must be modified before launch.
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
     * 
     * @param path
     * @return
     * @throws UnsupportedEncodingException 
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
            engineResult.status = ResultStatus.XML_EXCEPTION;
            engineResult.t = e;
        } catch (XMLStreamException e) {
            engineResult.status = ResultStatus.JAXB_EXCEPTION;
            engineResult.t = e;
        }
        return engineResult;
    }

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
            jobResult.status = ResultStatus.XML_EXCEPTION;
            jobResult.t = e;
        } catch (XMLStreamException e) {
            jobResult.status = ResultStatus.JAXB_EXCEPTION;
            jobResult.t = e;
        }
        return jobResult;
    }

    /**
     * Returns job object given a jobname.
     * @param jobname
     * @return
     */
    public JobResult job(String jobname) {
        HttpGet getRequest = new HttpGet(baseUrl + "job/" + jobname);
        getRequest.addHeader("Accept", "application/xml");
        return jobResult(getRequest);
    }

    public JobResult waitForJobState(String jobname, CrawlControllerState state, int tries, int interval) throws UnsupportedEncodingException {
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
            System.out.println(jobResult.status + " - " + ResultStatus.OK);
            if (jobResult.status == ResultStatus.OK) {
                // debug
                System.out.println(jobResult.job.crawlControllerState + " - " + state.name());
                if (state.name().equals(jobResult.job.crawlControllerState)) {
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
     * 
     * @param srcJobname
     * @param dstJobName
     * @param bAsProfile
     * @return
     * @throws UnsupportedEncodingException 
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
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
     * Teardown job and return it's state to unbuild. Note the job object will not include all the information present before calling this method.
     * @param jobname
     * @return
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
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
     * @param jobname
     * @return
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
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
     * @param jobname
     * @return
     * @throws UnsupportedEncodingException 
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
        StringEntity postEntity = null;
		try {
			postEntity = new UrlEncodedFormEntity(nvp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        postEntity.setContentType("application/x-www-form-urlencoded");
        postRequest.addHeader("Accept", "application/xml");
        postRequest.setEntity(postEntity);
        return scriptResult(postRequest);
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
            scriptResult.status = ResultStatus.OFFLINE;
            scriptResult.t = e;
        } catch (ClientProtocolException e) {
            scriptResult.status = ResultStatus.RESPONSE_EXCEPTION;
            scriptResult.t = e;
        } catch (IOException e) {
            scriptResult.status = ResultStatus.RESPONSE_EXCEPTION;
            scriptResult.t = e;
        } catch (JAXBException e) {
            scriptResult.status = ResultStatus.XML_EXCEPTION;
            scriptResult.t = e;
        } catch (XMLStreamException e) {
            scriptResult.status = ResultStatus.JAXB_EXCEPTION;
            scriptResult.t = e;
        }
        return scriptResult;
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

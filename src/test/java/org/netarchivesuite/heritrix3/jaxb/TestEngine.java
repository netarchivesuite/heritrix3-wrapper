package org.netarchivesuite.heritrix3.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestEngine {

    protected static ClassLoader clsLdr = TestEngine.class.getClassLoader();

    public static final File getTestResourceFile(String fname) {
        URL url = clsLdr.getResource(fname);
        String path = url.getFile();
        path = path.replaceAll("%5b", "[");
        path = path.replaceAll("%5d", "]");
        File file = new File(path);
        return file;
    }

    @Test
    public void test_engine() {
        File engineXmlFile = getTestResourceFile("jaxb/engine-example.xml");
        if (!engineXmlFile.exists()) {
            Assert.fail("Test data missing, 'jaxb/engine-example.xml'");
        }
        Engine engine;
        try {
            InputStream in = new FileInputStream(engineXmlFile);
            engine = Engine.unmarshall(in);
            in.close();
            AssertEngine(engine);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Engine.marshall(engine, out);
            out.close();
            byte[] xml = out.toByteArray();
            // debug
            //System.out.println(new String(xml, "UTF-8"));
            in = new ByteArrayInputStream(xml);
            engine = Engine.unmarshall(in);
            in.close();
            AssertEngine(engine);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Unexpected exception!");
        }
    }

    public void AssertEngine(Engine engine) {
        Assert.assertNotNull(engine);
        Assert.assertEquals("3.2.0", engine.heritrixVersion);
        HeapReport heapReport = engine.heapReport;
        Assert.assertNotNull(heapReport);
        Assert.assertEquals(new Long(42326696), heapReport.usedBytes);
        Assert.assertEquals(new Long(251658240), heapReport.totalBytes);
        Assert.assertEquals(new Long(251658240), heapReport.maxBytes);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/jobs", engine.jobsDir);
        Assert.assertEquals("https://192.168.1.101:6443/engine/jobsdir/", engine.jobsDirUrl);
        String[] expectedActions = {
                "rescan",
                "add",
                "create"
        };
        TestStructures.assertStringList(expectedActions, engine.availableActions);
        List<JobShort> jobs = engine.jobs;
        Assert.assertNotNull(jobs);
        Assert.assertEquals(1, jobs.size());
        JobShort job = jobs.get(0);
        Assert.assertEquals("11413812355408", job.shortName);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/11413812355408", job.url);
        Assert.assertEquals(false, job.isProfile);
        Assert.assertEquals(new Integer(0), job.launchCount);
        Assert.assertEquals(null, job.lastLaunch);
        Assert.assertEquals(false, job.hasApplicationContext);
        Assert.assertEquals("Unbuilt", job.statusDescription);
        Assert.assertEquals(false, job.isLaunchInfoPartial);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/jobs/11413812355408/crawler-beans.cxml", job.primaryConfig);
        Assert.assertEquals("https://192.168.1.101:6443/engine/jobdir/crawler-beans.cxml", job.primaryConfigUrl);
        Assert.assertEquals("11413812355408", job.key);
    }

}

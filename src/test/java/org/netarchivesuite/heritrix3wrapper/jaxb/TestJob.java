package org.netarchivesuite.heritrix3wrapper.jaxb;

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
import org.netarchivesuite.heritrix3wrapper.jaxb.ConfigFile;
import org.netarchivesuite.heritrix3wrapper.jaxb.ElapsedReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.FrontierReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.HeapReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.Job;
import org.netarchivesuite.heritrix3wrapper.jaxb.LoadReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.RateReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.Report;
import org.netarchivesuite.heritrix3wrapper.jaxb.SizeTotalsReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.ThreadReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.UriTotalsReport;
import org.netarchivesuite.heritrix3wrapper.jaxb.DateFormatLastLaunch.LastLaunchAdadapter;

@RunWith(JUnit4.class)
public class TestJob {

    protected static ClassLoader clsLdr = TestJob.class.getClassLoader();

    public static final File getTestResourceFile(String fname) {
        URL url = clsLdr.getResource(fname);
        String path = url.getFile();
        path = path.replaceAll("%5b", "[");
        path = path.replaceAll("%5d", "]");
        File file = new File(path);
        return file;
    }

    @Test
    public void test_job_unbuilt() {
        File engineXmlFile = getTestResourceFile("jaxb/job-unbuilt.xml");
        if (!engineXmlFile.exists()) {
            Assert.fail("Test data missing, 'jaxb/job-unbuilt.xml'");
        }
        Job job;
        try {
            InputStream in = new FileInputStream(engineXmlFile);
            job = Job.unmarshall(in);
            in.close();
            assertJobUnbuilt(job);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Job.marshall(job, out);
            out.close();
            byte[] xml = out.toByteArray();
            // debug
            //System.out.println(new String(xml, "UTF-8"));
            in = new ByteArrayInputStream(xml);
            job = Job.unmarshall(in);
            in.close();
            assertJobUnbuilt(job);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Unexpected exception!");
        }
    }

    public void assertJobUnbuilt(Job job) throws Exception {
        Assert.assertNotNull(job);
        Assert.assertEquals("1414020288834", job.shortName);
        Assert.assertNull(job.crawlControllerState);
        Assert.assertNull(job.crawlExitStatus);
        Assert.assertEquals("Unbuilt", job.statusDescription);
        String[] expectedActions = {
                "build",
                "launch"
        };
        TestStructures.assertStringList(expectedActions, job.availableActions);
        Assert.assertEquals(new Integer(1), job.launchCount);
        Long lastLaunchLong = new LastLaunchAdadapter().unmarshal("2014-10-23T01:24:50.457+02:00");
        Assert.assertEquals(lastLaunchLong, job.lastLaunch);
        // FIXME marshall uses UTC and not the original timezone
        //String lastLaunchStr = new LastLaunchAdadapter().marshal(lastLaunchLong);
        //Assert.assertEquals("2014-10-23T01:24:50.457+02:00", lastLaunchStr);
        Assert.assertEquals(false, job.isProfile);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/jobs/1414020288834/crawler-beans.cxml", job.primaryConfig);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/1414020288834/jobdir/crawler-beans.cxml", job.primaryConfigUrl);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/1414020288834/job/1414020288834", job.url);
        String[] expectedJobLogTail = {
                "2014-10-23T01:44:53.642+02:00 INFO Job instance discarded",
                "2014-10-23T01:44:50.161+02:00 INFO FINISHED 20141022232451",
                "2014-10-23T01:44:48.386+02:00 INFO EMPTY 20141022232451",
                "2014-10-23T01:44:48.385+02:00 INFO STOPPING 20141022232451",
                "2014-10-23T01:25:52.664+02:00 INFO RUNNING 20141022232451"
        };
        TestStructures.assertStringList(expectedJobLogTail, job.jobLogTail);
        UriTotalsReport uriTotalsReport = job.uriTotalsReport;
        Assert.assertNotNull(null, uriTotalsReport);
        Assert.assertNull(uriTotalsReport.downloadedUriCount);
        Assert.assertNull(uriTotalsReport.queuedUriCount);
        Assert.assertNull(uriTotalsReport.totalUriCount);
        Assert.assertNull(uriTotalsReport.futureUriCount);
        SizeTotalsReport sizeTotalsReport = job.sizeTotalsReport;
        Assert.assertEquals(new Long(0), sizeTotalsReport.dupByHash);
        Assert.assertEquals(new Long(0), sizeTotalsReport.dupByHashCount);
        Assert.assertEquals(new Long(0), sizeTotalsReport.novel);
        Assert.assertEquals(new Long(0), sizeTotalsReport.novelCount);
        Assert.assertEquals(new Long(0), sizeTotalsReport.notModified);
        Assert.assertEquals(new Long(0), sizeTotalsReport.notModifiedCount);
        Assert.assertEquals(new Long(0), sizeTotalsReport.total);
        Assert.assertEquals(new Long(0), sizeTotalsReport.totalCount);
        RateReport rateReport = job.rateReport;
        Assert.assertNotNull(rateReport);
        Assert.assertNull(rateReport.currentDocsPerSecond);
        Assert.assertNull(rateReport.averageDocsPerSecond);
        Assert.assertNull(rateReport.currentKiBPerSec);
        Assert.assertNull(rateReport.averageKiBPerSec);
        LoadReport loadReport = job.loadReport;
        Assert.assertNotNull(loadReport);
        Assert.assertNull(loadReport.busyThreads);
        Assert.assertNull(loadReport.totalThreads);
        Assert.assertNull(loadReport.congestionRatio);
        Assert.assertNull(loadReport.averageQueueDepth);
        Assert.assertNull(loadReport.deepestQueueDepth);
        ElapsedReport elapsedReport = job.elapsedReport;
        Assert.assertNotNull(elapsedReport);
        Assert.assertNull(elapsedReport.elapsedMilliseconds);
        Assert.assertNull(elapsedReport.elapsedPretty);
        ThreadReport threadReport = job.threadReport;
        Assert.assertNotNull(threadReport);
        Assert.assertNull(threadReport.toeCount);
        Assert.assertNull(threadReport.steps);
        Assert.assertNull(threadReport.processors);
        FrontierReport frontierReport = job.frontierReport;
        Assert.assertNotNull(frontierReport);
        Assert.assertNull(frontierReport.totalQueues);
        Assert.assertNull(frontierReport.inProcessQueues);
        Assert.assertNull(frontierReport.readyQueues);
        Assert.assertNull(frontierReport.snoozedQueues);
        Assert.assertNull(frontierReport.activeQueues);
        Assert.assertNull(frontierReport.inactiveQueues);
        Assert.assertNull(frontierReport.ineligibleQueues);
        Assert.assertNull(frontierReport.retiredQueues);
        Assert.assertNull(frontierReport.exhaustedQueues);
        Assert.assertNull(frontierReport.lastReachedState);
        List<String> crawlLogTail = job.crawlLogTail;
        Assert.assertNotNull(crawlLogTail);
        Assert.assertEquals(0, crawlLogTail.size());
        List<ConfigFile> configFiles = job.configFiles;
        Assert.assertNotNull(configFiles);
        Assert.assertEquals(0, configFiles.size());
        Assert.assertEquals(false, job.isLaunchInfoPartial);
        Assert.assertEquals(false, job.isRunning);
        Assert.assertEquals(true, job.isLaunchable);
        Assert.assertEquals(false, job.hasApplicationContext);
        Assert.assertEquals(new Integer(0), job.alertCount);
        String[] expectedCheckpointFiles = {
                ""
        };
        TestStructures.assertStringList(expectedCheckpointFiles, job.checkpointFiles);
        Assert.assertNull(job.alertLogFilePath);
        Assert.assertNull(job.crawlLogFilePath);
        List<Report> reports = job.reports;
        Assert.assertNotNull(reports);
        Assert.assertEquals(0, reports.size());
        HeapReport heapReport = job.heapReport;
        Assert.assertNotNull(heapReport);
        Assert.assertEquals(new Long(64566032), heapReport.usedBytes);
        Assert.assertEquals(new Long(136314880), heapReport.totalBytes);
        Assert.assertEquals(new Long(239075328), heapReport.maxBytes);
    }

    @Test
    public void test_job_finished() {
        File engineXmlFile = getTestResourceFile("jaxb/job-finished.xml");
        if (!engineXmlFile.exists()) {
            Assert.fail("Test data missing, 'jaxb/job-finished.xml'");
        }
        Job job;
        try {
            InputStream in = new FileInputStream(engineXmlFile);
            job = Job.unmarshall(in);
            in.close();
            assertJobFinished(job);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Job.marshall(job, out);
            out.close();
            byte[] xml = out.toByteArray();
            // debug
            //System.out.println(new String(xml, "UTF-8"));
            in = new ByteArrayInputStream(xml);
            job = Job.unmarshall(in);
            in.close();
            assertJobFinished(job);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Unexpected exception!");
        }
    }

    public void assertJobFinished(Job job) throws Exception {
        Assert.assertNotNull(job);
        Assert.assertEquals("1413988654119", job.shortName);
        Assert.assertEquals("FINISHED", job.crawlControllerState);
        Assert.assertEquals("FINISHED", job.crawlExitStatus);
        Assert.assertEquals("Finished: FINISHED", job.statusDescription);
        String[] expectedActions = {
                "teardown"
        };
        TestStructures.assertStringList(expectedActions, job.availableActions);
        Assert.assertEquals(new Integer(1), job.launchCount);
        Long lastLaunchLong = new LastLaunchAdadapter().unmarshal("2014-10-22T16:37:34.654+02:00");
        Assert.assertEquals(lastLaunchLong, job.lastLaunch);
        // FIXME marshall uses UTC and not the original timezone
        //String lastLaunchStr = new LastLaunchAdadapter().marshal(lastLaunchLong);
        //Assert.assertEquals("2014-10-22T16:37:34.654+02:00", lastLaunchStr);
        Assert.assertEquals(false, job.isProfile);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/jobs/1413988654119/crawler-beans.cxml", job.primaryConfig);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/1413988654119/jobdir/crawler-beans.cxml", job.primaryConfigUrl);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/1413988654119/job/1413988654119", job.url);
        String[] expectedJobLogTail = {
                "2014-10-22T17:03:33.432+02:00 INFO FINISHED 20141022143736",
                "2014-10-22T17:03:31.340+02:00 INFO EMPTY 20141022143736",
                "2014-10-22T17:03:31.339+02:00 INFO STOPPING 20141022143736",
                "2014-10-22T16:37:36.240+02:00 INFO RUNNING 20141022143736",
                "2014-10-22T16:37:36.204+02:00 INFO PAUSED 20141022143736"
        };
        TestStructures.assertStringList(expectedJobLogTail, job.jobLogTail);
        UriTotalsReport uriTotalsReport = job.uriTotalsReport;
        Assert.assertNotNull(null, uriTotalsReport);
        Assert.assertEquals(new Long(506), uriTotalsReport.downloadedUriCount);
        Assert.assertEquals(new Long(0), uriTotalsReport.queuedUriCount);
        Assert.assertEquals(new Long(506), uriTotalsReport.totalUriCount);
        Assert.assertEquals(new Long(0), uriTotalsReport.futureUriCount);
        SizeTotalsReport sizeTotalsReport = job.sizeTotalsReport;
        Assert.assertEquals(new Long(0), sizeTotalsReport.dupByHash);
        Assert.assertEquals(new Long(0), sizeTotalsReport.dupByHashCount);
        Assert.assertEquals(new Long(0), sizeTotalsReport.notModified);
        Assert.assertEquals(new Long(0), sizeTotalsReport.notModifiedCount);
        Assert.assertEquals(new Long(269461632), sizeTotalsReport.novel);
        Assert.assertEquals(new Long(506), sizeTotalsReport.novelCount);
        Assert.assertEquals(new Long(269461632), sizeTotalsReport.total);
        Assert.assertEquals(new Long(506), sizeTotalsReport.totalCount);
        RateReport rateReport = job.rateReport;
        Assert.assertNotNull(rateReport);
        Assert.assertEquals(new Double(0.0),rateReport.currentDocsPerSecond);
        Assert.assertEquals(new Double(0.3249428299694901),rateReport.averageDocsPerSecond);
        Assert.assertEquals(new Integer(0), rateReport.currentKiBPerSec);
        Assert.assertEquals(new Integer(168), rateReport.averageKiBPerSec);
        LoadReport loadReport = job.loadReport;
        Assert.assertNotNull(loadReport);
        Assert.assertEquals(new Integer(0), loadReport.busyThreads);
        Assert.assertEquals(new Integer(0), loadReport.totalThreads);
        Assert.assertEquals(new Double(1.0), loadReport.congestionRatio);
        Assert.assertEquals(new Integer(0), loadReport.averageQueueDepth);
        Assert.assertEquals(new Integer(0), loadReport.deepestQueueDepth);
        ElapsedReport elapsedReport = job.elapsedReport;
        Assert.assertNotNull(elapsedReport);
        Assert.assertEquals(new Long(1557197), elapsedReport.elapsedMilliseconds);
        Assert.assertEquals("25m57s197ms", elapsedReport.elapsedPretty);
        ThreadReport threadReport = job.threadReport;
        Assert.assertNotNull(threadReport);
        Assert.assertEquals(new Integer(25), threadReport.toeCount);
        String[] expectedSteps = {
                "25 ABOUT_TO_GET_URI"
        };
        TestStructures.assertStringList(expectedSteps, threadReport.steps);
        String[] expectedProcessors = {
                "25 noActiveProcessor"
        };
        TestStructures.assertStringList(expectedProcessors, threadReport.processors);
        /*
        Assert.assertNull(threadReport.toeCount);
        Assert.assertNull(threadReport.steps);
        Assert.assertNull(threadReport.processors);
        */
        FrontierReport frontierReport = job.frontierReport;
        Assert.assertNotNull(frontierReport);
        Assert.assertEquals(new Integer(19), frontierReport.totalQueues);
        Assert.assertEquals(new Integer(0), frontierReport.inProcessQueues);
        Assert.assertEquals(new Integer(0), frontierReport.readyQueues);
        Assert.assertEquals(new Integer(1), frontierReport.snoozedQueues);
        Assert.assertEquals(new Integer(1), frontierReport.activeQueues);
        Assert.assertEquals(new Integer(0), frontierReport.inactiveQueues);
        Assert.assertEquals(new Integer(0), frontierReport.ineligibleQueues);
        Assert.assertEquals(new Integer(0), frontierReport.retiredQueues);
        Assert.assertEquals(new Integer(18), frontierReport.exhaustedQueues);
        Assert.assertEquals("FINISH", frontierReport.lastReachedState);
        String[] expectecCrawlLogTail = {
                "2014-10-22T15:03:31.232Z   200   14614130 http://av.vimeo.com/43386/781/44508590.mp4?token2=1413990743_808203b084ce3493ec2aac7f0251e8e6&aksessionid=fc90f454a01d470d RLLEX http://player.vimeo.com/video/21669684?byline=0&color=FF5B1B video/mp4 #001 20141022150328990+1820 sha1:RQ4R5BRAEO6NNMXUKTKC6DCGFIHZOVXV - -",
                "2014-10-22T15:02:58.986Z   200   32943198 http://av.vimeo.com/67893/697/44508839.mp4?token2=1413990743_694338712286d35fb15767c8b63da4cd&aksessionid=d98a0f63e8a5e7cb RLLEX http://player.vimeo.com/video/21669684?byline=0&color=FF5B1B video/mp4 #001 20141022150245615+12472 sha1:UPR3X2XSBDNX673JZHQDLJU3H3G4PUBA - -",
                "2014-10-22T15:02:39.614Z   200    6573447 http://av.vimeo.com/57904/840/44508440.mp4?token2=1413990743_b8d9b51f771f6f45c7592f76e93b7ccc&aksessionid=01cd1b7c8a65f352&ns=4 RLLEX http://player.vimeo.com/video/21669684?byline=0&color=FF5B1B video/mp4 #001 20141022150238229+1182 sha1:M43D7E3T7A4SGBMSNXTIS6KWQKLHSMJB - -",
                "2014-10-22T15:02:26.885Z   200   16763066 http://av.vimeo.com/07646/123/44507789.mp4?token2=1413990757_44681831d4b32a3057572d6337d59c13&aksessionid=721c1ee2c4270669 RLLEX http://player.vimeo.com/video/21669425?byline=0&color=FF5B1B video/mp4 #010 20141022150224203+2207 sha1:PAK44YEGM472M43AWHRHUNGUAI7QMUEV - -",
                "2014-10-22T15:02:00.718Z   200   51689462 http://av.vimeo.com/98115/900/44508006.mp4?token2=1413990757_2b709480e201d7d2114bc7cbdb2dafe5&aksessionid=560088b8c6d2cf35&ns=4 RLLEX http://player.vimeo.com/video/21669425?byline=0&color=FF5B1B video/mp4 #002 20141022150154646+4650 sha1:2HV6UB45SZKIEN6LATHTXDNDMDBXVI3O - -",
                "2014-10-22T15:01:31.775Z   404      24881 http://netarkivet.dk/om-netarkivet/publikationer/s RLLLLRLR http://netarkivet.dk/publikationer/index-da.php/%s text/html #007 20141022150131430+337 sha1:NTDT5PZ7FR57CRWEKBQFZ47TCJ4RHBBW - -",
                "2014-10-22T15:01:28.429Z   301       1172 http://netarkivet.dk/publikationer/index-da.php/%s RLLLLRL http://netarkivet.dk/publikationer/index-da.php/ text/html #007 20141022150128379+48 sha1:H7I6KBTSJYO3EJDNOYJABJNQ5KMXJXRG - -",
                "2014-10-22T15:01:25.378Z   301          0 http://netarkivet.dk/publikationer/%s RLLLLL http://netarkivet.dk/publikationer/index-da.php text/html #007 20141022150125140+237 sha1:3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ - -",
                "2014-10-22T15:01:24.643Z   200    6392673 http://av.vimeo.com/21671/994/44507695.mp4?token2=1413990757_8a47236142024b112c6fb7f2b84db987&aksessionid=173167a654b7451f RLLEX http://player.vimeo.com/video/21669425?byline=0&color=FF5B1B video/mp4 #002 20141022150023750+60684 sha1:EHKPLV4HOXP5HX762AS47GRNXTW4CXKT - -",
                "2014-10-22T15:01:22.140Z   301          0 http://netarkivet.dk/?p=91 RLLLEEE http://netarkivet.dk/dk-domaenet-i-ord-og-tal/ text/html #007 20141022150121927+211 sha1:3I42H3S6NNFQ2MSVX7XZKYAYSCX5QBYJ - -"
        };
        TestStructures.assertStringList(expectecCrawlLogTail, job.crawlLogTail);
        Object[][] expectedConfigFiles = {
                {
                    "warcWriter.defaultStorePaths[0]",
                    "warcs default store path",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/warcs",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/warcs",
                    false
                },
                {
                    "loggerModule.path",
                    "logs subdirectory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs",
                    false
                },
                {
                    "actionDirectory.actionDir",
                    "ActionDirectory source directory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/action",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/action",
                    false
                },
                {
                    "warcWriter.directory",
                    "writer base path",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736",
                    false
                },
                {
                    "statisticsTracker.reportsDir",
                    "reports subdirectory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/reports",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/reports",
                    false
                },
                {
                    "actionDirectory.doneDir",
                    "ActionDirectory done directory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/actions-done",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/actions-done",
                    false
                },
                {
                    "crawlController.scratchDir",
                    "scratch subdirectory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/scratch",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/scratch",
                    false
                },
                {
                    "loggerModule.uriErrorsLogPath",
                    "uri-errors.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/uri-errors.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/uri-errors.log",
                    false
                },
                {
                    "loggerModule.crawlLogPath",
                    "crawl.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/crawl.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/crawl.log",
                    false
                },
                {
                    "loggerModule.runtimeErrorsLogPath",
                    "runtime-errors.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/runtime-errors.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/runtime-errors.log",
                    false
                },
                {
                    "loggerModule.progressLogPath",
                    "progress-statistics.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/progress-statistics.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/progress-statistics.log",
                    false
                },
                {
                    "warcWriter.storePaths[0]",
                    "warcs default store path",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/warcs",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/warcs",
                    false
                },
                {
                    "acceptSurts.surtsDumpFile",
                    "surtsDumpFile",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/surts.dump",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/surts.dump",
                    false
                },
                {
                    "loggerModule.nonfatalErrorsLogPath",
                    "nonfatal-errors.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/nonfatal-errors.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/nonfatal-errors.log",
                    false
                },
                {
                    "checkpointService.checkpointsDir",
                    "checkpoints subdirectory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/checkpoints",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/checkpoints",
                    false
                },
                {
                    "loggerModule.alertsLogPath",
                    "alerts.log",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/alerts.log",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/logs/alerts.log",
                    false
                },
                {
                    "seeds.textSource",
                    "seeds.textSource",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/seeds.txt",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/seeds.txt",
                    true
                },
                {
                    "bdb.dir",
                    "bdbmodule subdirectory",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/state",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/state",
                    false
                },
                {
                    "org.archive.modules.deciderules.surt.SurtPrefixedDecideRule#367937cd.surtsDumpFile",
                    "surtsDumpFile",
                    "/home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/negative-surts.dump",
                    "https://192.168.1.101:6443/engine/job/1413988654119/engine/anypath//home/nicl/heritrix-3.2.0/jobs/1413988654119/20141022143736/negative-surts.dump",
                    false
                },
        };
        TestStructures.assertConfigFileList(expectedConfigFiles, job.configFiles);
        Assert.assertEquals(false, job.isLaunchInfoPartial);
        Assert.assertEquals(false, job.isRunning);
        Assert.assertEquals(false, job.isLaunchable);
        Assert.assertEquals(true, job.hasApplicationContext);
        Assert.assertEquals(new Integer(0), job.alertCount);
        String[] expectedCheckpointFiles = {
                ""
        };
        TestStructures.assertStringList(expectedCheckpointFiles, job.checkpointFiles);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/./jobs/1413988654119/20141022143736/logs/alerts.log", job.alertLogFilePath);
        Assert.assertEquals("/home/nicl/heritrix-3.2.0/./jobs/1413988654119/20141022143736/logs/crawl.log", job.crawlLogFilePath);
        String[][] expectedReports = {
                {
                    "CrawlSummaryReport",
                    "CrawlSummary"
                },
                {
                    "SeedsReport",
                    "Seeds"
                },
                {
                    "HostsReport",
                    "Hosts"
                },
                {
                    "SourceTagsReport",
                    "SourceTags"
                },
                {
                    "MimetypesReport",
                    "Mimetypes"
                },
                {
                    "ResponseCodeReport",
                    "ResponseCode"
                },
                {
                    "ProcessorsReport",
                    "Processors"
                },
                {
                    "FrontierSummaryReport",
                    "FrontierSummary"
                },
                {
                    "ToeThreadsReport",
                    "ToeThreads"
                },
        };
        TestStructures.assertReportsList(expectedReports, job.reports);
        HeapReport heapReport = job.heapReport;
        Assert.assertNotNull(heapReport);
        Assert.assertEquals(new Long(98881504), heapReport.usedBytes);
        Assert.assertEquals(new Long(238026752), heapReport.totalBytes);
        Assert.assertEquals(new Long(239075328), heapReport.maxBytes);
    }

}

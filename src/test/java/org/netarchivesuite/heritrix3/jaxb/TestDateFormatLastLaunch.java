package org.netarchivesuite.heritrix3.jaxb;

import java.text.DateFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.netarchivesuite.heritrix3.jaxb.DateFormatLastLaunch.LastLaunchAdadapter;

@RunWith(JUnit4.class)
public class TestDateFormatLastLaunch {

    protected DateFormatLastLaunch dfll1a;
    protected DateFormatLastLaunch dfll1b;
    protected DateFormatLastLaunch dfll2a;
    protected DateFormatLastLaunch dfll2b;

    protected AtomicInteger runs = new AtomicInteger(0);

    @Test
    public void test_dateformatlastlaunch() {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                dfll1a = DateFormatLastLaunch.DateParserTL.get();
                dfll1b = DateFormatLastLaunch.DateParserTL.get();
                runs.incrementAndGet();
                synchronized (TestDateFormatLastLaunch.class) {
                    TestDateFormatLastLaunch.class.notify();
                }
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            @Override
            public void run() {
                dfll2a = DateFormatLastLaunch.DateParserTL.get();
                dfll2b = DateFormatLastLaunch.DateParserTL.get();
                runs.incrementAndGet();
                synchronized (TestDateFormatLastLaunch.class) {
                    TestDateFormatLastLaunch.class.notify();
                }
            }
        };
        t2.start();
        try {
            while (runs.get() != 2) {
                synchronized (TestDateFormatLastLaunch.class) {
                    TestDateFormatLastLaunch.class.wait();
                }
            }
            Assert.assertNotNull(dfll1a);
            Assert.assertNotNull(dfll1b);
            Assert.assertNotNull(dfll2a);
            Assert.assertNotNull(dfll2b);
            Assert.assertTrue(dfll1a == dfll1b);
            Assert.assertTrue(dfll2a == dfll2b);
            Assert.assertFalse(dfll1a == dfll2a);
            Assert.assertFalse(dfll1b == dfll2b);
            DateFormat df1a = dfll1a.getDateFormat();
            DateFormat df1b = dfll1b.getDateFormat();
            DateFormat df2a = dfll2a.getDateFormat();
            DateFormat df2b = dfll2b.getDateFormat();
            Assert.assertNotNull(df1a);
            Assert.assertNotNull(df1b);
            Assert.assertNotNull(df2a);
            Assert.assertNotNull(df2b);
            Assert.assertTrue(df1a == df1b);
            Assert.assertTrue(df2a == df2b);
            Assert.assertFalse(df1a == df2a);
            Assert.assertFalse(df1b == df2b);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Unexpection exception!");
        }
        try {
            LastLaunchAdadapter adapter = new LastLaunchAdadapter();

            Assert.assertEquals("", adapter.marshal(null));
            Assert.assertNull(adapter.unmarshal(null));
            Assert.assertNull(adapter.unmarshal(""));

            Long ctm = System.currentTimeMillis();
            String dateStr = adapter.marshal(ctm);
            Long l = adapter.unmarshal(dateStr);
            Assert.assertEquals(ctm, l);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpection exception!");
        }
    }

}
